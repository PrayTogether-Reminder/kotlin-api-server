package site.praytogether.praytogetherapi.modules.auth.domain.service

interface OtpService {
    fun generateAndSendOtp(email: String): String
    fun verifyOtp(email: String, otp: String): Boolean
    fun deleteOtp(email: String)
}