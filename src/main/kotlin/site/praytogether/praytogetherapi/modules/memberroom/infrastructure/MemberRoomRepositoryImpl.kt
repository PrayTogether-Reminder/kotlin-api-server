package site.praytogether.praytogetherapi.modules.memberroom.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.memberroom.domain.repository.MemberRoomRepository

interface MemberRoomJpaRepository : JpaRepository<MemberRoom, Long> {
    @Query("SELECT mr.member.id FROM MemberRoom mr WHERE mr.room.id = :roomId")
    fun findMemberIdsByRoomId(@Param("roomId") roomId: Long): List<Long>
    
    @Query("SELECT mr FROM MemberRoom mr JOIN FETCH mr.member WHERE mr.room.id = :roomId")
    fun findByRoomId(@Param("roomId") roomId: Long): List<MemberRoom>
    
    @Query("SELECT CASE WHEN COUNT(mr) > 0 THEN true ELSE false END FROM MemberRoom mr WHERE mr.member.id = :memberId AND mr.room.id = :roomId")
    fun existsByMemberIdAndRoomId(@Param("memberId") memberId: Long, @Param("roomId") roomId: Long): Boolean
    
    @Modifying
    @Query("DELETE FROM MemberRoom mr WHERE mr.member.id = :memberId AND mr.room.id = :roomId")
    fun deleteByMemberIdAndRoomId(@Param("memberId") memberId: Long, @Param("roomId") roomId: Long): Int
    
    @Modifying
    @Query("DELETE FROM MemberRoom mr WHERE mr.room.id = :roomId")
    fun deleteByRoomId(@Param("roomId") roomId: Long): Int
    
    @Query("SELECT mr FROM MemberRoom mr JOIN FETCH mr.room WHERE mr.member.id = :memberId")
    fun findByMemberId(@Param("memberId") memberId: Long): List<MemberRoom>
    
    @Query("SELECT COUNT(mr) FROM MemberRoom mr WHERE mr.room.id = :roomId")
    fun countByRoomId(@Param("roomId") roomId: Long): Long
}

@Repository
class MemberRoomRepositoryImpl(
    private val jpaRepository: MemberRoomJpaRepository
) : MemberRoomRepository {
    
    override fun save(memberRoom: MemberRoom): MemberRoom {
        return jpaRepository.save(memberRoom)
    }
    
    override fun findById(id: Long): MemberRoom? {
        return jpaRepository.findById(id).orElse(null)
    }
    
    override fun findMemberIdsByRoomId(roomId: Long): List<Long> {
        return jpaRepository.findMemberIdsByRoomId(roomId)
    }
    
    override fun findByRoomId(roomId: Long): List<MemberRoom> {
        return jpaRepository.findByRoomId(roomId)
    }
    
    override fun findMembersByRoomId(roomId: Long): List<MemberRoom> {
        return jpaRepository.findByRoomId(roomId)
    }
    
    override fun existsByMemberIdAndRoomId(memberId: Long, roomId: Long): Boolean {
        return jpaRepository.existsByMemberIdAndRoomId(memberId, roomId)
    }
    
    override fun deleteByMemberIdAndRoomId(memberId: Long, roomId: Long): Int {
        return jpaRepository.deleteByMemberIdAndRoomId(memberId, roomId)
    }
    
    override fun deleteByRoomId(roomId: Long): Int {
        return jpaRepository.deleteByRoomId(roomId)
    }
    
    override fun findByMemberId(memberId: Long): List<MemberRoom> {
        return jpaRepository.findByMemberId(memberId)
    }
    
    override fun countByRoomId(roomId: Long): Long {
        return jpaRepository.countByRoomId(roomId)
    }
    
    override fun deleteAll() {
        jpaRepository.deleteAll()
    }
    
    override fun findAll(): List<MemberRoom> {
        return jpaRepository.findAll()
    }

    override fun saveAll(memberRooms: List<MemberRoom>): List<MemberRoom> {
        return jpaRepository.saveAll(memberRooms)
    }
}