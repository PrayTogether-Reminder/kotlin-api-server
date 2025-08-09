package site.praytogether.praytogetherapi.common.exception

import site.praytogether.praytogetherapi.common.exception.spec.InvitationExceptionSpec

class InvitationException(
    spec: InvitationExceptionSpec,
    field: ExceptionField
) : BaseException(spec, field) {

    override fun getClientMessage(): String {
        return when (getExceptionSpec() as InvitationExceptionSpec) {
            InvitationExceptionSpec.INVITATION_NOT_FOUND -> "방 초대장을 찾을 수 없습니다."
            InvitationExceptionSpec.ALREADY_RESPONDED_INVITATION -> "이미 응답 설정(ACCEPT/REJECT)이 된 방 초대장입니다."
            InvitationExceptionSpec.INVITATION_ALREADY_EXISTS -> "이미 방에 대한 초대장이 존재합니다."
            InvitationExceptionSpec.MEMBER_ALREADY_IN_ROOM -> "이미 방에 속한 사용자입니다."
        }
    }

    companion object {
        fun invitationNotFound(invitationId: Long? = null): InvitationException {
            val fieldBuilder = ExceptionField.builder()
            invitationId?.let { fieldBuilder.add("invitationId", it) }
            return InvitationException(InvitationExceptionSpec.INVITATION_NOT_FOUND, fieldBuilder.build())
        }

        fun alreadyRespondedInvitation(invitationId: Long? = null): InvitationException {
            val fieldBuilder = ExceptionField.builder()
            invitationId?.let { fieldBuilder.add("invitationId", it) }
            return InvitationException(InvitationExceptionSpec.ALREADY_RESPONDED_INVITATION, fieldBuilder.build())
        }

        fun invitationAlreadyExists(inviterId: Long? = null, inviteeId: Long? = null, roomId: Long? = null): InvitationException {
            val fieldBuilder = ExceptionField.builder()
            inviterId?.let { fieldBuilder.add("inviterId", it) }
            inviteeId?.let { fieldBuilder.add("inviteeId", it) }
            roomId?.let { fieldBuilder.add("roomId", it) }
            return InvitationException(InvitationExceptionSpec.INVITATION_ALREADY_EXISTS, fieldBuilder.build())
        }

        fun memberAlreadyInRoom(inviteeId: Long? = null, roomId: Long? = null): InvitationException {
            val fieldBuilder = ExceptionField.builder()
            inviteeId?.let { fieldBuilder.add("inviteeId", it) }
            roomId?.let { fieldBuilder.add("roomId", it) }
            return InvitationException(InvitationExceptionSpec.MEMBER_ALREADY_IN_ROOM, fieldBuilder.build())
        }
    }
}