package site.praytogether.praytogetherapi.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom

@Configuration
class SecureRandomConfig {

    @Bean
    fun secureRandom(): SecureRandom {
        return SecureRandom()
    }
}