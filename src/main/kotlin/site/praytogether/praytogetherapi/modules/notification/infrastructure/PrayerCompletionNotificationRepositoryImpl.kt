package site.praytogether.praytogetherapi.modules.notification.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import site.praytogether.praytogetherapi.modules.notification.domain.entity.PrayerCompletionNotification
import site.praytogether.praytogetherapi.modules.notification.domain.repository.PrayerCompletionNotificationRepository

interface PrayerCompletionNotificationJpaRepository : JpaRepository<PrayerCompletionNotification, Long> {
    @Query("SELECT n FROM PrayerCompletionNotification n WHERE n.id = :id AND n.recipientId = :recipientId")
    fun findByIdAndRecipientId(@Param("id") id: Long, @Param("recipientId") recipientId: Long): PrayerCompletionNotification?
    
    fun findByRecipientIdOrderByCreatedTimeDesc(recipientId: Long): List<PrayerCompletionNotification>
    
    fun findByRecipientIdAndIsReadFalse(recipientId: Long): List<PrayerCompletionNotification>
    
    fun countByRecipientIdAndIsReadFalse(recipientId: Long): Long
}

@Repository
class PrayerCompletionNotificationRepositoryImpl(
    private val jpaRepository: PrayerCompletionNotificationJpaRepository
) : PrayerCompletionNotificationRepository {
    
    override fun save(notification: PrayerCompletionNotification): PrayerCompletionNotification {
        return jpaRepository.save(notification)
    }
    
    override fun findById(id: Long): PrayerCompletionNotification? {
        return jpaRepository.findById(id).orElse(null)
    }
    
    override fun findByIdAndRecipientId(id: Long, recipientId: Long): PrayerCompletionNotification? {
        return jpaRepository.findByIdAndRecipientId(id, recipientId)
    }
    
    override fun findByRecipientIdOrderByCreatedTimeDesc(recipientId: Long): List<PrayerCompletionNotification> {
        return jpaRepository.findByRecipientIdOrderByCreatedTimeDesc(recipientId)
    }
    
    override fun findByRecipientIdAndIsReadFalse(recipientId: Long): List<PrayerCompletionNotification> {
        return jpaRepository.findByRecipientIdAndIsReadFalse(recipientId)
    }
    
    override fun countByRecipientIdAndIsReadFalse(recipientId: Long): Long {
        return jpaRepository.countByRecipientIdAndIsReadFalse(recipientId)
    }
    
    override fun delete(notification: PrayerCompletionNotification) {
        jpaRepository.delete(notification)
    }
    
    override fun deleteAll() {
        jpaRepository.deleteAll()
    }
    
    override fun findAll(): List<PrayerCompletionNotification> {
        return jpaRepository.findAll()
    }
}