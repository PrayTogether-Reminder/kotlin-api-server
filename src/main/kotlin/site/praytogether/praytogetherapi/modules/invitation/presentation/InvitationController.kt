package site.praytogether.praytogetherapi.modules.invitation.presentation

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import site.praytogether.praytogetherapi.common.annotation.PrincipalId
import site.praytogether.praytogetherapi.modules.invitation.application.InvitationApplicationService
import site.praytogether.praytogetherapi.modules.invitation.application.dto.CreateInvitationCommand
import site.praytogether.praytogetherapi.modules.invitation.application.dto.InvitationScrollResponse
import site.praytogether.praytogetherapi.modules.invitation.presentation.dto.InvitationInfoScrollResponse
import site.praytogether.praytogetherapi.modules.invitation.application.dto.UpdateInvitationStatusCommand
import site.praytogether.praytogetherapi.modules.invitation.presentation.dto.InvitationCreateRequest
import site.praytogether.praytogetherapi.modules.invitation.presentation.dto.InvitationStatusUpdateRequest
import site.praytogether.praytogetherapi.common.dto.MessageResponse

@RestController
@RequestMapping("/api/invitations")
@Validated
class InvitationController(
    private val invitationApplicationService: InvitationApplicationService
) {

    @GetMapping
    fun getInvitations(
        @PrincipalId memberId: Long
    ): ResponseEntity<InvitationInfoScrollResponse> {
        val response = invitationApplicationService.getInvitations(memberId)
        // Convert the application service response to the expected presentation DTO
        val invitationInfos = response.invitations.map { appInvitation ->
            site.praytogether.praytogetherapi.modules.invitation.presentation.dto.InvitationInfo(
                invitationId = appInvitation.invitationId,
                inviterName = appInvitation.inviterName,
                roomName = appInvitation.roomName,
                roomDescription = appInvitation.roomDescription,
                createdTime = appInvitation.createdAt
            )
        }
        
        val presentationResponse = InvitationInfoScrollResponse(invitations = invitationInfos)
        return ResponseEntity.ok(presentationResponse)
    }

    @PostMapping
    fun createInvitation(
        @PrincipalId inviterId: Long,
        @Valid @RequestBody request: InvitationCreateRequest
    ): ResponseEntity<MessageResponse> {
        val command = CreateInvitationCommand(
            roomId = request.roomId,
            inviteeEmail = request.email
        )
        invitationApplicationService.createInvitation(inviterId, command)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(MessageResponse("초대를 완료했습니다."))
    }

    @PatchMapping("/{invitationId}")
    fun updateInvitationStatus(
        @PrincipalId memberId: Long,
        @PathVariable @Min(1, message = "Invitation ID must be positive") invitationId: Long,
        @Valid @RequestBody request: InvitationStatusUpdateRequest
    ): ResponseEntity<MessageResponse> {
        val command = UpdateInvitationStatusCommand(status = request.status)
        val message = invitationApplicationService.updateInvitationStatus(
            memberId, invitationId, command
        )
        return ResponseEntity.ok(MessageResponse(message))
    }
}