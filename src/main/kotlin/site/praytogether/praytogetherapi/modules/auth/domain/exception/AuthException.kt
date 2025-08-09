package site.praytogether.praytogetherapi.modules.auth.domain.exception

// Re-export common exceptions for backward compatibility
typealias InvalidCredentialsException = site.praytogether.praytogetherapi.common.exception.AuthException
typealias TokenExpiredException = site.praytogether.praytogetherapi.common.exception.AuthException
typealias TokenInvalidException = site.praytogether.praytogetherapi.common.exception.AuthException
typealias OtpNotFoundException = site.praytogether.praytogetherapi.common.exception.AuthException
typealias OtpExpiredException = site.praytogether.praytogetherapi.common.exception.AuthException
typealias RefreshTokenNotFoundException = site.praytogether.praytogetherapi.common.exception.AuthException

// Factory functions to create exceptions
fun invalidCredentials(email: String? = null): InvalidCredentialsException {
    return site.praytogether.praytogetherapi.common.exception.AuthException.invalidCredentials(email)
}

fun tokenExpired(): TokenExpiredException {
    return site.praytogether.praytogetherapi.common.exception.AuthException.tokenExpired()
}

fun tokenInvalid(): TokenInvalidException {
    return site.praytogether.praytogetherapi.common.exception.AuthException.tokenInvalid()
}

fun otpNotFound(email: String? = null): OtpNotFoundException {
    return site.praytogether.praytogetherapi.common.exception.AuthException.otpNotFound(email)
}

fun otpExpired(email: String? = null): OtpExpiredException {
    return site.praytogether.praytogetherapi.common.exception.AuthException.otpExpired(email)
}

fun refreshTokenNotFound(email: String? = null): RefreshTokenNotFoundException {
    return site.praytogether.praytogetherapi.common.exception.AuthException.refreshTokenNotFound(email)
}