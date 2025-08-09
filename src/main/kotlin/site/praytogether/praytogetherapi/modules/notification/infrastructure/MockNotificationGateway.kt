package site.praytogether.praytogetherapi.modules.notification.infrastructure

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import site.praytogether.praytogetherapi.modules.fcmtoken.domain.entity.FcmToken
import site.praytogether.praytogetherapi.modules.notification.domain.gateway.BatchNotificationResult
import site.praytogether.praytogetherapi.modules.notification.domain.gateway.NotificationGateway

@Component
@Profile("h2", "test")
class MockNotificationGateway : NotificationGateway {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun notifyCompletePrayer(
        fcmTokens: List<FcmToken>,
        message: String,
        onInvalidToken: (String) -> Unit
    ) {
        logger.info("[MOCK] Sending prayer completion notification to ${fcmTokens.size} users: $message")
    }

    override fun sendNotification(
        token: String,
        title: String,
        body: String,
        data: Map<String, String>
    ): Boolean {
        logger.info("[MOCK] Sending notification - Token: $token, Title: $title, Body: $body")
        return true
    }

    override fun sendBatchNotifications(
        tokens: List<String>,
        title: String,
        body: String,
        data: Map<String, String>
    ): BatchNotificationResult {
        logger.info("[MOCK] Sending batch notifications to ${tokens.size} tokens - Title: $title, Body: $body")
        return BatchNotificationResult(
            successCount = tokens.size,
            failureCount = 0,
            invalidTokens = emptyList()
        )
    }
}