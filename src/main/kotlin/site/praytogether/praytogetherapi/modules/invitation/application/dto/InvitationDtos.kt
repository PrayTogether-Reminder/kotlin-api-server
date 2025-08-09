package site.praytogether.praytogetherapi.modules.invitation.application.dto

import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import java.time.Instant

data class CreateInvitationCommand(
    val roomId: Long,
    val inviteeEmail: String
)

data class UpdateInvitationStatusCommand(
    val status: InvitationStatus
)

data class InvitationInfo(
    val invitationId: Long,
    val roomId: Long,
    val roomName: String,
    val roomDescription: String,
    val inviterName: String,
    val status: InvitationStatus,
    val createdAt: Instant
)

data class InvitationScrollResponse(
    val invitations: List<InvitationInfo>,
    val hasNext: Boolean
)