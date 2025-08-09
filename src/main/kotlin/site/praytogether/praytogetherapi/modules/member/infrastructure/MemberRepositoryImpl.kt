package site.praytogether.praytogetherapi.modules.member.infrastructure

import org.springframework.stereotype.Repository
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.member.domain.repository.MemberRepository
import site.praytogether.praytogetherapi.modules.member.domain.valueobject.MemberProfile

@Repository
class MemberRepositoryImpl(
    private val memberJpaRepository: MemberJpaRepository
) : MemberRepository {

    override fun save(member: Member): Member {
        return memberJpaRepository.save(member)
    }

    override fun findById(id: Long): Member? {
        return memberJpaRepository.findById(id).orElse(null)
    }

    override fun findByEmail(email: String): Member? {
        return memberJpaRepository.findByEmail(email).orElse(null)
    }

    override fun existsByEmail(email: String): Boolean {
        return memberJpaRepository.existsByEmail(email)
    }

    override fun findMemberProfileById(id: Long): MemberProfile? {
        return memberJpaRepository.findMemberProfileById(id).orElse(null)
    }

    override fun delete(member: Member) {
        memberJpaRepository.delete(member)
    }

    override fun deleteById(id: Long) {
        memberJpaRepository.deleteById(id)
    }
    
    override fun deleteAll() {
        memberJpaRepository.deleteAll()
    }
    
    override fun findAll(): List<Member> {
        return memberJpaRepository.findAll()
    }

    override fun saveAll(members: List<Member>): List<Member> {
        return memberJpaRepository.saveAll(members)
    }
}