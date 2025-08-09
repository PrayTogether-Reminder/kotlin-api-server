package site.praytogether.praytogetherapi.modules.prayer.presentation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreatePrayerRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    val title: String,

    @field:NotEmpty(message = "At least one content is required")
    val contents: List<String>,

    @field:NotNull(message = "Room ID is required")
    val roomId: Long
)

data class UpdatePrayerRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    val title: String,

    @field:NotEmpty(message = "At least one content is required")
    val contents: List<String>
)

data class CreatePrayerResponse(
    val prayerId: Long
)