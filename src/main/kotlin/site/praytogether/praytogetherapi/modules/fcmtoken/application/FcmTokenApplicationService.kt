package site.praytogether.praytogetherapi.modules.fcmtoken.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.fcmtoken.application.dto.FcmTokenRegisterCommand
import site.praytogether.praytogetherapi.modules.fcmtoken.domain.service.FcmTokenService

@Service
@Transactional
class FcmTokenApplicationService(
    private val fcmTokenService: FcmTokenService
) {

    fun registerFcmToken(memberId: Long, command: FcmTokenRegisterCommand) {
        fcmTokenService.registerFcmToken(memberId, command.fcmToken)
    }

    fun deleteFcmToken(memberId: Long, token: String) {
        fcmTokenService.deleteByTokenAndMemberId(token, memberId)
    }
}