package site.praytogether.praytogetherapi.modules.prayer.application.dto

import java.time.Instant

data class PrayerTitleQuery(
    val roomId: Long,
    val after: Long = 0,
    val limit: Int = 10
)

data class PrayerTitleResponse(
    val id: Long,
    val title: String,
    val memberId: Long,
    val createdAt: Instant?
)

data class PrayerTitleScrollResponse(
    val prayers: List<PrayerTitleResponse>,
    val hasNext: Boolean,
    val nextCursor: String?
)

data class PrayerContentResponse(
    val id: Long,
    val title: String,
    val contents: List<String>,
    val memberId: Long,
    val createdAt: Instant?
)