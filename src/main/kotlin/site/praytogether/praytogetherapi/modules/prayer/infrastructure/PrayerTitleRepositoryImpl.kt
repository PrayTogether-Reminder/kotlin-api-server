package site.praytogether.praytogetherapi.modules.prayer.infrastructure

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.prayer.domain.repository.PrayerTitleRepository

@Repository
class PrayerTitleRepositoryImpl(
    private val prayerTitleJpaRepository: PrayerTitleJpaRepository
) : PrayerTitleRepository {

    override fun save(prayerTitle: PrayerTitle): PrayerTitle {
        return prayerTitleJpaRepository.save(prayerTitle)
    }

    override fun findById(id: Long): PrayerTitle? {
        return prayerTitleJpaRepository.findById(id).orElse(null)
    }

    override fun findByRoomId(roomId: Long): List<PrayerTitle> {
        return prayerTitleJpaRepository.findByRoomId(roomId)
    }

    override fun findByMemberId(memberId: Long): List<PrayerTitle> {
        return prayerTitleJpaRepository.findByMemberId(memberId)
    }

    override fun findByRoomIdWithPagination(roomId: Long, afterId: Long, pageable: org.springframework.data.domain.Pageable): List<PrayerTitle> {
        return prayerTitleJpaRepository.findByRoomIdWithPagination(roomId, afterId, pageable)
    }
    
    fun findByRoomIdWithTimestampPagination(roomId: Long, afterTime: java.time.Instant?, pageable: org.springframework.data.domain.Pageable): List<PrayerTitle> {
        return prayerTitleJpaRepository.findByRoomIdWithTimestampPagination(roomId, afterTime, pageable)
    }

    override fun delete(prayerTitle: PrayerTitle) {
        prayerTitleJpaRepository.delete(prayerTitle)
    }
    
    override fun deleteAll() {
        prayerTitleJpaRepository.deleteAll()
    }
    
    override fun findAll(): List<PrayerTitle> {
        return prayerTitleJpaRepository.findAll()
    }
}