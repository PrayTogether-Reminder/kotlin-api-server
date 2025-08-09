package site.praytogether.praytogetherapi.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal
import java.util.*

@Configuration
@EnableJpaAuditing
class JpaAuditConfig {

    @Bean
    fun auditorAware(): AuditorAware<Long> {
        return AuditorAware {
            Optional.ofNullable(SecurityContextHolder.getContext())
                .map { it.authentication }
                .filter { it.isAuthenticated }
                .map { it.principal }
                .filter { it is PrayTogetherPrincipal }
                .map { it as PrayTogetherPrincipal }
                .map { it.id }
        }
    }
}