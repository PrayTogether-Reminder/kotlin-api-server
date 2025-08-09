package site.praytogether.praytogetherapi.modules.member.application.dto

data class MemberProfileResponse(
    val id: Long,
    val name: String,
    val email: String
)