package site.praytogether.praytogetherapi.test_config

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import site.praytogether.praytogetherapi.security.service.JwtService
import site.praytogether.praytogetherapi.security.model.PrayTogetherPrincipal
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole

@Component
class TestUtils(
    private val jwtService: JwtService
) {

    companion object {
        private var emailUniqueId = 0
        private var roomUniqueId = 0
        private var prayerTitleUniqueId = 0
    }

    fun createUniqueMember(): Member {
        val id = emailUniqueId++
        return Member.create("test$id", "test$id@test.com", "test")
    }

    fun createUniquePrayerTitle(roomId: Long, memberId: Long): PrayerTitle {
        return PrayerTitle.create(
            title = "test-prayer-title${prayerTitleUniqueId++}",
            roomId = roomId,
            memberId = memberId
        )
    }

    fun createUniqueMemberRoom(member: Member, room: Room): MemberRoom {
        return MemberRoom.create(
            member = member,
            room = room,
            role = RoomRole.OWNER
        )
    }

    fun createUniqueRoom(): Room {
        val id = roomUniqueId++
        return Room.create("test-Room$id", "test-description$id")
    }

    fun createAuthHttpHeader(member: Member): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val principal = PrayTogetherPrincipal(
            id = member.id!!,
            email = member.email,
            password = member.password
        )
        val accessToken = jwtService.issueAccessToken(principal)
        headers.setBearerAuth(accessToken)
        return headers
    }
}