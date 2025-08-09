package site.praytogether.praytogetherapi.modules.auth.domain.valueobject

data class JwtTokenPair(
    val accessToken: String,
    val refreshToken: String
)

data class TokenPayload(
    val userId: Long,
    val email: String,
    val tokenType: String
)