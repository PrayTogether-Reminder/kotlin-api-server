package site.praytogether.praytogetherapi.modules.room.application.dto

import java.time.Instant

data class RoomResponse(
    val id: Long,
    val name: String,
    val description: String
)

data class RoomListResponse(
    val rooms: List<RoomResponse>
)

data class RoomInfiniteScrollQuery(
    val memberId: Long,
    val orderBy: String = "time",
    val after: String = "0", 
    val dir: String = "desc"
)