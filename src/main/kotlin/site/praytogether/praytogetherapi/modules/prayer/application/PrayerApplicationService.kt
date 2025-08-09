package site.praytogether.praytogetherapi.modules.prayer.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import site.praytogether.praytogetherapi.modules.prayer.application.dto.*
import site.praytogether.praytogetherapi.modules.prayer.domain.service.PrayerCompletionService
import site.praytogether.praytogetherapi.modules.prayer.domain.service.PrayerDomainService
import site.praytogether.praytogetherapi.modules.member.domain.service.MemberDomainService
import site.praytogether.praytogetherapi.modules.fcmtoken.domain.service.FcmTokenService
import site.praytogether.praytogetherapi.modules.memberroom.domain.service.MemberRoomService
import site.praytogether.praytogetherapi.modules.notification.domain.gateway.NotificationGateway
import site.praytogether.praytogetherapi.modules.notification.domain.service.PrayerCompletionNotificationService
import site.praytogether.praytogetherapi.modules.notification.domain.constant.NotificationMessageFormat

@Service
@Transactional
class PrayerApplicationService(
    private val prayerDomainService: PrayerDomainService,
    private val prayerCompletionService: PrayerCompletionService,
    private val memberDomainService: MemberDomainService,
    private val fcmTokenService: FcmTokenService,
    private val memberRoomService: MemberRoomService,
    private val notificationGateway: NotificationGateway,
    private val prayerCompletionNotificationService: PrayerCompletionNotificationService
) {

    fun createPrayer(command: CreatePrayerCommand): Long {
        val prayerTitle = prayerDomainService.createPrayer(
            title = command.title,
            roomId = command.roomId,
            memberId = command.memberId,
            contents = command.contents
        )
        return prayerTitle.id!!
    }

    fun updatePrayer(command: UpdatePrayerCommand) {
        prayerDomainService.updatePrayerWithContents(
            prayerTitleId = command.prayerTitleId,
            title = command.title,
            contents = command.contents
        )
    }

    @Transactional(readOnly = true)
    fun getPrayerTitles(query: PrayerTitleQuery): PrayerTitleScrollResponse {
        val prayers = prayerDomainService.getPrayerTitles(
            roomId = query.roomId,
            afterId = query.after,
            limit = query.limit
        )

        val prayerResponses = prayers.map { prayer ->
            PrayerTitleResponse(
                id = prayer.id!!,
                title = prayer.title,
                memberId = prayer.memberId,
                createdAt = prayer.createdTime
            )
        }

        val hasNext = prayers.size >= query.limit
        val nextCursor = if (hasNext && prayers.isNotEmpty()) {
            prayers.last().id.toString()
        } else null

        return PrayerTitleScrollResponse(
            prayers = prayerResponses,
            hasNext = hasNext,
            nextCursor = nextCursor
        )
    }

    @Transactional(readOnly = true)
    fun getPrayerContents(prayerTitleId: Long): site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerContentsResponse {
        val contents = prayerDomainService.getPrayerContents(prayerTitleId)
        
        val prayerContentInfos = contents.map { content ->
            val memberId = content.createdBy
            if (memberId != null) {
                val member = memberDomainService.getMemberById(memberId)
                site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerContentInfo(
                    id = content.id!!,
                    content = content.content,
                    memberId = memberId,
                    memberName = member.name,
                    createdTime = content.createdTime ?: java.time.Instant.now()
                )
            } else {
                // Handle case where createdBy is null (e.g., in tests)
                site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerContentInfo(
                    id = content.id!!,
                    content = content.content,
                    memberId = 0L, // Default value for unknown user
                    memberName = "Unknown",
                    createdTime = content.createdTime ?: java.time.Instant.now()
                )
            }
        }.sortedBy { it.memberName }
        
        return site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerContentsResponse(
            prayerContents = prayerContentInfos
        )
    }

    fun deletePrayer(prayerTitleId: Long, memberId: Long) {
        prayerDomainService.deletePrayer(prayerTitleId, memberId)
    }

    fun completePrayer(prayerTitleId: Long, senderId: Long, roomId: Long): String {
        val prayerTitle = prayerDomainService.getPrayerTitleById(prayerTitleId)

        // Validate member is in room
        memberRoomService.validateMemberExistInRoom(senderId, roomId)

        // Create prayer completion record
        prayerCompletionService.create(senderId, prayerTitle)

        // Get sender information
        val sender = memberDomainService.getMemberById(senderId)

        // Create notification message using template
        val message = NotificationMessageFormat.formatPrayerCompletion(sender.name, prayerTitle.title)

        // Get all members in the room
        val memberIds = memberRoomService.fetchMemberIdsInRoom(roomId)
        
        // Create notification records
        prayerCompletionNotificationService.create(
            senderId = senderId,
            recipientIds = memberIds,
            message = message,
            prayerTitle = prayerTitle
        )

        // Get FCM tokens for all members
        val fcmTokens = fcmTokenService.fetchTokensByMemberIds(memberIds)
        
        // Send push notifications
        notificationGateway.notifyCompletePrayer(
            fcmTokens = fcmTokens,
            message = message,
            onInvalidToken = { token -> fcmTokenService.deleteByToken(token) }
        )
        
        return message
    }
}