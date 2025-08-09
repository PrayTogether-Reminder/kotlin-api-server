package site.praytogether.praytogetherapi.modules.member.domain.repository

import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.member.domain.valueobject.MemberProfile

interface MemberRepository {
    fun save(member: Member): Member
    fun findById(id: Long): Member?
    fun findByEmail(email: String): Member?
    fun existsByEmail(email: String): Boolean
    fun findMemberProfileById(id: Long): MemberProfile?
    fun delete(member: Member)
    fun deleteById(id: Long)
    fun deleteAll()
    fun findAll(): List<Member>
    fun saveAll(members: List<Member>): List<Member>
}