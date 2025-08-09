package site.praytogether.praytogetherapi.common.exception.spec

import org.springframework.http.HttpStatus

enum class AuthExceptionSpec(
    private val status: HttpStatus,
    private val code: String,
    private val debugMessage: String
) : ExceptionSpec {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-001", "잘못된 인증 정보입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-002", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-003", "유효하지 않은 토큰입니다."),
    OTP_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-004", "OTP를 찾을 수 없거나 만료되었습니다."),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH-005", "OTP가 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-006", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_VALID(HttpStatus.UNAUTHORIZED, "AUTH-007", "유효하지 않은 리프레시 토큰입니다."),
    OTP_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-008", "OTP 전송에 실패했습니다."),
    OTP_TEMPLATE_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-009", "OTP 템플릿 로드에 실패했습니다."),
    
    // JWT 관련 예외
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-010", "액세스 토큰이 만료되었습니다."),
    ACCESS_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "AUTH-011", "액세스 토큰 처리 중 오류가 발생했습니다."),
    AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, "AUTH-012", "인증에 실패했습니다."),
    UNKNOWN_AUTHENTICATION_FAILURE(HttpStatus.UNAUTHORIZED, "AUTH-013", "알 수 없는 인증 실패"),
    INCORRECT_EMAIL_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH-014", "이메일 또는 비밀번호가 일치하지 않습니다.");

    override fun getStatus(): HttpStatus = status
    override fun getCode(): String = code
    override fun getDebugMessage(): String = debugMessage
}