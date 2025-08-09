package site.praytogether.praytogetherapi.modules.invitation.domain.model

import java.time.Instant

data class InvitationInfo(
    val invitationId: Long,
    val inviterName: String,
    val roomName: String,
    val roomDescription: String,
    val createdTime: Instant // invitation createdTime
)