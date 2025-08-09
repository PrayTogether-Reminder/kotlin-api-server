package site.praytogether.praytogetherapi.modules.prayer.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerContent

interface PrayerContentJpaRepository : JpaRepository<PrayerContent, Long> {
    fun findByPrayerTitleId(prayerTitleId: Long): List<PrayerContent>
    fun deleteByPrayerTitleId(prayerTitleId: Long)
}