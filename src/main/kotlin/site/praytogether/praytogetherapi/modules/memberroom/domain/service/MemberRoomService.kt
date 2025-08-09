package site.praytogether.praytogetherapi.modules.memberroom.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.common.exception.MemberRoomException
import site.praytogether.praytogetherapi.modules.memberroom.domain.repository.MemberRoomRepository
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole

@Service
@Transactional(readOnly = true)
class MemberRoomService(
    private val memberRoomRepository: MemberRoomRepository
) {

    @Transactional
    fun addMemberToRoom(member: Member, room: Room, role: RoomRole): MemberRoom {
        val memberRoom = MemberRoom.create(
            member = member,
            room = room,
            role = role
        )
        return memberRoomRepository.save(memberRoom)
    }

    fun fetchMemberIdsInRoom(roomId: Long): List<Long> {
        return memberRoomRepository.findMemberIdsByRoomId(roomId)
    }

    fun fetchMembersInRoom(roomId: Long): List<MemberInfo> {
        return memberRoomRepository.findMembersByRoomId(roomId).map { memberRoom ->
            MemberInfo(
                memberId = memberRoom.member.id!!,
                memberName = memberRoom.member.name,
                role = memberRoom.role
            )
        }
    }

    fun validateMemberExistInRoom(memberId: Long, roomId: Long) {
        if (!memberRoomRepository.existsByMemberIdAndRoomId(memberId, roomId)) {
            throw MemberRoomException.memberNotInRoom(memberId, roomId)
        }
    }

    fun validateMemberNotExistInRoom(memberId: Long, roomId: Long) {
        if (memberRoomRepository.existsByMemberIdAndRoomId(memberId, roomId)) {
            throw MemberRoomException.memberRoomAlreadyExists(memberId, roomId)
        }
    }

    @Transactional
    fun removeMemberFromRoom(memberId: Long, roomId: Long): Boolean {
        return memberRoomRepository.deleteByMemberIdAndRoomId(memberId, roomId) > 0
    }

    fun getRoomsByMemberId(memberId: Long): List<MemberRoom> {
        return memberRoomRepository.findByMemberId(memberId)
    }

    fun getMemberCountInRoom(roomId: Long): Int {
        return memberRoomRepository.countByRoomId(roomId).toInt()
    }

    fun getRoomsInfoByMemberId(memberId: Long): List<RoomInfo> {
        return memberRoomRepository.findByMemberId(memberId).map { memberRoom ->
            RoomInfo(
                roomId = memberRoom.room.id!!,
                roomName = memberRoom.room.name,
                roomDescription = memberRoom.room.description,
                role = memberRoom.role,
                joinedAt = memberRoom.createdTime ?: java.time.Instant.now()
            )
        }
    }

    @Transactional
    fun deleteAllMemberRoomsByRoomId(roomId: Long): Int {
        return memberRoomRepository.deleteByRoomId(roomId)
    }
}

data class MemberInfo(
    val memberId: Long,
    val memberName: String,
    val role: RoomRole
)

data class RoomInfo(
    val roomId: Long,
    val roomName: String,
    val roomDescription: String,
    val role: RoomRole,
    val joinedAt: java.time.Instant
)