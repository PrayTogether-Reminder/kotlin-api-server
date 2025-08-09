package site.praytogether.praytogetherapi.modules.memberroom.domain.repository

import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom

interface MemberRoomRepository {
    fun save(memberRoom: MemberRoom): MemberRoom
    fun findById(id: Long): MemberRoom?
    fun findByMemberId(memberId: Long): List<MemberRoom>
    fun findByRoomId(roomId: Long): List<MemberRoom>
    fun findMembersByRoomId(roomId: Long): List<MemberRoom>
    fun findMemberIdsByRoomId(roomId: Long): List<Long>
    fun existsByMemberIdAndRoomId(memberId: Long, roomId: Long): Boolean
    fun deleteByMemberIdAndRoomId(memberId: Long, roomId: Long): Int
    fun deleteByRoomId(roomId: Long): Int
    fun countByRoomId(roomId: Long): Long
    fun deleteAll()
    fun findAll(): List<MemberRoom>
    fun saveAll(memberRooms: List<MemberRoom>): List<MemberRoom>
}