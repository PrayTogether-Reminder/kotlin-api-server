package site.praytogether.praytogetherapi.modules.auth.application.dto

data class LoginCommand(
    val email: String,
    val password: String
)

data class SignupCommand(
    val name: String,
    val email: String,
    val password: String
)

data class SendOtpCommand(
    val email: String
)

data class VerifyOtpCommand(
    val email: String,
    val otp: String
)

data class RefreshTokenCommand(
    val refreshToken: String
)