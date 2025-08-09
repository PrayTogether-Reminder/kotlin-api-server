package site.praytogether.praytogetherapi.modules.member.application.dto

data class CreateMemberCommand(
    val name: String,
    val email: String,
    val password: String
)

data class UpdateMemberCommand(
    val memberId: Long,
    val name: String? = null,
    val password: String? = null
)