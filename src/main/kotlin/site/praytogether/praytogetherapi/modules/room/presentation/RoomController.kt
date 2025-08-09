package site.praytogether.praytogetherapi.modules.room.presentation

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import site.praytogether.praytogetherapi.modules.room.application.RoomApplicationService
import site.praytogether.praytogetherapi.modules.room.application.dto.CreateRoomCommand
import site.praytogether.praytogetherapi.modules.room.application.dto.RoomListResponse
import site.praytogether.praytogetherapi.modules.room.application.dto.RoomResponse
import site.praytogether.praytogetherapi.modules.room.application.dto.UpdateRoomCommand
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.common.annotation.PrincipalId
import site.praytogether.praytogetherapi.modules.memberroom.domain.service.MemberRoomService
import site.praytogether.praytogetherapi.modules.room.presentation.dto.CreateRoomRequest
import site.praytogether.praytogetherapi.modules.room.presentation.dto.UpdateRoomRequest
import site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomMembersResponse
import site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomMemberDto
import site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomMemberResponse

@RestController
@RequestMapping("/api/rooms")
@Validated
class RoomController(
    private val roomApplicationService: RoomApplicationService,
    private val memberRoomService: MemberRoomService
) {

    @PostMapping
    fun createRoom(
        @PrincipalId creatorId: Long,
        @Valid @RequestBody request: CreateRoomRequest
    ): ResponseEntity<MessageResponse> {
        val command = CreateRoomCommand(
            name = request.name,
            description = request.description,
            creatorId = creatorId
        )
        
        roomApplicationService.createRoom(command)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(MessageResponse("Room created successfully"))
    }

    @GetMapping("/{id}")
    fun getRoomById(@PathVariable @Min(1, message = "Room ID must be positive") id: Long): ResponseEntity<RoomResponse> {
        val room = roomApplicationService.getRoomById(id)
        return ResponseEntity.ok(room)
    }

    @GetMapping
    fun getAllRooms(
        @PrincipalId memberId: Long,
        @RequestParam(defaultValue = "time") orderBy: String?,
        @RequestParam(defaultValue = "0") after: String?,
        @RequestParam(defaultValue = "desc") dir: String?
    ): ResponseEntity<site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomInfiniteScrollResponse> {
        val query = site.praytogether.praytogetherapi.modules.room.application.dto.RoomInfiniteScrollQuery(
            memberId = memberId,
            orderBy = orderBy ?: "time",
            after = after ?: "0",
            dir = dir ?: "desc"
        )
        val response = roomApplicationService.getRoomsInfiniteScroll(query)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateRoom(
        @PathVariable @Min(1, message = "Room ID must be positive") id: Long,
        @Valid @RequestBody request: UpdateRoomRequest
    ): ResponseEntity<MessageResponse> {
        val command = UpdateRoomCommand(
            roomId = id,
            name = request.name,
            description = request.description
        )
        
        roomApplicationService.updateRoom(command)
        return ResponseEntity.ok(MessageResponse("Room updated successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteRoom(
        @PrincipalId memberId: Long,
        @PathVariable @Min(1, message = "Room ID must be positive") id: Long
    ): ResponseEntity<MessageResponse> {
        roomApplicationService.deleteRoom(id)
        return ResponseEntity.ok(MessageResponse("Room deleted successfully"))
    }

    @GetMapping("/{roomId}/members")
    fun getRoomMembers(
        @PrincipalId memberId: Long,
        @PathVariable @Min(1, message = "Room ID must be positive") roomId: Long
    ): ResponseEntity<RoomMemberResponse> {
        // Validate member is in the room
        memberRoomService.validateMemberExistInRoom(memberId, roomId)
        
        val members = memberRoomService.fetchMembersInRoom(roomId)
        val memberIdNames = members.map { memberInfo ->
            site.praytogether.praytogetherapi.modules.member.application.dto.MemberIdName(
                id = memberInfo.memberId,
                name = memberInfo.memberName
            )
        }
        
        return ResponseEntity.ok(RoomMemberResponse.from(memberIdNames))
    }
}