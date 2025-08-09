package site.praytogether.praytogetherapi.modules.auth.domain.service

interface RefreshTokenService {
    fun save(key: String, token: String)
    fun get(key: String): String?
    fun delete(key: String): String?
    fun validateRefreshTokenExist(memberId: String, refresh: String)
}