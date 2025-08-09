package site.praytogether.praytogetherapi.modules.member.domain.exception

// Re-export common exceptions for backward compatibility
typealias MemberNotFoundException = site.praytogether.praytogetherapi.common.exception.MemberException
typealias MemberAlreadyExistException = site.praytogether.praytogetherapi.common.exception.MemberException

// Factory functions to create exceptions
fun memberNotFound(memberId: Long? = null, email: String? = null): MemberNotFoundException {
    return site.praytogether.praytogetherapi.common.exception.MemberException.memberNotFound(memberId, email)
}

fun memberAlreadyExists(email: String? = null): MemberAlreadyExistException {
    return site.praytogether.praytogetherapi.common.exception.MemberException.memberAlreadyExists(email)
}