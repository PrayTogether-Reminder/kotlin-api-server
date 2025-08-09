package site.praytogether.praytogetherapi.modules.prayer.domain.model

import jakarta.validation.constraints.NotBlank

data class PrayerUpdateContent(
    val id: Long?,
    val memberId: Long,
    
    @field:NotBlank(message = "기도 대상자 이름을 작성해 주세요.")
    val memberName: String,
    
    @field:NotBlank(message = "기도 내용을 입력해 주세요.")
    val content: String
)