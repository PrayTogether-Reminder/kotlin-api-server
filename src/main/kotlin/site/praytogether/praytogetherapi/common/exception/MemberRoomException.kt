package site.praytogether.praytogetherapi.common.exception

import site.praytogether.praytogetherapi.common.exception.spec.MemberRoomExceptionSpec

class MemberRoomException(
    spec: MemberRoomExceptionSpec,
    field: ExceptionField
) : BaseException(spec, field) {

    override fun getClientMessage(): String {
        return when (getExceptionSpec() as MemberRoomExceptionSpec) {
            MemberRoomExceptionSpec.MEMBER_ROOM_NOT_FOUND -> "회원이 속한 방을 찾을 수 없습니다."
            MemberRoomExceptionSpec.MEMBER_ROOM_ALREADY_EXIST -> "이미 방에 존재하는 회원입니다."
            MemberRoomExceptionSpec.MEMBER_NOT_IN_ROOM -> "방에 속하지 않은 사용자입니다."
        }
    }

    companion object {
        fun memberRoomNotFound(memberId: Long? = null, roomId: Long? = null): MemberRoomException {
            val fieldBuilder = ExceptionField.builder()
            memberId?.let { fieldBuilder.add("memberId", it) }
            roomId?.let { fieldBuilder.add("roomId", it) }
            return MemberRoomException(MemberRoomExceptionSpec.MEMBER_ROOM_NOT_FOUND, fieldBuilder.build())
        }

        fun memberRoomAlreadyExists(memberId: Long? = null, roomId: Long? = null): MemberRoomException {
            val fieldBuilder = ExceptionField.builder()
            memberId?.let { fieldBuilder.add("memberId", it) }
            roomId?.let { fieldBuilder.add("roomId", it) }
            return MemberRoomException(MemberRoomExceptionSpec.MEMBER_ROOM_ALREADY_EXIST, fieldBuilder.build())
        }

        fun memberNotInRoom(memberId: Long? = null, roomId: Long? = null): MemberRoomException {
            val fieldBuilder = ExceptionField.builder()
            memberId?.let { fieldBuilder.add("memberId", it) }
            roomId?.let { fieldBuilder.add("roomId", it) }
            return MemberRoomException(MemberRoomExceptionSpec.MEMBER_NOT_IN_ROOM, fieldBuilder.build())
        }
    }
}