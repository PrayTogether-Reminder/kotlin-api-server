package site.praytogether.praytogetherapi.modules.notification.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.notification.domain.entity.PrayerCompletionNotification
import site.praytogether.praytogetherapi.modules.notification.domain.repository.PrayerCompletionNotificationRepository
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle

@Service
@Transactional(readOnly = true)
class PrayerCompletionNotificationService(
    private val repository: PrayerCompletionNotificationRepository
) {

    @Transactional
    fun create(
        senderId: Long,
        recipientIds: List<Long>,
        message: String,
        prayerTitle: PrayerTitle
    ): List<PrayerCompletionNotification> {
        val notifications = recipientIds
            .filter { it != senderId } // Don't notify the sender
            .map { recipientId ->
                PrayerCompletionNotification.create(
                    senderId = senderId,
                    recipientId = recipientId,
                    message = message,
                    prayerTitleId = prayerTitle.id!!
                )
            }
        
        return notifications.map { repository.save(it) }
    }

    fun getNotificationsByRecipient(recipientId: Long): List<PrayerCompletionNotification> {
        return repository.findByRecipientIdOrderByCreatedTimeDesc(recipientId)
    }

    fun getUnreadNotificationCount(recipientId: Long): Long {
        return repository.countByRecipientIdAndIsReadFalse(recipientId)
    }

    @Transactional
    fun markAsRead(notificationId: Long, recipientId: Long): Boolean {
        val notification = repository.findByIdAndRecipientId(notificationId, recipientId)
            ?: return false
        
        notification.markAsRead()
        repository.save(notification)
        return true
    }

    @Transactional
    fun markAllAsRead(recipientId: Long): Int {
        val notifications = repository.findByRecipientIdAndIsReadFalse(recipientId)
        notifications.forEach { it.markAsRead() }
        notifications.forEach { repository.save(it) }
        return notifications.size
    }
}