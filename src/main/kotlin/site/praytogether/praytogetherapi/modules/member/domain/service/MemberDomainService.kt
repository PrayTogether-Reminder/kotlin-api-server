package site.praytogether.praytogetherapi.modules.member.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.member.domain.exception.memberAlreadyExists
import site.praytogether.praytogetherapi.modules.member.domain.exception.memberNotFound
import site.praytogether.praytogetherapi.modules.member.domain.repository.MemberRepository
import site.praytogether.praytogetherapi.modules.member.domain.valueobject.MemberProfile

@Service
@Transactional(readOnly = true)
class MemberDomainService(
    private val memberRepository: MemberRepository
) {

    fun getMemberById(id: Long): Member {
        return memberRepository.findById(id)
            ?: throw memberNotFound(id)
    }

    fun getMemberByEmail(email: String): Member {
        return memberRepository.findByEmail(email)
            ?: throw memberNotFound(email = email)
    }

    fun getMemberProfile(id: Long): MemberProfile {
        return memberRepository.findMemberProfileById(id)
            ?: throw memberNotFound(id)
    }

    fun validateEmailUniqueness(email: String) {
        if (memberRepository.existsByEmail(email)) {
            throw memberAlreadyExists(email)
        }
    }

    fun validateEmailExists(email: String) {
        if (!memberRepository.existsByEmail(email)) {
            throw memberNotFound(email = email)
        }
    }

    @Transactional
    fun createMember(name: String, email: String, encodedPassword: String): Member {
        validateEmailUniqueness(email)
        val member = Member.create(name, email, encodedPassword)
        return memberRepository.save(member)
    }

    @Transactional
    fun updateMember(id: Long, name: String? = null, password: String? = null): Member {
        val member = getMemberById(id)
        
        name?.let { member.updateName(it) }
        password?.let { member.updatePassword(it) }
        
        return memberRepository.save(member)
    }

    @Transactional
    fun deleteMember(id: Long) {
        val member = getMemberById(id)
        memberRepository.delete(member)
    }

    @Transactional
    fun deleteMemberById(id: Long) {
        memberRepository.deleteById(id)
    }

    fun existsByEmail(email: String): Boolean {
        return memberRepository.existsByEmail(email)
    }
}