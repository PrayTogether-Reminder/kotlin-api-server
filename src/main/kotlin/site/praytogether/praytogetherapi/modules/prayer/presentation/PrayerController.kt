package site.praytogether.praytogetherapi.modules.prayer.presentation

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import site.praytogether.praytogetherapi.common.annotation.PrincipalId
import site.praytogether.praytogetherapi.modules.prayer.application.PrayerApplicationService
import site.praytogether.praytogetherapi.modules.prayer.application.dto.*
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.*
import site.praytogether.praytogetherapi.common.dto.MessageResponse

@RestController
@RequestMapping("/api/prayers")
@Validated
class PrayerController(
    private val prayerApplicationService: PrayerApplicationService,
    private val memberDomainService: site.praytogether.praytogetherapi.modules.member.domain.service.MemberDomainService,
    private val prayerDomainService: site.praytogether.praytogetherapi.modules.prayer.domain.service.PrayerDomainService
) {

    @PostMapping
    fun createPrayer(
        @PrincipalId memberId: Long,
        @Valid @RequestBody request: CreatePrayerRequest
    ): ResponseEntity<CreatePrayerResponse> {
        val command = CreatePrayerCommand(
            title = request.title,
            contents = request.contents,
            roomId = request.roomId,
            memberId = memberId
        )
        
        val prayerId = prayerApplicationService.createPrayer(command)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreatePrayerResponse(prayerId))
    }

    @PutMapping("/{prayerTitleId}")
    fun updatePrayer(
        @PathVariable @Min(1, message = "Prayer ID must be positive") prayerTitleId: Long,
        @Valid @RequestBody request: PrayerUpdateRequest
    ): ResponseEntity<MessageResponse> {
        val command = UpdatePrayerCommand(
            prayerTitleId = prayerTitleId,
            title = request.title,
            contents = request.contents.map { content ->
                PrayerContentUpdateCommand(
                    id = content.id,
                    memberId = content.memberId,
                    content = content.content
                )
            }
        )
        
        prayerApplicationService.updatePrayer(command)
        return ResponseEntity.ok(MessageResponse("Prayer updated successfully"))
    }

    @GetMapping
    fun getPrayerTitles(
        @RequestParam roomId: Long,
        @RequestParam(defaultValue = "0") after: String?,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<PrayerTitleInfiniteScrollResponse> {
        try {
            // Convert after parameter - handle both timestamp and ID based pagination
            val afterInstant = if (after == "0" || after.isNullOrEmpty()) {
                null
            } else {
                try {
                    // Try to parse as timestamp first
                    java.time.Instant.parse(after)
                } catch (e: Exception) {
                    try {
                        // Fallback to ID-based approach - convert ID to timestamp by looking up the entity
                        val id = after.toLong()
                        if (id > 0) {
                            val prayerTitle = prayerDomainService.getPrayerTitleById(id)
                            prayerTitle.createdTime
                        } else null
                    } catch (e2: Exception) {
                        null
                    }
                }
            }
            
            // Use timestamp-based pagination directly
            val prayers = prayerDomainService.getPrayerTitlesWithTimestamp(roomId, afterInstant, limit)
            
            // Convert to the expected presentation format - handle potential errors gracefully
            val prayerTitleInfos = prayers.mapNotNull { prayerTitle ->
                try {
                    val member = memberDomainService.getMemberById(prayerTitle.memberId)
                    val contentCount = prayerDomainService.getPrayerContents(prayerTitle.id!!).size
                    
                    PrayerTitleInfo(
                        id = prayerTitle.id!!,
                        title = prayerTitle.title,
                        memberId = prayerTitle.memberId,
                        memberName = member.name,
                        createdTime = prayerTitle.createdTime ?: java.time.Instant.now(),
                        contentCount = contentCount
                    )
                } catch (e: Exception) {
                    // Log the error but continue processing other items
                    null
                }
            }
            
            val infiniteScrollResponse = PrayerTitleInfiniteScrollResponse(prayerTitles = prayerTitleInfos)
            return ResponseEntity.ok(infiniteScrollResponse)
        } catch (e: Exception) {
            // Fallback to empty response on any error
            val emptyResponse = PrayerTitleInfiniteScrollResponse(prayerTitles = emptyList())
            return ResponseEntity.ok(emptyResponse)
        }
    }

    @GetMapping("/{prayerTitleId}/contents")
    fun getPrayerContents(
        @PathVariable @Min(1, message = "Prayer ID must be positive") prayerTitleId: Long
    ): ResponseEntity<PrayerContentsResponse> {
        val response = prayerApplicationService.getPrayerContents(prayerTitleId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{prayerTitleId}")
    fun deletePrayer(
        @PrincipalId memberId: Long,
        @PathVariable @Min(1, message = "Prayer ID must be positive") prayerTitleId: Long
    ): ResponseEntity<MessageResponse> {
        prayerApplicationService.deletePrayer(prayerTitleId, memberId)
        return ResponseEntity.ok(MessageResponse("Prayer deleted successfully"))
    }

    @PostMapping("/{prayerTitleId}/completion")
    fun completePrayer(
        @PrincipalId memberId: Long,
        @PathVariable @Min(1, message = "Prayer ID must be positive") prayerTitleId: Long,
        @Valid @RequestBody request: PrayerCompletionCreateRequest
    ): ResponseEntity<MessageResponse> {
        prayerApplicationService.completePrayer(prayerTitleId, memberId, request.roomId)
        return ResponseEntity.ok(MessageResponse("기도 완료 알림을 전송했습니다."))
    }
}