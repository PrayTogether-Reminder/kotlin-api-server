package site.praytogether.praytogetherapi.modules.auth.domain.service

import site.praytogether.praytogetherapi.modules.auth.domain.valueobject.JwtTokenPair
import site.praytogether.praytogetherapi.modules.auth.domain.valueobject.TokenPayload

interface JwtTokenService {
    fun generateTokenPair(userId: Long, email: String): JwtTokenPair
    fun validateToken(token: String): TokenPayload?
    fun extractTokenFromHeader(authorizationHeader: String?): String?
}