package site.praytogether.praytogetherapi.common.util

import org.springframework.security.core.context.SecurityContextHolder
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal

object SecurityUtil {
    fun getCurrentMemberId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal as PrayTogetherPrincipal
        return principal.id
    }
}