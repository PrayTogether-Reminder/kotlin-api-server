package site.praytogether.praytogetherapi.modules.prayer.domain.model

import jakarta.validation.constraints.NotBlank

data class PrayerRequestContent(
    val memberId: Long,
    
    @field:NotBlank(message = "기도자의 이름을 작성해 주세요.")
    val memberName: String,
    
    @field:NotBlank(message = "기도 내용을 입력해 주세요.")
    val content: String
)