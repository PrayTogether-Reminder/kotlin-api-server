package site.praytogether.praytogetherapi.modules.prayer.presentation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class PrayerUpdateRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    val title: String,

    val contents: List<PrayerUpdateContent>
)

data class PrayerUpdateContent(
    val id: Long? = null, // null for new content, provided for existing content
    
    @field:NotNull(message = "Member ID is required")
    val memberId: Long,
    
    @field:NotBlank(message = "Member name is required")
    val memberName: String,
    
    @field:NotBlank(message = "Content is required")
    val content: String
)