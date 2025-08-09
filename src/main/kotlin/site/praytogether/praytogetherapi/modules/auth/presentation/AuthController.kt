package site.praytogether.praytogetherapi.modules.auth.presentation

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import site.praytogether.praytogetherapi.modules.auth.application.AuthApplicationService
import site.praytogether.praytogetherapi.common.annotation.PrincipalId
import site.praytogether.praytogetherapi.modules.auth.application.dto.*
import site.praytogether.praytogetherapi.modules.auth.presentation.dto.*
import site.praytogether.praytogetherapi.common.dto.MessageResponse

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authApplicationService: AuthApplicationService
) {

    // Login is handled by JwtAuthFilter in Spring Security filter chain

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<MessageResponse> {
        val command = SignupCommand(
            name = request.name,
            email = request.email,
            password = request.password
        )
        
        authApplicationService.signup(command)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(MessageResponse("User registered successfully"))
    }

    @PostMapping("/send-otp")
    fun sendOtp(@Valid @RequestBody request: OtpRequest): ResponseEntity<MessageResponse> {
        val command = SendOtpCommand(email = request.email)
        authApplicationService.sendOtp(command)
        return ResponseEntity.ok(MessageResponse("OTP sent successfully"))
    }

    @PostMapping("/verify-otp")
    fun verifyOtp(@Valid @RequestBody request: OtpVerifyRequest): ResponseEntity<MessageResponse> {
        val command = VerifyOtpCommand(
            email = request.email,
            otp = request.otp
        )
        
        val isValid = authApplicationService.verifyOtp(command)
        return if (isValid) {
            ResponseEntity.ok(MessageResponse("OTP verified successfully"))
        } else {
            ResponseEntity.badRequest().body(MessageResponse("Invalid OTP"))
        }
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<TokenRefreshResponse> {
        val command = RefreshTokenCommand(refreshToken = request.refreshToken)
        val response = authApplicationService.refreshToken(command)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/withdraw")
    fun withdraw(@PrincipalId memberId: Long): ResponseEntity<MessageResponse> {
        authApplicationService.withdraw(memberId)
        return ResponseEntity.ok(MessageResponse("회원 탈퇴를 완료했습니다.\n함께 기도해 주셔 감사합니다."))
    }
}