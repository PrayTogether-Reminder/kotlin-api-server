package site.praytogether.praytogetherapi.common.exception

class ExceptionField private constructor(
    private val fields: Map<String, Any>
) {
    fun get(): Map<String, Any> = fields

    class Builder {
        private val fields = mutableMapOf<String, Any>()

        fun add(key: String, value: Any): Builder {
            fields[key] = value
            return this
        }

        fun build(): ExceptionField {
            return ExceptionField(fields.toMap())
        }
    }

    companion object {
        fun builder(): Builder = Builder()
    }
}