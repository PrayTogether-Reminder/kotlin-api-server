package site.praytogether.praytogetherapi.modules.fcmtoken.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.praytogether.praytogetherapi.modules.fcmtoken.domain.entity.FcmToken
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {

    fun existsByTokenAndMember(token: String, member: Member): Boolean

    @Query("SELECT f FROM FcmToken f WHERE f.member.id IN :memberIds AND f.isActive = true")
    fun findByMemberIds(@Param("memberIds") memberIds: List<Long>): List<FcmToken>

    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.member.id = :memberId")
    fun deleteByMemberId(@Param("memberId") memberId: Long)

    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.token = :token AND f.member.id = :memberId")
    fun deleteByTokenAndMemberId(@Param("token") token: String, @Param("memberId") memberId: Long)

    @Modifying
    @Query("DELETE FROM FcmToken f WHERE f.token = :token")
    fun deleteByToken(@Param("token") token: String)

    fun findByMemberAndToken(member: Member, token: String): FcmToken?
}