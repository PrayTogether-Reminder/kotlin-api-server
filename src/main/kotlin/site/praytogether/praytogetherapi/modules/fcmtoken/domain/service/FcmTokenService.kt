package site.praytogether.praytogetherapi.modules.fcmtoken.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.fcmtoken.domain.entity.FcmToken
import site.praytogether.praytogetherapi.modules.fcmtoken.domain.repository.FcmTokenRepository
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.member.domain.repository.MemberRepository
import site.praytogether.praytogetherapi.modules.member.domain.exception.memberNotFound

@Service
@Transactional(readOnly = true)
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val memberRepository: MemberRepository
) {

    @Transactional
    fun create(member: Member, token: String): FcmToken {
        return FcmToken.create(member, token)
    }

    @Transactional
    fun save(fcmToken: FcmToken): FcmToken {
        return fcmTokenRepository.save(fcmToken)
    }

    fun isExist(member: Member, token: String): Boolean {
        return fcmTokenRepository.existsByTokenAndMember(token, member)
    }

    fun fetchTokensByMemberIds(memberIds: List<Long>): List<FcmToken> {
        return fcmTokenRepository.findByMemberIds(memberIds)
    }

    @Transactional
    fun deleteByMemberId(memberId: Long) {
        fcmTokenRepository.deleteByMemberId(memberId)
    }

    @Transactional
    fun deleteByTokenAndMemberId(token: String, memberId: Long) {
        fcmTokenRepository.deleteByTokenAndMemberId(token, memberId)
    }

    @Transactional
    fun deleteByToken(token: String) {
        fcmTokenRepository.deleteByToken(token)
    }

    @Transactional
    fun registerFcmToken(memberId: Long, token: String): FcmToken {
        val member = memberRepository.findById(memberId)
            ?: throw memberNotFound(memberId)

        // Delete existing tokens for this member
        deleteByMemberId(memberId)

        // Create and save new token
        val fcmToken = create(member, token)
        return save(fcmToken)
    }
}