package site.praytogether.praytogetherapi.modules.prayer.domain.model

import java.time.Instant

data class PrayerTitleInfo(
    val id: Long,
    val title: String,
    val createdTime: Instant
)