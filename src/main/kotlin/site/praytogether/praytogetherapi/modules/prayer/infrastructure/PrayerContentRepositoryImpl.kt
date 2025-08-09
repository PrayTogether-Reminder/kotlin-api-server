package site.praytogether.praytogetherapi.modules.prayer.infrastructure

import org.springframework.stereotype.Repository
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerContent
import site.praytogether.praytogetherapi.modules.prayer.domain.repository.PrayerContentRepository

@Repository
class PrayerContentRepositoryImpl(
    private val prayerContentJpaRepository: PrayerContentJpaRepository
) : PrayerContentRepository {

    override fun save(prayerContent: PrayerContent): PrayerContent {
        return prayerContentJpaRepository.save(prayerContent)
    }

    override fun findById(id: Long): PrayerContent? {
        return prayerContentJpaRepository.findById(id).orElse(null)
    }

    override fun findByPrayerTitleId(prayerTitleId: Long): List<PrayerContent> {
        return prayerContentJpaRepository.findByPrayerTitleId(prayerTitleId)
    }

    override fun delete(prayerContent: PrayerContent) {
        prayerContentJpaRepository.delete(prayerContent)
    }
    
    override fun deleteAll() {
        prayerContentJpaRepository.deleteAll()
    }
    
    override fun findAll(): List<PrayerContent> {
        return prayerContentJpaRepository.findAll()
    }
}