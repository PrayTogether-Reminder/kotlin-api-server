package site.praytogether.praytogetherapi.modules.prayer.domain.entity

import jakarta.persistence.*
import site.praytogether.praytogetherapi.common.entity.BaseEntity

@Entity
@Table(name = "prayer_completion")
class PrayerCompletion protected constructor() : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prayer_completion_seq")
    @SequenceGenerator(name = "prayer_completion_seq", sequenceName = "prayer_completion_seq", allocationSize = 50)
    var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prayer_title_id", nullable = false)
    lateinit var prayerTitle: PrayerTitle
        protected set

    @Column(name = "prayer_id", nullable = false, updatable = false)
    var prayerId: Long = 0
        protected set

    companion object {
        fun create(prayerId: Long, prayerTitle: PrayerTitle): PrayerCompletion {
            return PrayerCompletion().apply {
                this.prayerId = prayerId
                this.prayerTitle = prayerTitle
            }
        }
    }
}