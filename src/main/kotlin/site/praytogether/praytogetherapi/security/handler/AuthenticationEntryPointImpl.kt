package site.praytogether.praytogetherapi.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import site.praytogether.praytogetherapi.common.exception.ExceptionResponse
import site.praytogether.praytogetherapi.common.exception.spec.AuthExceptionSpec

@Component
class AuthenticationEntryPointImpl(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.status = HttpStatus.UNAUTHORIZED.value()

        when {
            authException.cause == null -> {
                // 인증이 없는 요청시 예외 발생
                setUnAuthorizationExceptionResponse(response)
            }
            authException.cause is JwtException -> {
                // JWT 관련 예외 발생
                logger.error("인증 실패 예외 타입={}", authException.cause!!.javaClass)
                setJwtExceptionResponse(response, authException.cause as JwtException)
            }
            else -> {
                // 알 수 없는 예외 발생
                setDefaultResponse(response)
            }
        }
    }

    private fun setUnAuthorizationExceptionResponse(response: HttpServletResponse) {
        logger.error("인증되지 않은 요청입니다.")
        val exceptionResponse = ExceptionResponse.of(
            AuthExceptionSpec.AUTHENTICATION_FAIL.getStatus().value(),
            AuthExceptionSpec.AUTHENTICATION_FAIL.getCode(),
            "로그인을 해주세요."
        )
        response.writer.write(objectMapper.writeValueAsString(exceptionResponse))
    }

    private fun setDefaultResponse(response: HttpServletResponse) {
        val errorResponse = ExceptionResponse.of(
            AuthExceptionSpec.UNKNOWN_AUTHENTICATION_FAILURE.getStatus().value(),
            AuthExceptionSpec.UNKNOWN_AUTHENTICATION_FAILURE.getCode(),
            "알 수 없는 이유로 인증에 실패했습니다."
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

    private fun setJwtExceptionResponse(response: HttpServletResponse, e: JwtException) {
        val message = findJwtExceptionMessage(e)
        
        val (status, code) = when (e) {
            is ExpiredJwtException -> {
                AuthExceptionSpec.ACCESS_TOKEN_EXPIRED.getStatus().value() to 
                AuthExceptionSpec.ACCESS_TOKEN_EXPIRED.getCode()
            }
            else -> {
                AuthExceptionSpec.ACCESS_TOKEN_EXCEPTION.getStatus().value() to
                AuthExceptionSpec.ACCESS_TOKEN_EXCEPTION.getCode()
            }
        }
        
        response.status = status
        val errorResponse = ExceptionResponse.of(status, code, message)
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

    private fun findJwtExceptionMessage(jwtException: JwtException): String {
        return when (jwtException) {
            is ExpiredJwtException -> {
                logger.error("토큰 기간이 만료됐습니다.")
                "토큰 기간이 만료됐습니다."
            }
            is MalformedJwtException -> {
                logger.error("토큰 구조가 올바르지 않습니다.")
                "유효하지 않은 토큰입니다."
            }
            is SignatureException -> {
                logger.error("토큰의 서명이 올바르지 않습니다.")
                "유효하지 않은 토큰입니다."
            }
            is UnsupportedJwtException -> {
                logger.error("지원하지 않는 JWT 토큰입니다.")
                "유효하지 않은 토큰입니다."
            }
            else -> {
                logger.warn("정의 되지 않은 JWT 예외 type: {}", jwtException.javaClass.simpleName)
                "유효하지 않은 토큰입니다."
            }
        }
    }
}