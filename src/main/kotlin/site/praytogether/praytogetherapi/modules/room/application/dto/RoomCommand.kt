package site.praytogether.praytogetherapi.modules.room.application.dto

data class CreateRoomCommand(
    val name: String,
    val description: String,
    val creatorId: Long
)

data class UpdateRoomCommand(
    val roomId: Long,
    val name: String? = null,
    val description: String? = null
)