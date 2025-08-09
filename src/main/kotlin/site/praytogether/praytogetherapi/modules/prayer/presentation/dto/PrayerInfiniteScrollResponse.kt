package site.praytogether.praytogetherapi.modules.prayer.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class PrayerTitleInfiniteScrollResponse(
    @JsonProperty("prayerTitles")
    val prayerTitles: List<PrayerTitleInfo>
)

data class PrayerTitleInfo(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("memberId")
    val memberId: Long,
    @JsonProperty("memberName")
    val memberName: String,
    @JsonProperty("createdTime")
    val createdTime: Instant,
    @JsonProperty("contentCount")
    val contentCount: Int
)