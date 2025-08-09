package site.praytogether.praytogetherapi.modules.prayer.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerContent
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.prayer.domain.exception.prayerTitleNotFound
import site.praytogether.praytogetherapi.modules.prayer.domain.repository.PrayerContentRepository
import site.praytogether.praytogetherapi.modules.prayer.domain.repository.PrayerTitleRepository
import site.praytogether.praytogetherapi.modules.room.domain.repository.RoomRepository
import site.praytogether.praytogetherapi.modules.room.domain.exception.roomNotFound
import site.praytogether.praytogetherapi.modules.prayer.application.dto.PrayerContentUpdateCommand

@Service
@Transactional(readOnly = true)
class PrayerDomainService(
    private val prayerTitleRepository: PrayerTitleRepository,
    private val prayerContentRepository: PrayerContentRepository,
    private val roomRepository: RoomRepository
) {

    @Transactional
    fun createPrayer(title: String, roomId: Long, memberId: Long, contents: List<String>): PrayerTitle {
        // Verify room exists
        roomRepository.findById(roomId)
            ?: throw roomNotFound(roomId)

        // Create prayer title
        val prayerTitle = PrayerTitle.create(
            title = title,
            roomId = roomId,
            memberId = memberId
        )
        val savedTitle = prayerTitleRepository.save(prayerTitle)

        // Create prayer contents
        contents.forEach { content ->
            val prayerContent = PrayerContent.create(
                prayerTitleId = savedTitle.id!!,
                content = content
            )
            prayerContentRepository.save(prayerContent)
        }

        return savedTitle
    }

    @Transactional
    fun updatePrayer(prayerTitleId: Long, title: String, contents: List<String>): PrayerTitle {
        val prayerTitle = getPrayerTitleById(prayerTitleId)

        // Update title
        prayerTitle.updateTitle(title)
        val updatedTitle = prayerTitleRepository.save(prayerTitle)

        // Delete existing contents
        val existingContents = prayerContentRepository.findByPrayerTitleId(prayerTitleId)
        existingContents.forEach { prayerContentRepository.delete(it) }

        // Create new contents
        contents.forEach { content ->
            val prayerContent = PrayerContent.create(
                prayerTitleId = prayerTitleId,
                content = content
            )
            prayerContentRepository.save(prayerContent)
        }

        return updatedTitle
    }

    @Transactional
    fun updatePrayerWithContents(prayerTitleId: Long, title: String, contents: List<PrayerContentUpdateCommand>): PrayerTitle {
        val prayerTitle = getPrayerTitleById(prayerTitleId)

        // Update title
        prayerTitle.updateTitle(title)
        val updatedTitle = prayerTitleRepository.save(prayerTitle)

        // Get existing contents
        val existingContents = prayerContentRepository.findByPrayerTitleId(prayerTitleId)
        val existingContentIds = existingContents.map { it.id!! }.toSet()
        
        // Track which existing content IDs are being updated
        val updatedContentIds = mutableSetOf<Long>()
        
        // Process each content in the request
        contents.forEach { contentCommand ->
            if (contentCommand.id != null) {
                // Update existing content
                val existingContent = existingContents.find { it.id == contentCommand.id }
                if (existingContent != null) {
                    existingContent.updateContent(contentCommand.content)
                    prayerContentRepository.save(existingContent)
                    updatedContentIds.add(contentCommand.id)
                }
            } else {
                // Create new content
                val newContent = PrayerContent.create(
                    prayerTitleId = prayerTitleId,
                    content = contentCommand.content
                )
                prayerContentRepository.save(newContent)
            }
        }
        
        // Delete contents that weren't updated (i.e., were removed)
        existingContents.forEach { content ->
            if (content.id != null && !updatedContentIds.contains(content.id!!)) {
                prayerContentRepository.delete(content)
            }
        }

        return updatedTitle
    }

    fun getPrayerTitleById(prayerTitleId: Long): PrayerTitle {
        return prayerTitleRepository.findById(prayerTitleId)
            ?: throw prayerTitleNotFound(prayerTitleId)
    }

    fun getPrayerTitles(roomId: Long, afterId: Long?, limit: Int): List<PrayerTitle> {
        val finalAfterId = afterId ?: 0L
        val pageable = org.springframework.data.domain.PageRequest.of(0, limit)
        return prayerTitleRepository.findByRoomIdWithPagination(
            roomId,
            finalAfterId,
            pageable
        )
    }
    
    fun getPrayerTitlesWithTimestamp(roomId: Long, afterTime: java.time.Instant?, limit: Int): List<PrayerTitle> {
        val pageable = org.springframework.data.domain.PageRequest.of(0, limit)
        val repositoryImpl = prayerTitleRepository as site.praytogether.praytogetherapi.modules.prayer.infrastructure.PrayerTitleRepositoryImpl
        return repositoryImpl.findByRoomIdWithTimestampPagination(
            roomId,
            afterTime,
            pageable
        )
    }

    fun getPrayerContents(prayerTitleId: Long): List<PrayerContent> {
        return prayerContentRepository.findByPrayerTitleId(prayerTitleId)
    }

    @Transactional
    fun deletePrayer(prayerTitleId: Long, memberId: Long) {
        val prayerTitle = getPrayerTitleById(prayerTitleId)

        // Verify ownership - throw not found to prevent information disclosure
        if (prayerTitle.memberId != memberId) {
            throw prayerTitleNotFound(prayerTitleId)
        }

        // Delete contents first
        val contents = prayerContentRepository.findByPrayerTitleId(prayerTitleId)
        contents.forEach { prayerContentRepository.delete(it) }

        // Delete title
        prayerTitleRepository.delete(prayerTitle)
    }

    fun validatePrayerOwnership(prayerTitleId: Long, memberId: Long): PrayerTitle {
        val prayerTitle = getPrayerTitleById(prayerTitleId)
        if (prayerTitle.memberId != memberId) {
            throw IllegalAccessException("You can only modify your own prayers")
        }
        return prayerTitle
    }
}