package site.praytogether.praytogetherapi.modules.invitation.domain.exception

// Re-export common exceptions for backward compatibility
typealias InvitationNotFoundException = site.praytogether.praytogetherapi.common.exception.InvitationException

// Factory functions to create exceptions
fun invitationNotFound(invitationId: Long? = null): InvitationNotFoundException {
    return site.praytogether.praytogetherapi.common.exception.InvitationException.invitationNotFound(invitationId)
}

fun alreadyRespondedInvitation(invitationId: Long? = null): InvitationNotFoundException {
    return site.praytogether.praytogetherapi.common.exception.InvitationException.alreadyRespondedInvitation(invitationId)
}

// Keep old exception classes for backward compatibility where no common equivalent exists
class InvitationAlreadyExistsException(message: String) : RuntimeException(message)
class InvalidInvitationStatusException(message: String) : RuntimeException(message)