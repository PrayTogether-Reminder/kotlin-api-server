package site.praytogether.praytogetherapi.modules.auth.infrastructure

import org.springframework.stereotype.Service
import site.praytogether.praytogetherapi.modules.auth.domain.cache.RefreshTokenCache
import site.praytogether.praytogetherapi.common.exception.AuthException
import site.praytogether.praytogetherapi.modules.auth.domain.service.RefreshTokenService

@Service
class RefreshTokenServiceImpl(
    private val refreshTokenCache: RefreshTokenCache
) : RefreshTokenService {

    override fun save(key: String, token: String) {
        refreshTokenCache.save(key, token)
    }

    override fun get(key: String): String? {
        return refreshTokenCache.get(key)
    }

    override fun delete(key: String): String? {
        return refreshTokenCache.delete(key)
    }

    override fun validateRefreshTokenExist(memberId: String, refresh: String) {
        val cachedRefresh = try {
            refreshTokenCache.get(memberId)
        } catch (e: Exception) {
            throw AuthException.refreshTokenNotFound()
        }
        if (cachedRefresh != refresh) {
            throw AuthException.refreshTokenNotValid(refresh)
        }
    }
}