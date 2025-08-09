package site.praytogether.praytogetherapi.modules.prayer.domain.model

data class PrayerContentInfo(
    val id: Long,
    val memberId: Long,
    val memberName: String,
    val content: String
)