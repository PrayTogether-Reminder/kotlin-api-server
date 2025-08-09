package site.praytogether.praytogetherapi.modules.prayer.domain.entity

import jakarta.persistence.*
import site.praytogether.praytogetherapi.common.constant.CoreConstant.PrayerTitleConstant
import site.praytogether.praytogetherapi.common.entity.BaseEntity

@Entity
@Table(name = "prayer_title")
@SequenceGenerator(
    name = "PRAYER_TITLE_SEQ_GENERATOR",
    sequenceName = "PRAYER_TITLE_SEQ",
    initialValue = 1,
    allocationSize = 50
)
class PrayerTitle protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRAYER_TITLE_SEQ_GENERATOR")
    var id: Long? = null
        protected set

    @Column(nullable = false, length = PrayerTitleConstant.TITLE_ENTITY_MAX_LEN)
    var title: String = ""
        protected set

    @Column(nullable = false)
    var roomId: Long = 0
        protected set

    @Column(nullable = false)
    var memberId: Long = 0
        protected set

    constructor(title: String, roomId: Long, memberId: Long) : this() {
        this.title = title
        this.roomId = roomId
        this.memberId = memberId
    }

    fun updateTitle(newTitle: String) {
        this.title = newTitle
    }

    companion object {
        fun create(title: String, roomId: Long, memberId: Long): PrayerTitle {
            return PrayerTitle(title, roomId, memberId)
        }
    }
}