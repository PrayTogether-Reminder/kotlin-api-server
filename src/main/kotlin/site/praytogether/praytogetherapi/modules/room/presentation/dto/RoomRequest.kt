package site.praytogether.praytogetherapi.modules.room.presentation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateRoomRequest(
    @field:NotBlank(message = "Room name is required")
    @field:Size(max = 50, message = "Room name must be less than 50 characters")
    val name: String,

    @field:NotBlank(message = "Room description is required")
    @field:Size(max = 255, message = "Room description must be less than 255 characters")
    val description: String
)

data class UpdateRoomRequest(
    @field:Size(max = 50, message = "Room name must be less than 50 characters")
    val name: String? = null,

    @field:Size(max = 255, message = "Room description must be less than 255 characters")
    val description: String? = null
)