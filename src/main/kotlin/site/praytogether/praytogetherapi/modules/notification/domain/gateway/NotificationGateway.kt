package site.praytogether.praytogetherapi.modules.notification.domain.gateway

import site.praytogether.praytogetherapi.modules.fcmtoken.domain.entity.FcmToken

interface NotificationGateway {
    fun notifyCompletePrayer(
        fcmTokens: List<FcmToken>,
        message: String,
        onInvalidToken: (String) -> Unit
    )
    
    fun sendNotification(
        token: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): Boolean
    
    fun sendBatchNotifications(
        tokens: List<String>,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): BatchNotificationResult
}

data class BatchNotificationResult(
    val successCount: Int,
    val failureCount: Int,
    val invalidTokens: List<String>
)