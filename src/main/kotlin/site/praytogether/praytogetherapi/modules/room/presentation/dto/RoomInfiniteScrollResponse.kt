package site.praytogether.praytogetherapi.modules.room.presentation.dto

import java.time.Instant

data class RoomInfiniteScrollResponse(
    val rooms: List<RoomInfo>
)

data class RoomInfo(
    val id: Long,
    val name: String,
    val description: String,
    val joinedTime: Instant,
    val memberCount: Int
)