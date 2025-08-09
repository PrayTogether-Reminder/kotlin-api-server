package site.praytogether.praytogetherapi.security.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.member.domain.repository.MemberRepository
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal

@Service
class PrayTogetherUserDetailsService(
    private val memberRepository: MemberRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val member = memberRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("회원 정보를 찾을 수 없습니다.")
        
        return PrayTogetherPrincipal(
            id = member.id!!,
            email = member.email,
            password = member.password
        )
    }
}