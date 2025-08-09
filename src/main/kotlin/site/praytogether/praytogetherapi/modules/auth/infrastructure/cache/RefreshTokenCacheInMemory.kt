package site.praytogether.praytogetherapi.modules.auth.infrastructure.cache

import site.praytogether.praytogetherapi.modules.auth.domain.cache.RefreshTokenCache
import java.util.concurrent.ConcurrentHashMap

class RefreshTokenCacheInMemory(
    private val cache: MutableMap<String, String> = ConcurrentHashMap()
) : RefreshTokenCache {
    
    override fun save(key: String, value: String) {
        cache[key] = value
    }
    
    override fun delete(key: String): String? {
        return cache.remove(key)
    }
    
    override fun get(key: String): String? {
        return cache[key]
    }
    
    override fun isExist(key: String): Boolean {
        return cache.containsKey(key)
    }
}