package site.praytogether.praytogetherapi.modules.prayer.presentation.dto

import jakarta.validation.constraints.NotNull

data class PrayerCompletionCreateRequest(
    @field:NotNull(message = "Room ID is required")
    val roomId: Long
)