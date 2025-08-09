package site.praytogether.praytogetherapi.modules.auth.infrastructure

import org.springframework.stereotype.Service
import site.praytogether.praytogetherapi.modules.auth.domain.cache.OtpCache
import site.praytogether.praytogetherapi.modules.auth.domain.service.MailService
import site.praytogether.praytogetherapi.modules.auth.domain.service.OtpService
import java.security.SecureRandom

@Service
class OtpServiceImpl(
    private val mailService: MailService,
    private val otpCache: OtpCache
) : OtpService {

    private val secureRandom = SecureRandom()

    override fun generateAndSendOtp(email: String): String {
        val otp = generateOtp()
        otpCache.put(email, otp)
        
        // Send OTP email
        mailService.sendOtpEmail(email, otp)
        
        return otp
    }

    override fun verifyOtp(email: String, otp: String): Boolean {
        return try {
            val cachedOtp = otpCache.get(email)
            cachedOtp == otp
        } catch (e: Exception) {
            false
        }
    }

    override fun deleteOtp(email: String) {
        otpCache.delete(email)
    }

    private fun generateOtp(): String {
        val otp = secureRandom.nextInt(900000) + 100000
        return otp.toString()
    }
}