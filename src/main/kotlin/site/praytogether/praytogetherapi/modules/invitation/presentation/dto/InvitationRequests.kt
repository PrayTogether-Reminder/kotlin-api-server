package site.praytogether.praytogetherapi.modules.invitation.presentation.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus

data class InvitationCreateRequest(
    @field:NotNull(message = "Room ID is required")
    val roomId: Long,
    
    @field:Email(message = "Valid email is required")
    val email: String
)

data class InvitationStatusUpdateRequest(
    @field:NotNull(message = "Status is required")
    val status: InvitationStatus
)