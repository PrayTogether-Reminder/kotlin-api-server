package site.praytogether.praytogetherapi.modules.room.presentation.dto

import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole

data class RoomMembersResponse(
    val roomId: Long,
    val members: List<RoomMemberDto>
)

data class RoomMemberDto(
    val memberId: Long,
    val memberName: String,
    val role: RoomRole
)