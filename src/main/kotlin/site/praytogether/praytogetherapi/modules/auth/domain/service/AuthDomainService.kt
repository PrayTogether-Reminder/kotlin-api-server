package site.praytogether.praytogetherapi.modules.auth.domain.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.common.exception.AuthException
import site.praytogether.praytogetherapi.modules.auth.domain.valueobject.JwtTokenPair
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.member.domain.repository.MemberRepository

@Service
@Transactional(readOnly = true)
class AuthDomainService(
    private val memberRepository: MemberRepository,
    private val jwtTokenService: JwtTokenService,
    private val refreshTokenService: RefreshTokenService,
    private val otpService: OtpService,
    private val passwordEncoder: PasswordEncoder
) {

    fun authenticateMember(email: String, password: String): Member {
        val member = memberRepository.findByEmail(email)
            ?: throw AuthException.invalidCredentials(email)

        if (!passwordEncoder.matches(password, member.password)) {
            throw AuthException.invalidCredentials(email)
        }

        return member
    }

    @Transactional
    fun generateAndStoreTokens(memberId: Long, email: String): JwtTokenPair {
        val tokenPair = jwtTokenService.generateTokenPair(memberId, email)
        refreshTokenService.save(memberId.toString(), tokenPair.refreshToken)
        return tokenPair
    }

    @Transactional
    fun refreshTokenPair(refreshToken: String): JwtTokenPair {
        val tokenPayload = jwtTokenService.validateToken(refreshToken)
            ?: throw AuthException.tokenInvalid()

        // Validate refresh token exists in cache
        refreshTokenService.validateRefreshTokenExist(tokenPayload.userId.toString(), refreshToken)

        // Remove old refresh token
        refreshTokenService.delete(tokenPayload.userId.toString())

        // Generate new token pair
        val newTokenPair = jwtTokenService.generateTokenPair(tokenPayload.userId, tokenPayload.email)
        
        // Store new refresh token
        refreshTokenService.save(tokenPayload.userId.toString(), newTokenPair.refreshToken)

        return newTokenPair
    }

    @Transactional
    fun logout(memberId: Long) {
        refreshTokenService.delete(memberId.toString())
    }

    @Transactional
    fun sendOtp(email: String) {
        otpService.generateAndSendOtp(email)
    }

    fun verifyOtp(email: String, otp: String): Boolean {
        return otpService.verifyOtp(email, otp)
    }

    @Transactional
    fun withdrawMember(memberId: Long) {
        // Delete refresh token first
        refreshTokenService.delete(memberId.toString())
        
        // Delete member (this will cascade delete related data based on JPA configuration)
        memberRepository.deleteById(memberId)
    }
}