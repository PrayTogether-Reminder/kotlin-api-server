package site.praytogether.praytogetherapi.security.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import site.praytogether.praytogetherapi.common.constant.CoreConstant.JwtConstant
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret-key}") jwtSecretKey: String,
    @Value("\${jwt.access-expire-time}") private val accessTokenExpireTime: Long,
    @Value("\${jwt.refresh-expire-time}") private val refreshTokenExpireTime: Long
) {
    private val TYPE = "type"
    private val EMAIL = "email"
    private val UID = "uid"
    
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtSecretKey.toByteArray())

    fun issueAccessToken(principal: PrayTogetherPrincipal): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(principal.id.toString())
            .claim(EMAIL, principal.email)
            .claim(TYPE, JwtConstant.ACCESS_TYPE)
            .claim(UID, UUID.randomUUID().toString().substring(0, 6))
            .issuedAt(Date(now))
            .expiration(Date(now + accessTokenExpireTime))
            .signWith(secretKey)
            .compact()
    }

    fun issueRefreshToken(principal: PrayTogetherPrincipal): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(principal.id.toString())
            .claim(EMAIL, principal.email)
            .claim(TYPE, JwtConstant.REFRESH_TYPE)
            .claim(UID, UUID.randomUUID().toString().substring(0, 6))
            .issuedAt(Date(now))
            .expiration(Date(now + refreshTokenExpireTime))
            .signWith(secretKey)
            .compact()
    }

    @Throws(JwtException::class)
    fun isValid(token: String) {
        Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
    }

    fun extractEmail(token: String): String {
        return extractAllClaims(token)[EMAIL] as String
    }

    fun extractType(token: String): String {
        return extractAllClaims(token)[TYPE] as String
    }

    fun extractMemberId(token: String): Long {
        return extractAllClaims(token).subject.toLong()
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload
    }

    fun extractPrincipal(token: String): PrayTogetherPrincipal {
        val claims = extractAllClaims(token)
        return PrayTogetherPrincipal(
            email = claims[EMAIL] as String,
            id = claims.subject.toLong(),
            password = "" // JWT에서는 password가 없으므로 빈 문자열
        )
    }
}