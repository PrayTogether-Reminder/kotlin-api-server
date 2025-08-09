package site.praytogether.praytogetherapi.modules.auth.application

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.auth.application.dto.*
import site.praytogether.praytogetherapi.modules.auth.domain.service.AuthDomainService
import site.praytogether.praytogetherapi.modules.member.domain.service.MemberDomainService

@Service
@Transactional
class AuthApplicationService(
    private val authDomainService: AuthDomainService,
    private val memberDomainService: MemberDomainService,
    private val passwordEncoder: PasswordEncoder
) {

    fun login(command: LoginCommand): LoginResponse {
        val member = authDomainService.authenticateMember(command.email, command.password)
        val tokenPair = authDomainService.generateAndStoreTokens(member.id!!, member.email)

        return LoginResponse(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken,
            memberId = member.id!!,
            email = member.email
        )
    }

    fun signup(command: SignupCommand): Long {
        val encodedPassword = passwordEncoder.encode(command.password)
        val member = memberDomainService.createMember(command.name, command.email, encodedPassword)
        return member.id!!
    }

    fun sendOtp(command: SendOtpCommand) {
        authDomainService.sendOtp(command.email)
    }

    fun verifyOtp(command: VerifyOtpCommand): Boolean {
        return authDomainService.verifyOtp(command.email, command.otp)
    }

    fun refreshToken(command: RefreshTokenCommand): TokenRefreshResponse {
        val newTokenPair = authDomainService.refreshTokenPair(command.refreshToken)

        return TokenRefreshResponse(
            accessToken = newTokenPair.accessToken,
            refreshToken = newTokenPair.refreshToken
        )
    }

    fun logout(memberId: Long) {
        authDomainService.logout(memberId)
    }

    fun withdraw(memberId: Long) {
        authDomainService.withdrawMember(memberId)
    }
}