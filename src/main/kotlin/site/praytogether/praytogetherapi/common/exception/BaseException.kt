package site.praytogether.praytogetherapi.common.exception

import site.praytogether.praytogetherapi.common.exception.spec.ExceptionSpec

abstract class BaseException(
    private val exceptionSpec: ExceptionSpec,
    private val exceptionField: ExceptionField
) : RuntimeException() {

    fun getExceptionSpec(): ExceptionSpec = exceptionSpec

    fun getLogMessage(): String {
        val fieldEntries = exceptionField.get().entries.joinToString(", ") { "${it.key}=${it.value}" }
        val fields = if (fieldEntries.isNotEmpty()) "[ $fieldEntries ]" else "[]"
        val enumName = (exceptionSpec as Enum<*>).name
        return "[ERROR] ${exceptionSpec.getCode()} : $enumName = ${exceptionSpec.getDebugMessage()} $fields"
    }

    abstract fun getClientMessage(): String
}