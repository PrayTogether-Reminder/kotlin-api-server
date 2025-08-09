package site.praytogether.praytogetherapi.modules.invitation.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.invitation.application.dto.*
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import site.praytogether.praytogetherapi.modules.invitation.domain.service.InvitationDomainService
import site.praytogether.praytogetherapi.modules.memberroom.domain.service.MemberRoomService
import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole

@Service
@Transactional(readOnly = true)
class InvitationApplicationService(
    private val invitationDomainService: InvitationDomainService,
    private val memberRoomService: MemberRoomService
) {

    fun getInvitations(memberId: Long): InvitationScrollResponse {
        val invitations = invitationDomainService.getInvitationsByInviteeId(memberId)
        
        val invitationInfos = invitations.map { invitation ->
            InvitationInfo(
                invitationId = invitation.id!!,
                roomId = invitation.room.id!!,
                roomName = invitation.room.name,
                roomDescription = invitation.room.description,
                inviterName = invitation.inviter.name,
                status = invitation.status,
                createdAt = invitation.createdTime ?: java.time.Instant.now()
            )
        }
        
        return InvitationScrollResponse(
            invitations = invitationInfos,
            hasNext = false
        )
    }

    @Transactional
    fun createInvitation(inviterId: Long, command: CreateInvitationCommand): Long {
        // Validate inviter exists and is in the room
        memberRoomService.validateMemberExistInRoom(inviterId, command.roomId)
        
        val invitation = invitationDomainService.createInvitation(
            inviterId = inviterId,
            inviteeEmail = command.inviteeEmail,
            roomId = command.roomId
        )
        
        return invitation.id!!
    }

    @Transactional
    fun updateInvitationStatus(
        memberId: Long, 
        invitationId: Long, 
        command: UpdateInvitationStatusCommand
    ): String {
        return when (command.status) {
            InvitationStatus.ACCEPTED -> {
                val invitation = invitationDomainService.acceptInvitation(invitationId, memberId)
                
                // Add member to room
                memberRoomService.addMemberToRoom(
                    member = invitation.invitee,
                    room = invitation.room,
                    role = RoomRole.MEMBER
                )
                
                "기도방 초대를 수락했습니다."
            }
            InvitationStatus.REJECTED -> {
                invitationDomainService.rejectInvitation(invitationId, memberId)
                "기도방 초대를 거절했습니다."
            }
            else -> throw IllegalArgumentException("Invalid status: ${command.status}")
        }
    }
}