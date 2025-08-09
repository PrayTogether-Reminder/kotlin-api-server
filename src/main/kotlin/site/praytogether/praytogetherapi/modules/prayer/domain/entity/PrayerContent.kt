package site.praytogether.praytogetherapi.modules.prayer.domain.entity

import jakarta.persistence.*
import site.praytogether.praytogetherapi.common.entity.BaseEntity

@Entity
@Table(name = "prayer_content")
@SequenceGenerator(
    name = "PRAYER_CONTENT_SEQ_GENERATOR",
    sequenceName = "PRAYER_CONTENT_SEQ",
    initialValue = 1,
    allocationSize = 50
)
class PrayerContent protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRAYER_CONTENT_SEQ_GENERATOR")
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var prayerTitleId: Long = 0
        protected set

    @Column(nullable = false, columnDefinition = "CLOB")
    var content: String = ""
        protected set

    constructor(prayerTitleId: Long, content: String) : this() {
        this.prayerTitleId = prayerTitleId
        this.content = content
    }

    fun updateContent(newContent: String) {
        this.content = newContent
    }

    companion object {
        fun create(prayerTitleId: Long, content: String): PrayerContent {
            return PrayerContent(prayerTitleId, content)
        }
    }
}