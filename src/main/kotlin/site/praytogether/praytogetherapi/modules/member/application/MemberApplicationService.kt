package site.praytogether.praytogetherapi.modules.member.application

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.member.application.dto.CreateMemberCommand
import site.praytogether.praytogetherapi.modules.member.application.dto.MemberProfileResponse
import site.praytogether.praytogetherapi.modules.member.application.dto.UpdateMemberCommand
import site.praytogether.praytogetherapi.modules.member.domain.service.MemberDomainService

@Service
@Transactional
class MemberApplicationService(
    private val memberDomainService: MemberDomainService,
    private val passwordEncoder: PasswordEncoder
) {

    fun createMember(command: CreateMemberCommand): Long {
        val encodedPassword = passwordEncoder.encode(command.password)
        val member = memberDomainService.createMember(command.name, command.email, encodedPassword)
        return member.id!!
    }

    fun updateMember(command: UpdateMemberCommand) {
        val encodedPassword = command.password?.let { passwordEncoder.encode(it) }
        memberDomainService.updateMember(
            id = command.memberId,
            name = command.name,
            password = encodedPassword
        )
    }

    @Transactional(readOnly = true)
    fun getMemberProfile(memberId: Long): MemberProfileResponse {
        val memberProfile = memberDomainService.getMemberProfile(memberId)
        return MemberProfileResponse(
            id = memberProfile.id,
            name = memberProfile.name,
            email = memberProfile.email
        )
    }
}