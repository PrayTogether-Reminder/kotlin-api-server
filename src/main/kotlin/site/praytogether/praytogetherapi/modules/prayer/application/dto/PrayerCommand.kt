package site.praytogether.praytogetherapi.modules.prayer.application.dto

data class CreatePrayerCommand(
    val title: String,
    val contents: List<String>,
    val roomId: Long,
    val memberId: Long
)

data class UpdatePrayerCommand(
    val prayerTitleId: Long,
    val title: String,
    val contents: List<PrayerContentUpdateCommand>
)

data class PrayerContentUpdateCommand(
    val id: Long? = null, // null for new content
    val memberId: Long,
    val content: String
)