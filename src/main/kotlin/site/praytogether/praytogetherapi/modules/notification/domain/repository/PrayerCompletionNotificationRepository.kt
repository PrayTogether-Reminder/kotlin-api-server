package site.praytogether.praytogetherapi.modules.notification.domain.repository

import site.praytogether.praytogetherapi.modules.notification.domain.entity.PrayerCompletionNotification

interface PrayerCompletionNotificationRepository {
    fun save(notification: PrayerCompletionNotification): PrayerCompletionNotification
    fun findById(id: Long): PrayerCompletionNotification?
    fun findByIdAndRecipientId(id: Long, recipientId: Long): PrayerCompletionNotification?
    fun findByRecipientIdOrderByCreatedTimeDesc(recipientId: Long): List<PrayerCompletionNotification>
    fun findByRecipientIdAndIsReadFalse(recipientId: Long): List<PrayerCompletionNotification>
    fun countByRecipientIdAndIsReadFalse(recipientId: Long): Long
    fun delete(notification: PrayerCompletionNotification)
    fun deleteAll()
    fun findAll(): List<PrayerCompletionNotification>
}