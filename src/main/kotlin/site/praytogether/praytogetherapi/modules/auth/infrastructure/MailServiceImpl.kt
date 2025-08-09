package site.praytogether.praytogetherapi.modules.auth.infrastructure

import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import site.praytogether.praytogetherapi.modules.auth.domain.exception.OtpSendFailException
import site.praytogether.praytogetherapi.modules.auth.domain.exception.OtpTemplateLoadFailException
import site.praytogether.praytogetherapi.modules.auth.domain.service.MailService
import java.io.IOException
import java.util.*

@Service
class MailServiceImpl(
    private val mailSender: JavaMailSender
) : MailService {

    @Value("\${spring.mail.username}")
    private lateinit var prayTogetherEmail: String

    companion object {
        private const val EMAIL_OTP_TEMPLATE_PATH = "templates/email/otp.html"
    }

    override fun sendOtpEmail(email: String, otp: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        
        try {
            val helper = MimeMessageHelper(message, false, "UTF-8")
            setMessageContent(helper, email, otp)
            mailSender.send(message)
        } catch (e: MessagingException) {
            throw OtpSendFailException("Failed to send OTP email to $email")
        }
    }

    private fun setMessageContent(helper: MimeMessageHelper, email: String, otp: String) {
        helper.setFrom(prayTogetherEmail)
        helper.setTo(email)
        helper.setSubject("기도함께 이메일 인증번호")
        helper.setSentDate(Date())

        val emailTemplate = loadEmailTemplate()
        val emailContent = emailTemplate.format(otp)

        helper.setText(emailContent, true)
    }

    private fun loadEmailTemplate(): String {
        return try {
            val resource = ClassPathResource(EMAIL_OTP_TEMPLATE_PATH)
            val templateBytes = FileCopyUtils.copyToByteArray(resource.inputStream)
            String(templateBytes, Charsets.UTF_8)
        } catch (e: IOException) {
            throw OtpTemplateLoadFailException("Failed to load email template from $EMAIL_OTP_TEMPLATE_PATH")
        }
    }
}