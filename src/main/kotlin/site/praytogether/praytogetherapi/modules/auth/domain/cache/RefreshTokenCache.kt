package site.praytogether.praytogetherapi.modules.auth.domain.cache

interface RefreshTokenCache {
    fun save(key: String, value: String)
    fun delete(key: String): String?
    fun get(key: String): String?
    fun isExist(key: String): Boolean
}