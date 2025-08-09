package site.praytogether.praytogetherapi.modules.auth.infrastructure.cache

import site.praytogether.praytogetherapi.modules.auth.domain.cache.OtpCache
import java.util.concurrent.ConcurrentHashMap

class OtpCacheInMemory(
    private val cache: MutableMap<String, String> = ConcurrentHashMap()
) : OtpCache {
    
    override fun put(key: String, value: String) {
        cache[key] = value
    }
    
    override fun delete(key: String): String? {
        return cache.remove(key)
    }
    
    override fun get(key: String): String? {
        return cache[key]
    }
}