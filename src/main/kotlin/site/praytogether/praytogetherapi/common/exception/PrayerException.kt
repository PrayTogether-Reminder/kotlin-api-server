package site.praytogether.praytogetherapi.common.exception

import site.praytogether.praytogetherapi.common.exception.spec.PrayerExceptionSpec

class PrayerException(
    spec: PrayerExceptionSpec,
    field: ExceptionField
) : BaseException(spec, field) {

    override fun getClientMessage(): String {
        return when (getExceptionSpec() as PrayerExceptionSpec) {
            PrayerExceptionSpec.PRAYER_TITLE_NOT_FOUND -> "기도 제목을 찾을 수 없습니다."
        }
    }

    companion object {
        fun prayerTitleNotFound(prayerTitleId: Long? = null): PrayerException {
            val fieldBuilder = ExceptionField.builder()
            prayerTitleId?.let { fieldBuilder.add("prayerTitleId", it) }
            return PrayerException(PrayerExceptionSpec.PRAYER_TITLE_NOT_FOUND, fieldBuilder.build())
        }
    }
}