package site.praytogether.praytogetherapi.modules.prayer.domain.repository

import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle

interface PrayerTitleRepository {
    fun save(prayerTitle: PrayerTitle): PrayerTitle
    fun findById(id: Long): PrayerTitle?
    fun findByRoomId(roomId: Long): List<PrayerTitle>
    fun findByMemberId(memberId: Long): List<PrayerTitle>
    fun findByRoomIdWithPagination(roomId: Long, afterId: Long, pageable: org.springframework.data.domain.Pageable): List<PrayerTitle>
    fun delete(prayerTitle: PrayerTitle)
    fun deleteAll()
    fun findAll(): List<PrayerTitle>
}