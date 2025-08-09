package site.praytogether.praytogetherapi.modules.invitation.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.invitation.domain.entity.Invitation
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import site.praytogether.praytogetherapi.modules.invitation.domain.exception.invitationNotFound
import site.praytogether.praytogetherapi.common.exception.InvitationException
import site.praytogether.praytogetherapi.modules.invitation.domain.repository.InvitationRepository
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.member.domain.exception.memberNotFound
import site.praytogether.praytogetherapi.modules.member.domain.repository.MemberRepository
import site.praytogether.praytogetherapi.modules.memberroom.domain.repository.MemberRoomRepository
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.domain.exception.roomNotFound
import site.praytogether.praytogetherapi.modules.room.domain.repository.RoomRepository

@Service
@Transactional(readOnly = true)
class InvitationDomainService(
    private val invitationRepository: InvitationRepository,
    private val memberRepository: MemberRepository,
    private val roomRepository: RoomRepository,
    private val memberRoomRepository: MemberRoomRepository
) {

    @Transactional
    fun createInvitation(inviterId: Long, inviteeEmail: String, roomId: Long): Invitation {
        val inviter = memberRepository.findById(inviterId)
            ?: throw memberNotFound(inviterId)
            
        val invitee = memberRepository.findByEmail(inviteeEmail)
            ?: throw memberNotFound(email = inviteeEmail)
            
        val room = roomRepository.findById(roomId)
            ?: throw roomNotFound(roomId)
            
        // Check if invitee is already in the room
        if (memberRoomRepository.existsByMemberIdAndRoomId(invitee.id!!, room.id!!)) {
            throw InvitationException.memberAlreadyInRoom(invitee.id!!, room.id!!)
        }
        
        // Check if invitation already exists
        val existingInvitation = invitationRepository.findByInviteeAndRoomAndStatus(
            invitee, room, InvitationStatus.PENDING
        )
        
        if (existingInvitation != null) {
            throw InvitationException.invitationAlreadyExists(inviter.id!!, invitee.id!!, room.id!!)
        }
        
        val invitation = Invitation.create(
            inviter = inviter,
            invitee = invitee,
            room = room
        )
        
        return invitationRepository.save(invitation)
    }

    fun getInvitationsByInviteeId(inviteeId: Long): List<Invitation> {
        return invitationRepository.findByInviteeIdOrderByCreatedTimeAsc(inviteeId)
    }

    fun getInvitationByIdAndInviteeId(invitationId: Long, inviteeId: Long): Invitation {
        return invitationRepository.findByIdAndInviteeId(invitationId, inviteeId)
            ?: throw invitationNotFound(invitationId)
    }

    @Transactional
    fun acceptInvitation(invitationId: Long, inviteeId: Long): Invitation {
        val invitation = getInvitationByIdAndInviteeId(invitationId, inviteeId)
        invitation.accept()
        return invitationRepository.save(invitation)
    }

    @Transactional
    fun rejectInvitation(invitationId: Long, inviteeId: Long): Invitation {
        val invitation = getInvitationByIdAndInviteeId(invitationId, inviteeId)
        invitation.reject()
        return invitationRepository.save(invitation)
    }

    fun validatePendingInvitationNotExists(inviteeId: Long, roomId: Long) {
        val invitee = memberRepository.findById(inviteeId)
            ?: throw memberNotFound(inviteeId)
            
        val room = roomRepository.findById(roomId)
            ?: throw roomNotFound(roomId)
            
        val existingInvitation = invitationRepository.findByInviteeAndRoomAndStatus(
            invitee, room, InvitationStatus.PENDING
        )
        
        if (existingInvitation != null) {
            throw InvitationException.invitationAlreadyExists(inviteeId, roomId)
        }
    }
}