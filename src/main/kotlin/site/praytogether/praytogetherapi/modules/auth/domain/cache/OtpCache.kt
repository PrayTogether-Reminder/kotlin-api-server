package site.praytogether.praytogetherapi.modules.auth.domain.cache

interface OtpCache {
    fun put(key: String, value: String)
    fun delete(key: String): String?
    fun get(key: String): String?
}