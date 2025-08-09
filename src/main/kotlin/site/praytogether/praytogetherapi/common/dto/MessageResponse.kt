package site.praytogether.praytogetherapi.common.dto

data class MessageResponse(
    val message: String
) {
    companion object {
        fun of(msg: String): MessageResponse {
            return MessageResponse(msg)
        }
    }
}