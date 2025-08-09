package site.praytogether.praytogetherapi.modules.room.presentation.dto

import site.praytogether.praytogetherapi.modules.member.application.dto.MemberIdName

data class RoomMemberResponse(
    val members: List<MemberIdName>
) {
    companion object {
        fun from(memberIdNames: List<MemberIdName>): RoomMemberResponse {
            return RoomMemberResponse(memberIdNames)
        }
    }
}