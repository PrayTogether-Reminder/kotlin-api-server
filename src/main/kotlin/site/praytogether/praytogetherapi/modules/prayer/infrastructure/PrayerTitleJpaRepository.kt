package site.praytogether.praytogetherapi.modules.prayer.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle

interface PrayerTitleJpaRepository : JpaRepository<PrayerTitle, Long> {
    fun findByRoomId(roomId: Long): List<PrayerTitle>
    fun findByMemberId(memberId: Long): List<PrayerTitle>
    
    @Query("""
        SELECT p FROM PrayerTitle p 
        WHERE p.roomId = :roomId 
        AND (:afterTime IS NULL OR p.createdTime < :afterTime)
        ORDER BY p.createdTime DESC
    """)
    fun findByRoomIdWithTimestampPagination(
        @Param("roomId") roomId: Long,
        @Param("afterTime") afterTime: java.time.Instant?,
        pageable: org.springframework.data.domain.Pageable
    ): List<PrayerTitle>
    
    @Query("""
        SELECT p FROM PrayerTitle p 
        WHERE p.roomId = :roomId 
        ORDER BY p.createdTime DESC
    """)
    fun findByRoomIdWithPagination(
        @Param("roomId") roomId: Long,
        @Param("afterId") afterId: Long,
        pageable: org.springframework.data.domain.Pageable
    ): List<PrayerTitle>
}