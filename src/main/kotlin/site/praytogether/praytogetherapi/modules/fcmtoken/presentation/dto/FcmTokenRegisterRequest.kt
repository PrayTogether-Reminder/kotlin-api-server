package site.praytogether.praytogetherapi.modules.fcmtoken.presentation.dto

import jakarta.validation.constraints.NotBlank

data class FcmTokenRegisterRequest(
    @field:NotBlank(message = "FCM token is required")
    val fcmToken: String
)