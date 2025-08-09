package site.praytogether.praytogetherapi.common.exception.spec

import org.springframework.http.HttpStatus

enum class InvitationExceptionSpec(
    private val status: HttpStatus,
    private val code: String,
    private val debugMessage: String
) : ExceptionSpec {
    INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITATION-001", "방 초대장을 찾을 수 없습니다."),
    ALREADY_RESPONDED_INVITATION(HttpStatus.BAD_REQUEST, "INVITATION-002", "이미 응답 설정(ACCEPT/REJECT)이 된 방 초대장 입니다."),
    INVITATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "INVITATION-003", "이미 방에 대한 초대장이 존재합니다."),
    MEMBER_ALREADY_IN_ROOM(HttpStatus.CONFLICT, "INVITATION-004", "이미 방에 속한 사용자입니다.");

    override fun getStatus(): HttpStatus = status
    override fun getCode(): String = code
    override fun getDebugMessage(): String = debugMessage
}