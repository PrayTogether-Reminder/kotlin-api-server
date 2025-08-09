package site.praytogether.praytogetherapi.modules.auth.application.dto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val memberId: Long,
    val email: String
)

data class TokenRefreshResponse(
    val accessToken: String,
    val refreshToken: String
)