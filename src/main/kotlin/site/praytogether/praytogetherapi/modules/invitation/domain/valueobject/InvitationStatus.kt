package site.praytogether.praytogetherapi.modules.invitation.domain.valueobject

enum class InvitationStatus(val koreanName: String) {
    PENDING("대기중"),
    ACCEPTED("수락"),
    REJECTED("거절")
}