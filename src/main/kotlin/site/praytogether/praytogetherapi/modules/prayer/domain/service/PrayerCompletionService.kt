package site.praytogether.praytogetherapi.modules.prayer.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerCompletion
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.prayer.domain.repository.PrayerCompletionRepository

@Service
@Transactional(readOnly = true)
class PrayerCompletionService(
    private val prayerCompletionRepository: PrayerCompletionRepository
) {

    @Transactional
    fun create(prayerId: Long, prayerTitle: PrayerTitle): PrayerCompletion {
        val prayerCompletion = PrayerCompletion.create(prayerId, prayerTitle)
        return prayerCompletionRepository.save(prayerCompletion)
    }
}