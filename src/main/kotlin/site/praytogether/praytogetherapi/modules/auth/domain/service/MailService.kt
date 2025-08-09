package site.praytogether.praytogetherapi.modules.auth.domain.service

interface MailService {
    fun sendOtpEmail(email: String, otp: String)
}