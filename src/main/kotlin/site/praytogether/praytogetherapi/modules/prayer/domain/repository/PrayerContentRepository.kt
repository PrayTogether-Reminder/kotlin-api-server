package site.praytogether.praytogetherapi.modules.prayer.domain.repository

import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerContent

interface PrayerContentRepository {
    fun save(prayerContent: PrayerContent): PrayerContent
    fun findById(id: Long): PrayerContent?
    fun findByPrayerTitleId(prayerTitleId: Long): List<PrayerContent>
    fun delete(prayerContent: PrayerContent)
    fun deleteAll()
    fun findAll(): List<PrayerContent>
}