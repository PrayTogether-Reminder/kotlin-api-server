package site.praytogether.praytogetherapi.modules.invitation.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import site.praytogether.praytogetherapi.modules.invitation.domain.entity.Invitation
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import site.praytogether.praytogetherapi.modules.invitation.domain.repository.InvitationRepository
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room

interface InvitationJpaRepository : JpaRepository<Invitation, Long> {
    @Query("SELECT i FROM Invitation i WHERE i.invitee.id = :inviteeId ORDER BY i.createdTime ASC")
    fun findByInviteeIdOrderByCreatedTimeAsc(@Param("inviteeId") inviteeId: Long): List<Invitation>
    
    @Query("SELECT i FROM Invitation i WHERE i.invitee = :invitee AND i.room = :room AND i.status = :status")
    fun findByInviteeAndRoomAndStatus(
        @Param("invitee") invitee: Member,
        @Param("room") room: Room,
        @Param("status") status: InvitationStatus
    ): Invitation?
    
    @Query("SELECT i FROM Invitation i WHERE i.invitee.id = :inviteeId AND i.status = :status")
    fun findByInviteeIdAndStatus(
        @Param("inviteeId") inviteeId: Long,
        @Param("status") status: InvitationStatus
    ): List<Invitation>
    
    @Query("SELECT i FROM Invitation i WHERE i.id = :id AND i.invitee.id = :inviteeId")
    fun findByIdAndInviteeId(@Param("id") id: Long, @Param("inviteeId") inviteeId: Long): Invitation?
    
    @Query("SELECT i FROM Invitation i WHERE i.room.id = :roomId AND i.invitee.id = :inviteeId")
    fun findByRoomIdAndInviteeId(@Param("roomId") roomId: Long, @Param("inviteeId") inviteeId: Long): Invitation?
}

@Repository
class InvitationRepositoryImpl(
    private val jpaRepository: InvitationJpaRepository
) : InvitationRepository {
    
    override fun save(invitation: Invitation): Invitation {
        return jpaRepository.save(invitation)
    }
    
    override fun findById(id: Long): Invitation? {
        return jpaRepository.findById(id).orElse(null)
    }
    
    override fun findByIdAndInviteeId(id: Long, inviteeId: Long): Invitation? {
        return jpaRepository.findByIdAndInviteeId(id, inviteeId)
    }
    
    override fun findByInviteeIdOrderByCreatedTimeAsc(inviteeId: Long): List<Invitation> {
        return jpaRepository.findByInviteeIdOrderByCreatedTimeAsc(inviteeId)
    }
    
    override fun findByInviteeAndRoomAndStatus(invitee: Member, room: Room, status: InvitationStatus): Invitation? {
        return jpaRepository.findByInviteeAndRoomAndStatus(invitee, room, status)
    }
    
    override fun findByInviteeIdAndStatus(inviteeId: Long, status: InvitationStatus): List<Invitation> {
        return jpaRepository.findByInviteeIdAndStatus(inviteeId, status)
    }
    
    override fun findByRoomIdAndInviteeId(roomId: Long, inviteeId: Long): Invitation? {
        return jpaRepository.findByRoomIdAndInviteeId(roomId, inviteeId)
    }
    
    override fun delete(invitation: Invitation) {
        jpaRepository.delete(invitation)
    }
    
    override fun deleteAll() {
        jpaRepository.deleteAll()
    }
    
    override fun findAll(): List<Invitation> {
        return jpaRepository.findAll()
    }
}