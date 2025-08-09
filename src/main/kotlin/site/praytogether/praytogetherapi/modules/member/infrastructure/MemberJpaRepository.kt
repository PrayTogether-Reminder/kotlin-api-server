package site.praytogether.praytogetherapi.modules.member.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.member.domain.valueobject.MemberProfile
import java.util.*

interface MemberJpaRepository : JpaRepository<Member, Long> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Optional<Member>
    fun findMemberProfileById(id: Long): Optional<MemberProfile>
}