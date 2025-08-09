package site.praytogether.praytogetherapi.modules.notification.domain.entity

import jakarta.persistence.*
import site.praytogether.praytogetherapi.common.entity.BaseEntity

@Entity
@Table(name = "notification")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
    name = "notification_type",
    discriminatorType = DiscriminatorType.STRING,
    length = 50
)
@SequenceGenerator(
    name = "notification_seq_generator",
    sequenceName = "notification_seq",
    allocationSize = 50
)
abstract class Notification protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq_generator")
    var id: Long? = null
        protected set

    @Column(name = "sender_id", nullable = false)
    var senderId: Long = 0
        protected set

    @Column(name = "recipient_id", nullable = false)
    var recipientId: Long = 0
        protected set

    @Column(name = "message", nullable = false, length = 500)
    var message: String = ""
        protected set

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false
        protected set

    constructor(
        senderId: Long,
        recipientId: Long,
        message: String
    ) : this() {
        this.senderId = senderId
        this.recipientId = recipientId
        this.message = message
        this.isRead = false
    }

    fun markAsRead() {
        this.isRead = true
    }
}