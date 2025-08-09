package site.praytogether.praytogetherapi.modules.auth.infrastructure

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import site.praytogether.praytogetherapi.common.config.JwtProperties
import site.praytogether.praytogetherapi.common.constant.CoreConstant.JwtConstant
import site.praytogether.praytogetherapi.modules.auth.domain.service.JwtTokenService
import site.praytogether.praytogetherapi.modules.auth.domain.valueobject.JwtTokenPair
import site.praytogether.praytogetherapi.modules.auth.domain.valueobject.TokenPayload
import java.security.Key
import java.util.*

@Service
class JwtTokenServiceImpl(
    private val jwtProperties: JwtProperties
) : JwtTokenService {

    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray())

    companion object {
        private const val EMAIL = "email"
        private const val TYPE = "type"
        private const val UID = "uid"
    }

    override fun generateTokenPair(userId: Long, email: String): JwtTokenPair {
        val accessToken = generateAccessToken(userId, email)
        val refreshToken = generateRefreshToken(userId, email)
        return JwtTokenPair(accessToken, refreshToken)
    }

    private fun generateAccessToken(userId: Long, email: String): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(userId.toString())
            .claim(EMAIL, email)
            .claim(TYPE, JwtConstant.ACCESS_TYPE)
            .claim(UID, UUID.randomUUID().toString().substring(0, 6))
            .issuedAt(Date(now))
            .expiration(Date(now + jwtProperties.accessExpireTime))
            .signWith(secretKey)
            .compact()
    }

    private fun generateRefreshToken(userId: Long, email: String): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(userId.toString())
            .claim(EMAIL, email)
            .claim(TYPE, JwtConstant.REFRESH_TYPE)
            .claim(UID, UUID.randomUUID().toString().substring(0, 6))
            .issuedAt(Date(now))
            .expiration(Date(now + jwtProperties.refreshExpireTime))
            .signWith(secretKey)
            .compact()
    }

    override fun validateToken(token: String): TokenPayload? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(secretKey as javax.crypto.SecretKey)
                .build()
                .parseSignedClaims(token)
                .payload

            TokenPayload(
                userId = claims.subject.toLong(),
                email = claims.get(EMAIL, String::class.java),
                tokenType = claims.get(TYPE, String::class.java)
            )
        } catch (e: JwtException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override fun extractTokenFromHeader(authorizationHeader: String?): String? {
        return if (authorizationHeader != null && authorizationHeader.startsWith(JwtConstant.HTTP_HEADER_AUTH_BEARER)) {
            authorizationHeader.substring(JwtConstant.HTTP_HEADER_AUTH_BEARER.length)
        } else {
            null
        }
    }
}