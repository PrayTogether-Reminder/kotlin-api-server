package site.praytogether.praytogetherapi.modules.memberroom.domain.exception

// Re-export common exceptions for backward compatibility
typealias MemberRoomNotFoundException = site.praytogether.praytogetherapi.common.exception.MemberRoomException
typealias MemberRoomAlreadyExistsException = site.praytogether.praytogetherapi.common.exception.MemberRoomException

// Factory functions to create exceptions
fun memberRoomNotFound(memberId: Long? = null, roomId: Long? = null): MemberRoomNotFoundException {
    return site.praytogether.praytogetherapi.common.exception.MemberRoomException.memberRoomNotFound(memberId, roomId)
}

fun memberRoomAlreadyExists(memberId: Long? = null, roomId: Long? = null): MemberRoomAlreadyExistsException {
    return site.praytogether.praytogetherapi.common.exception.MemberRoomException.memberRoomAlreadyExists(memberId, roomId)
}

// Keep old exception classes for backward compatibility where no common equivalent exists
class MemberNotInRoomException(message: String) : RuntimeException(message)