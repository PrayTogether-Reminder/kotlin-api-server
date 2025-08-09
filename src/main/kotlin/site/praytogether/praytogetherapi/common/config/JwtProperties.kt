package site.praytogether.praytogetherapi.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secretKey: String,
    val accessExpireTime: Long,
    val refreshExpireTime: Long
)