package site.praytogether.praytogetherapi.common.exception.spec

import org.springframework.http.HttpStatus

enum class PrayerExceptionSpec(
    private val status: HttpStatus,
    private val code: String,
    private val debugMessage: String
) : ExceptionSpec {
    PRAYER_TITLE_NOT_FOUND(HttpStatus.NOT_FOUND, "PRAYER-001", "기도 제목을 찾을 수 없습니다.");

    override fun getStatus(): HttpStatus = status
    override fun getCode(): String = code
    override fun getDebugMessage(): String = debugMessage
}