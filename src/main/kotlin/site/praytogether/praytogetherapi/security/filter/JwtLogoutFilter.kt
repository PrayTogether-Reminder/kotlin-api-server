package site.praytogether.praytogetherapi.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import site.praytogether.praytogetherapi.modules.auth.infrastructure.RefreshTokenServiceImpl
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal

class JwtLogoutFilter(
    private val refreshTokenService: RefreshTokenServiceImpl
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val LOGOUT_URL = "/api/auth/logout"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null) {
            response.status = HttpServletResponse.SC_NO_CONTENT
            return
        }
        
        val principal = authentication.principal as PrayTogetherPrincipal
        logger.info("로그아웃 ${principal.id}")
        logger.info("로그아웃 인증 정보 = $authentication")
        
        refreshTokenService.delete(principal.id.toString())
        response.status = HttpServletResponse.SC_NO_CONTENT
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath != LOGOUT_URL
    }
}