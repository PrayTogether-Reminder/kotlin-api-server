package site.praytogether.praytogetherapi.common.exception.spec

import org.springframework.http.HttpStatus

enum class MemberRoomExceptionSpec(
    private val status: HttpStatus,
    private val code: String,
    private val debugMessage: String
) : ExceptionSpec {
    MEMBER_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_ROOM-001", "회원이 속한 방을 찾을 수 없습니다."),
    MEMBER_ROOM_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "MEMBER_ROOM-002", "이미 방에 존재하는 회원입니다."),
    MEMBER_NOT_IN_ROOM(HttpStatus.FORBIDDEN, "MEMBER_ROOM-003", "방에 속하지 않은 사용자입니다.");

    override fun getStatus(): HttpStatus = status
    override fun getCode(): String = code
    override fun getDebugMessage(): String = debugMessage
}