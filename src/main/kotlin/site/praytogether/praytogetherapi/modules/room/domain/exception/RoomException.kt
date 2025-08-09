package site.praytogether.praytogetherapi.modules.room.domain.exception

// Re-export common exceptions for backward compatibility
typealias RoomNotFoundException = site.praytogether.praytogetherapi.common.exception.RoomException

// Factory functions to create exceptions
fun roomNotFound(roomId: Long? = null): RoomNotFoundException {
    return site.praytogether.praytogetherapi.common.exception.RoomException.roomNotFound(roomId)
}

// Keep the old class names for backward compatibility
class RoomAlreadyExistException(message: String = "Room already exists") : RuntimeException(message)
class RoomAccessDeniedException(message: String = "Access denied to room") : RuntimeException(message)