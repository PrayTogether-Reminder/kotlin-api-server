package site.praytogether.praytogetherapi.modules.invitation.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class InvitationInfoScrollResponse(
    @JsonProperty("invitations")
    val invitations: List<InvitationInfo>
)

data class InvitationInfo(
    @JsonProperty("invitationId")
    val invitationId: Long,
    @JsonProperty("inviterName")
    val inviterName: String,
    @JsonProperty("roomName")
    val roomName: String,
    @JsonProperty("roomDescription")
    val roomDescription: String,
    @JsonProperty("createdTime")
    val createdTime: Instant
)