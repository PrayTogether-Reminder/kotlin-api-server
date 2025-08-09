package site.praytogether.praytogetherapi.common.exception.spec

import org.springframework.http.HttpStatus

enum class RoomExceptionSpec(
    private val status: HttpStatus,
    private val code: String,
    private val debugMessage: String
) : ExceptionSpec {
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ROOM-001", "방을 찾을 수 없습니다.");

    override fun getStatus(): HttpStatus = status
    override fun getCode(): String = code
    override fun getDebugMessage(): String = debugMessage
}