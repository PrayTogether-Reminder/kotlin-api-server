package site.praytogether.praytogetherapi.modules.prayer.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class PrayerContentsResponse(
    @JsonProperty("prayerContents")
    val prayerContents: List<PrayerContentInfo> = emptyList()
)

data class PrayerContentInfo(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("content")
    val content: String,
    @JsonProperty("memberId")
    val memberId: Long,
    @JsonProperty("memberName")
    val memberName: String,
    @JsonProperty("createdTime")
    val createdTime: Instant
)