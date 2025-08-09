package site.praytogether.praytogetherapi.modules.memberroom.domain.model

import java.time.Instant

data class RoomInfo(
    val id: Long,
    val name: String,
    var memberCnt: Long,
    val description: String,
    val joinedTime: Instant,
    val isNotification: Boolean
) {
    constructor(
        id: Long,
        name: String,
        description: String,
        createdTime: Instant,
        isNotification: Boolean
    ) : this(
        id = id,
        name = name,
        memberCnt = 0L,
        description = description,
        joinedTime = createdTime,
        isNotification = isNotification
    )
}