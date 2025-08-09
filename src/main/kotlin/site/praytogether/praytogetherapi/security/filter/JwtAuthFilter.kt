package site.praytogether.praytogetherapi.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import site.praytogether.praytogetherapi.common.exception.ExceptionResponse
import site.praytogether.praytogetherapi.common.exception.spec.AuthExceptionSpec
import site.praytogether.praytogetherapi.modules.auth.presentation.dto.LoginRequest
import site.praytogether.praytogetherapi.modules.auth.application.dto.LoginResponse
import site.praytogether.praytogetherapi.modules.auth.infrastructure.RefreshTokenServiceImpl
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal
import site.praytogether.praytogetherapi.security.service.JwtService
import java.io.IOException
import java.nio.charset.StandardCharsets

class JwtAuthFilter(
    private val authenticationManager: AuthenticationManager,
    private val objectMapper: ObjectMapper,
    private val refreshTokenService: RefreshTokenServiceImpl,
    private val jwtService: JwtService
) : UsernamePasswordAuthenticationFilter() {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val LOGIN_URL = "/api/auth/login"

    init {
        setFilterProcessesUrl(LOGIN_URL)
    }

    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication {
        try {
            val dto = objectMapper.readValue(request.inputStream, LoginRequest::class.java)
            logger.info("[API] 로그인 요청 시도 ${dto.email}")
            return authenticationManager.authenticate(createAuthToken(dto))
        } catch (e: IOException) {
            logger.error("[API] 로그인 요청 parse 오류", e)
            throw RuntimeException("[API] 로그인 요청 parse 오류", e)
        }
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        logger.error("[API] 로그인 실패 : ${failed.message}")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.characterEncoding = StandardCharsets.UTF_8.name()
        
        val exceptionResponse = ExceptionResponse.of(
            AuthExceptionSpec.INCORRECT_EMAIL_PASSWORD.getStatus().value(),
            AuthExceptionSpec.INCORRECT_EMAIL_PASSWORD.getCode(),
            "이메일 또는 비밀번호가 일치하지 않습니다."
        )
        
        response.writer.write(objectMapper.writeValueAsString(exceptionResponse))
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val principal = authResult.principal as PrayTogetherPrincipal
        val accessToken = jwtService.issueAccessToken(principal)
        val refreshToken = jwtService.issueRefreshToken(principal)
        refreshTokenService.save(principal.id.toString(), refreshToken)

        val loginResponse = LoginResponse(
            memberId = principal.id,
            email = principal.email,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
        
        response.writer.write(objectMapper.writeValueAsString(loginResponse))
        response.contentType = "application/json"
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.status = HttpServletResponse.SC_OK
        logger.info("[API] 로그인 요청 종료 ${principal.email}")
    }

    private fun createAuthToken(request: LoginRequest): UsernamePasswordAuthenticationToken {
        return UsernamePasswordAuthenticationToken(request.email, request.password)
    }
}