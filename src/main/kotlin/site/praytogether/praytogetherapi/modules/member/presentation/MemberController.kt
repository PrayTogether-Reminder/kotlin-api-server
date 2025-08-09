package site.praytogether.praytogetherapi.modules.member.presentation

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import site.praytogether.praytogetherapi.modules.member.application.MemberApplicationService
import site.praytogether.praytogetherapi.modules.member.application.dto.CreateMemberCommand
import site.praytogether.praytogetherapi.modules.member.application.dto.UpdateMemberCommand
import site.praytogether.praytogetherapi.modules.member.presentation.dto.CreateMemberRequest
import site.praytogether.praytogetherapi.modules.member.presentation.dto.MemberResponse
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.modules.member.presentation.dto.UpdateMemberRequest
import site.praytogether.praytogetherapi.common.annotation.PrincipalId

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val memberApplicationService: MemberApplicationService
) {

    @PostMapping
    fun createMember(@Valid @RequestBody request: CreateMemberRequest): ResponseEntity<MessageResponse> {
        val command = CreateMemberCommand(
            name = request.name,
            email = request.email,
            password = request.password
        )
        
        memberApplicationService.createMember(command)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(MessageResponse("Member created successfully"))
    }

    @GetMapping("/profile")
    fun getMyProfile(@PrincipalId memberId: Long): ResponseEntity<MemberResponse> {
        val memberProfile = memberApplicationService.getMemberProfile(memberId)
        val response = MemberResponse(
            id = memberProfile.id,
            name = memberProfile.name,
            email = memberProfile.email
        )
        return ResponseEntity.ok(response)
    }
    
    @GetMapping("/{id}")
    fun getMemberProfile(@PathVariable id: Long): ResponseEntity<MemberResponse> {
        val memberProfile = memberApplicationService.getMemberProfile(id)
        val response = MemberResponse(
            id = memberProfile.id,
            name = memberProfile.name,
            email = memberProfile.email
        )
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateMember(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateMemberRequest
    ): ResponseEntity<MessageResponse> {
        val command = UpdateMemberCommand(
            memberId = id,
            name = request.name,
            password = request.password
        )
        
        memberApplicationService.updateMember(command)
        return ResponseEntity.ok(MessageResponse("Member updated successfully"))
    }
}