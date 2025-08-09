package site.praytogether.praytogetherapi.modules.auth.infrastructure.cache

import com.github.benmanes.caffeine.cache.Cache
import site.praytogether.praytogetherapi.modules.auth.domain.cache.RefreshTokenCache
import site.praytogether.praytogetherapi.common.exception.AuthException

class RefreshTokenCacheCaffeine(
    private val cache: Cache<String, String>
) : RefreshTokenCache {
    
    override fun save(key: String, value: String) {
        cache.put(key, value)
    }
    
    override fun delete(key: String): String? {
        cache.invalidate(key)
        return key
    }
    
    override fun get(key: String): String? {
        return cache.getIfPresent(key) 
            ?: throw AuthException.refreshTokenNotFound()
    }
    
    override fun isExist(key: String): Boolean {
        return cache.asMap().containsKey(key)
    }
}