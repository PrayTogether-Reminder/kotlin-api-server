package site.praytogether.praytogetherapi.common.exception.spec

import org.springframework.http.HttpStatus

enum class MemberExceptionSpec(
    private val status: HttpStatus,
    private val code: String,
    private val debugMessage: String
) : ExceptionSpec {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-001", "회원 정보를 찾을 수 없습니다."),
    MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "MEMBER-002", "이미 존재하는 회원 입니다.");

    override fun getStatus(): HttpStatus = status
    override fun getCode(): String = code
    override fun getDebugMessage(): String = debugMessage
}