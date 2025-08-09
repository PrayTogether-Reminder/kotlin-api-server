package site.praytogether.praytogetherapi.modules.auth.infrastructure.cache

import com.github.benmanes.caffeine.cache.Cache
import site.praytogether.praytogetherapi.modules.auth.domain.cache.OtpCache
import site.praytogether.praytogetherapi.common.exception.AuthException

class OtpCacheCaffeine(
    private val cache: Cache<String, String>
) : OtpCache {
    
    override fun put(key: String, value: String) {
        // old value is replaced by the new value
        cache.put(key, value)
    }
    
    override fun delete(key: String): String? {
        cache.invalidate(key)
        return key
    }
    
    override fun get(key: String): String? {
        return cache.getIfPresent(key) 
            ?: throw AuthException.otpNotFound(key)
    }
}