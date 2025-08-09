package site.praytogether.praytogetherapi.common.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import site.praytogether.praytogetherapi.common.constant.CoreConstant.OtpConstant.OTP_TTL_MINUTE
import site.praytogether.praytogetherapi.common.constant.CoreConstant.OtpConstant.REFRESH_TOKEN_TTL_DAYS
import site.praytogether.praytogetherapi.modules.auth.domain.cache.OtpCache
import site.praytogether.praytogetherapi.modules.auth.domain.cache.RefreshTokenCache
import site.praytogether.praytogetherapi.modules.auth.infrastructure.cache.OtpCacheCaffeine
import site.praytogether.praytogetherapi.modules.auth.infrastructure.cache.OtpCacheInMemory
import site.praytogether.praytogetherapi.modules.auth.infrastructure.cache.RefreshTokenCacheCaffeine
import site.praytogether.praytogetherapi.modules.auth.infrastructure.cache.RefreshTokenCacheInMemory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Configuration
class CacheConfig {

    @Bean
    @Primary
    fun otpCacheCaffeine(): OtpCache {
        return OtpCacheCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(OTP_TTL_MINUTE.toLong(), TimeUnit.MINUTES)
                .build()
        )
    }

    @Bean
    fun otpCacheInMemory(): OtpCache {
        return OtpCacheInMemory(ConcurrentHashMap())
    }

    @Bean
    @Primary
    fun refreshTokenCacheCaffeine(): RefreshTokenCache {
        return RefreshTokenCacheCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(REFRESH_TOKEN_TTL_DAYS.toLong(), TimeUnit.DAYS)
                .build()
        )
    }

    @Bean
    fun refreshTokenCacheInMemory(): RefreshTokenCache {
        return RefreshTokenCacheInMemory(ConcurrentHashMap())
    }
}