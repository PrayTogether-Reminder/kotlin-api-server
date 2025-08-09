package site.praytogether.praytogetherapi.common.exception

import site.praytogether.praytogetherapi.common.exception.spec.MemberExceptionSpec

class MemberException(
    spec: MemberExceptionSpec,
    field: ExceptionField
) : BaseException(spec, field) {

    override fun getClientMessage(): String {
        return when (getExceptionSpec() as MemberExceptionSpec) {
            MemberExceptionSpec.MEMBER_NOT_FOUND -> "회원 정보를 찾을 수 없습니다."
            MemberExceptionSpec.MEMBER_ALREADY_EXIST -> "이미 존재하는 회원입니다."
        }
    }

    companion object {
        fun memberNotFound(memberId: Long? = null, email: String? = null): MemberException {
            val fieldBuilder = ExceptionField.builder()
            memberId?.let { fieldBuilder.add("memberId", it) }
            email?.let { fieldBuilder.add("email", it) }
            return MemberException(MemberExceptionSpec.MEMBER_NOT_FOUND, fieldBuilder.build())
        }

        fun memberAlreadyExists(email: String? = null): MemberException {
            val fieldBuilder = ExceptionField.builder()
            email?.let { fieldBuilder.add("email", it) }
            return MemberException(MemberExceptionSpec.MEMBER_ALREADY_EXIST, fieldBuilder.build())
        }
    }
}