package site.praytogether.praytogetherapi.modules.fcmtoken.presentation

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import site.praytogether.praytogetherapi.common.annotation.PrincipalId
import site.praytogether.praytogetherapi.modules.fcmtoken.application.FcmTokenApplicationService
import site.praytogether.praytogetherapi.modules.fcmtoken.application.dto.FcmTokenRegisterCommand
import site.praytogether.praytogetherapi.modules.fcmtoken.presentation.dto.FcmTokenDeleteRequest
import site.praytogether.praytogetherapi.modules.fcmtoken.presentation.dto.FcmTokenRegisterRequest
import site.praytogether.praytogetherapi.common.dto.MessageResponse

@RestController
@RequestMapping("/api/fcm-token")
class FcmTokenController(
    private val fcmTokenApplicationService: FcmTokenApplicationService
) {

    @PostMapping
    fun registerFcmToken(
        @PrincipalId memberId: Long,
        @Valid @RequestBody request: FcmTokenRegisterRequest
    ): ResponseEntity<MessageResponse> {
        val command = FcmTokenRegisterCommand(fcmToken = request.fcmToken)
        fcmTokenApplicationService.registerFcmToken(memberId, command)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(MessageResponse("FCM 토큰을 등록했습니다."))
    }

    @DeleteMapping
    fun deleteFcmToken(
        @PrincipalId memberId: Long,
        @Valid @RequestBody request: FcmTokenDeleteRequest
    ): ResponseEntity<MessageResponse> {
        fcmTokenApplicationService.deleteFcmToken(memberId, request.fcmToken)
        return ResponseEntity.ok(MessageResponse("FCM 토큰을 삭제했습니다."))
    }
}