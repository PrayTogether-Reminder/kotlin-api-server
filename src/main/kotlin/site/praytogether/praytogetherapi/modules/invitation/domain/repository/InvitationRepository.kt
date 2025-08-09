package site.praytogether.praytogetherapi.modules.invitation.domain.repository

import site.praytogether.praytogetherapi.modules.invitation.domain.entity.Invitation
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room

interface InvitationRepository {
    fun save(invitation: Invitation): Invitation
    fun findById(id: Long): Invitation?
    fun findByIdAndInviteeId(id: Long, inviteeId: Long): Invitation?
    fun findByInviteeIdOrderByCreatedTimeAsc(inviteeId: Long): List<Invitation>
    fun findByInviteeAndRoomAndStatus(invitee: Member, room: Room, status: InvitationStatus): Invitation?
    fun findByInviteeIdAndStatus(inviteeId: Long, status: InvitationStatus): List<Invitation>
    fun findByRoomIdAndInviteeId(roomId: Long, inviteeId: Long): Invitation?
    fun delete(invitation: Invitation)
    fun deleteAll()
    fun findAll(): List<Invitation>
}