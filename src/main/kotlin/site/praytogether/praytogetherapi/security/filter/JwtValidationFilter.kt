package site.praytogether.praytogetherapi.security.filter

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.filter.OncePerRequestFilter
import site.praytogether.praytogetherapi.common.constant.CoreConstant.JwtConstant.HTTP_HEADER_AUTH_BEARER
import site.praytogether.praytogetherapi.common.constant.CoreConstant.JwtConstant.HTTP_HEADER_AUTHORIZATION
import site.praytogether.praytogetherapi.security.exception.JwtAuthenticationException
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal
import site.praytogether.praytogetherapi.security.service.JwtService

class JwtValidationFilter(
    private val jwtService: JwtService,
    private val authenticationEntryPoint: AuthenticationEntryPoint
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractTokenFrom(request)
        
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }
        
        try {
            jwtService.isValid(token)
            setAuthentication(token)
            filterChain.doFilter(request, response)
        } catch (e: JwtException) {
            logger.error("JWT 예외 발생 : ${e.message}")
            authenticationEntryPoint.commence(request, response, JwtAuthenticationException(e))
        }
    }

    private fun extractTokenFrom(request: HttpServletRequest): String? {
        val value = request.getHeader(HTTP_HEADER_AUTHORIZATION)
        if (value == null || !value.contains(HTTP_HEADER_AUTH_BEARER)) {
            return null
        }
        return value.substring(HTTP_HEADER_AUTH_BEARER.length)
    }

    private fun setAuthentication(token: String) {
        val authentication = getAuthentication(token)
        SecurityContextHolder.getContext().authentication = authentication
        logger.info("인증 정보 구성 완료 : ${authentication.principal}")
    }

    private fun getAuthentication(token: String): Authentication {
        val principal = jwtService.extractPrincipal(token)
        return UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
    }
}