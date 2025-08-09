package site.praytogether.praytogetherapi.common.exception

import site.praytogether.praytogetherapi.common.exception.spec.AuthExceptionSpec

class AuthException(
    spec: AuthExceptionSpec,
    field: ExceptionField
) : BaseException(spec, field) {

    override fun getClientMessage(): String {
        return when (getExceptionSpec() as AuthExceptionSpec) {
            AuthExceptionSpec.INVALID_CREDENTIALS -> "잘못된 인증 정보입니다."
            AuthExceptionSpec.TOKEN_EXPIRED -> "토큰이 만료되었습니다."
            AuthExceptionSpec.TOKEN_INVALID -> "유효하지 않은 토큰입니다."
            AuthExceptionSpec.OTP_NOT_FOUND -> "OTP를 찾을 수 없거나 만료되었습니다."
            AuthExceptionSpec.OTP_EXPIRED -> "OTP가 만료되었습니다."
            AuthExceptionSpec.REFRESH_TOKEN_NOT_FOUND -> "리프레시 토큰을 찾을 수 없습니다."
            AuthExceptionSpec.REFRESH_TOKEN_NOT_VALID -> "유효하지 않은 리프레시 토큰입니다."
            AuthExceptionSpec.OTP_SEND_FAILED -> "OTP 전송에 실패했습니다."
            AuthExceptionSpec.OTP_TEMPLATE_LOAD_FAILED -> "OTP 템플릿 로드에 실패했습니다."
            AuthExceptionSpec.ACCESS_TOKEN_EXPIRED -> "액세스 토큰이 만료되었습니다."
            AuthExceptionSpec.ACCESS_TOKEN_EXCEPTION -> "액세스 토큰 처리 중 오류가 발생했습니다."
            AuthExceptionSpec.AUTHENTICATION_FAIL -> "인증에 실패했습니다."
            AuthExceptionSpec.UNKNOWN_AUTHENTICATION_FAILURE -> "알 수 없는 인증 실패"
            AuthExceptionSpec.INCORRECT_EMAIL_PASSWORD -> "이메일 또는 비밀번호가 일치하지 않습니다."
        }
    }

    companion object {
        fun invalidCredentials(email: String? = null): AuthException {
            val fieldBuilder = ExceptionField.builder()
            email?.let { fieldBuilder.add("email", it) }
            return AuthException(AuthExceptionSpec.INVALID_CREDENTIALS, fieldBuilder.build())
        }

        fun tokenExpired(): AuthException {
            return AuthException(AuthExceptionSpec.TOKEN_EXPIRED, ExceptionField.builder().build())
        }

        fun tokenInvalid(): AuthException {
            return AuthException(AuthExceptionSpec.TOKEN_INVALID, ExceptionField.builder().build())
        }

        fun otpNotFound(email: String? = null): AuthException {
            val fieldBuilder = ExceptionField.builder()
            email?.let { fieldBuilder.add("email", it) }
            return AuthException(AuthExceptionSpec.OTP_NOT_FOUND, fieldBuilder.build())
        }

        fun otpExpired(email: String? = null): AuthException {
            val fieldBuilder = ExceptionField.builder()
            email?.let { fieldBuilder.add("email", it) }
            return AuthException(AuthExceptionSpec.OTP_EXPIRED, fieldBuilder.build())
        }

        fun refreshTokenNotFound(email: String? = null): AuthException {
            val fieldBuilder = ExceptionField.builder()
            email?.let { fieldBuilder.add("email", it) }
            return AuthException(AuthExceptionSpec.REFRESH_TOKEN_NOT_FOUND, fieldBuilder.build())
        }

        fun refreshTokenNotValid(token: String? = null): AuthException {
            val fieldBuilder = ExceptionField.builder()
            token?.let { fieldBuilder.add("token", it.take(10) + "...") }
            return AuthException(AuthExceptionSpec.REFRESH_TOKEN_NOT_VALID, fieldBuilder.build())
        }

        fun otpSendFailed(email: String? = null, reason: String? = null): AuthException {
            val fieldBuilder = ExceptionField.builder()
            email?.let { fieldBuilder.add("email", it) }
            reason?.let { fieldBuilder.add("reason", it) }
            return AuthException(AuthExceptionSpec.OTP_SEND_FAILED, fieldBuilder.build())
        }

        fun otpTemplateLoadFailed(reason: String? = null): AuthException {
            val fieldBuilder = ExceptionField.builder()
            reason?.let { fieldBuilder.add("reason", it) }
            return AuthException(AuthExceptionSpec.OTP_TEMPLATE_LOAD_FAILED, fieldBuilder.build())
        }
    }
}