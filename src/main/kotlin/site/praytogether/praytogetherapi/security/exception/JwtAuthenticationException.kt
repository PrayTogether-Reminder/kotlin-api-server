package site.praytogether.praytogetherapi.security.exception

import io.jsonwebtoken.JwtException
import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(
    val jwtException: JwtException
) : AuthenticationException(jwtException.message, jwtException)