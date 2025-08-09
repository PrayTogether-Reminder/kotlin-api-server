package site.praytogether.praytogetherapi.modules.room.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.room.application.dto.CreateRoomCommand
import site.praytogether.praytogetherapi.modules.room.application.dto.RoomListResponse
import site.praytogether.praytogetherapi.modules.room.application.dto.RoomResponse
import site.praytogether.praytogetherapi.modules.room.application.dto.UpdateRoomCommand
import site.praytogether.praytogetherapi.modules.room.application.dto.RoomInfiniteScrollQuery
import site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomInfiniteScrollResponse
import site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomInfo
import site.praytogether.praytogetherapi.modules.room.domain.service.RoomDomainService
import site.praytogether.praytogetherapi.modules.memberroom.domain.service.MemberRoomService
import site.praytogether.praytogetherapi.modules.member.domain.service.MemberDomainService
import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole

@Service
@Transactional
class RoomApplicationService(
    private val roomDomainService: RoomDomainService,
    private val memberRoomService: MemberRoomService,
    private val memberDomainService: MemberDomainService
) {

    fun createRoom(command: CreateRoomCommand): Long {
        val room = roomDomainService.createRoom(command.name, command.description, command.creatorId)
        
        // Add creator as room owner using DDD style
        val creator = memberDomainService.getMemberById(command.creatorId)
        room.addMember(creator, RoomRole.OWNER)
        
        // Save will cascade to MemberRoom automatically
        roomDomainService.saveRoom(room)
        
        return room.id!!
    }

    fun updateRoom(command: UpdateRoomCommand) {
        roomDomainService.updateRoom(
            roomId = command.roomId,
            name = command.name,
            description = command.description
        )
    }

    @Transactional(readOnly = true)
    fun getRoomById(roomId: Long): RoomResponse {
        val room = roomDomainService.getRoomById(roomId)
        return RoomResponse(
            id = room.id!!,
            name = room.name,
            description = room.description
        )
    }

    @Transactional(readOnly = true)
    fun getAllRooms(): RoomListResponse {
        val rooms = roomDomainService.getAllRooms()
        val roomResponses = rooms.map { room ->
            RoomResponse(
                id = room.id!!,
                name = room.name,
                description = room.description
            )
        }
        return RoomListResponse(roomResponses)
    }

    fun deleteRoom(roomId: Long) {
        // First delete all member-room relationships
        memberRoomService.deleteAllMemberRoomsByRoomId(roomId)
        
        // Then delete the room itself
        roomDomainService.deleteRoom(roomId)
    }

    @Transactional(readOnly = true)
    fun getRoomsInfiniteScroll(query: RoomInfiniteScrollQuery): RoomInfiniteScrollResponse {
        val memberRooms = memberRoomService.getRoomsByMemberId(query.memberId)
        
        // Parse after parameter - could be timestamp or "0" for initial request
        val afterTime = if (query.after == "0" || query.after.isEmpty()) {
            java.time.Instant.now()
        } else {
            try {
                java.time.Instant.parse(query.after)
            } catch (e: Exception) {
                java.time.Instant.now()
            }
        }
        
        // Filter and sort member rooms based on joined time
        val filteredRooms = memberRooms
            .filter { memberRoom -> 
                val createdTime = memberRoom.createdTime ?: java.time.Instant.now()
                if (query.dir == "desc") {
                    createdTime.isBefore(afterTime)
                } else {
                    createdTime.isAfter(afterTime)
                }
            }
            .sortedWith { mr1, mr2 ->
                val time1 = mr1.createdTime ?: java.time.Instant.now()
                val time2 = mr2.createdTime ?: java.time.Instant.now()
                if (query.dir == "desc") {
                    time2.compareTo(time1)
                } else {
                    time1.compareTo(time2)
                }
            }
            .take(10)
        
        val roomInfos = filteredRooms.map { memberRoom ->
            val room = roomDomainService.getRoomById(memberRoom.room.id!!)
            val memberCount = memberRoomService.getMemberCountInRoom(memberRoom.room.id!!)
            
            site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomInfo(
                id = room.id!!,
                name = room.name,
                description = room.description,
                joinedTime = memberRoom.createdTime ?: java.time.Instant.now(),
                memberCount = memberCount
            )
        }
        
        return RoomInfiniteScrollResponse(rooms = roomInfos)
    }
}