package site.praytogether.praytogetherapi.modules.notification.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.Table
import site.praytogether.praytogetherapi.modules.notification.domain.valueobject.NotificationType

@Entity
@Table(name = "PRAYER_COMPLETION_NOTIFICATION")
@DiscriminatorValue(NotificationType.PRAYER_COMPLETION)
class PrayerCompletionNotification protected constructor() : Notification() {

    @Column(name = "prayer_title_id", nullable = false)
    var prayerTitleId: Long = 0
        protected set

    constructor(
        senderId: Long,
        recipientId: Long,
        message: String,
        prayerTitleId: Long
    ) : this() {
        this.senderId = senderId
        this.recipientId = recipientId
        this.message = message
        this.prayerTitleId = prayerTitleId
    }

    companion object {
        fun create(
            senderId: Long,
            recipientId: Long,
            message: String,
            prayerTitleId: Long
        ): PrayerCompletionNotification {
            return PrayerCompletionNotification(senderId, recipientId, message, prayerTitleId)
        }
    }
}