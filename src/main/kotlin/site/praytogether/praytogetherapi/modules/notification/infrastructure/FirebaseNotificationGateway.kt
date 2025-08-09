package site.praytogether.praytogetherapi.modules.notification.infrastructure

import com.google.firebase.messaging.*
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import site.praytogether.praytogetherapi.modules.fcmtoken.domain.entity.FcmToken
import site.praytogether.praytogetherapi.modules.notification.domain.gateway.BatchNotificationResult
import site.praytogether.praytogetherapi.modules.notification.domain.gateway.NotificationGateway

@Component
@Profile("!h2 & !test")
class FirebaseNotificationGateway(
    private val firebaseMessaging: FirebaseMessaging
) : NotificationGateway {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun notifyCompletePrayer(
        fcmTokens: List<FcmToken>,
        message: String,
        onInvalidToken: (String) -> Unit
    ) {
        if (fcmTokens.isEmpty()) {
            return
        }

        val tokens = fcmTokens.map { it.token }
        val result = sendBatchNotifications(
            tokens = tokens,
            title = "기도 완료 알림",
            body = message
        )

        // Handle invalid tokens
        result.invalidTokens.forEach { invalidToken ->
            logger.info("Removing invalid FCM token: $invalidToken")
            onInvalidToken(invalidToken)
        }
    }

    override fun sendNotification(
        token: String,
        title: String,
        body: String,
        data: Map<String, String>
    ): Boolean {
        return try {
            val message = Message.builder()
                .setToken(token)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .putAllData(data)
                .build()

            val response = firebaseMessaging.send(message)
            logger.debug("Successfully sent message: $response")
            true
        } catch (e: FirebaseMessagingException) {
            logger.error("Failed to send FCM message to token: $token", e)
            false
        }
    }

    override fun sendBatchNotifications(
        tokens: List<String>,
        title: String,
        body: String,
        data: Map<String, String>
    ): BatchNotificationResult {
        if (tokens.isEmpty()) {
            return BatchNotificationResult(0, 0, emptyList())
        }

        val messages = tokens.map { token ->
            Message.builder()
                .setToken(token)
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )
                .putAllData(data)
                .build()
        }

        return try {
            val response = firebaseMessaging.sendEach(messages)
            val invalidTokens = mutableListOf<String>()
            
            response.responses.forEachIndexed { index, sendResponse ->
                if (!sendResponse.isSuccessful) {
                    val exception = sendResponse.exception
                    if (exception is FirebaseMessagingException) {
                        when (exception.messagingErrorCode) {
                            MessagingErrorCode.INVALID_ARGUMENT,
                            MessagingErrorCode.UNREGISTERED -> {
                                invalidTokens.add(tokens[index])
                            }
                            else -> {
                                logger.error("FCM send failed for token ${tokens[index]}: ${exception.message}")
                            }
                        }
                    }
                }
            }

            BatchNotificationResult(
                successCount = response.successCount,
                failureCount = response.failureCount,
                invalidTokens = invalidTokens
            )
        } catch (e: Exception) {
            logger.error("Failed to send batch FCM messages", e)
            BatchNotificationResult(0, tokens.size, emptyList())
        }
    }
}