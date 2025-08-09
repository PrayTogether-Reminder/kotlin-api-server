package site.praytogether.praytogetherapi.common.exception

import site.praytogether.praytogetherapi.common.exception.spec.RoomExceptionSpec

class RoomException(
    spec: RoomExceptionSpec,
    field: ExceptionField
) : BaseException(spec, field) {

    override fun getClientMessage(): String {
        return when (getExceptionSpec() as RoomExceptionSpec) {
            RoomExceptionSpec.ROOM_NOT_FOUND -> "방을 찾을 수 없습니다."
        }
    }

    companion object {
        fun roomNotFound(roomId: Long? = null): RoomException {
            val fieldBuilder = ExceptionField.builder()
            roomId?.let { fieldBuilder.add("roomId", it) }
            return RoomException(RoomExceptionSpec.ROOM_NOT_FOUND, fieldBuilder.build())
        }
    }
}