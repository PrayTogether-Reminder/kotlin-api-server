package site.praytogether.praytogetherapi.test_config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import site.praytogether.praytogetherapi.modules.invitation.domain.repository.InvitationRepository
import site.praytogether.praytogetherapi.modules.member.domain.repository.MemberRepository
import site.praytogether.praytogetherapi.modules.memberroom.domain.repository.MemberRoomRepository
import site.praytogether.praytogetherapi.modules.notification.domain.repository.PrayerCompletionNotificationRepository
import site.praytogether.praytogetherapi.modules.prayer.domain.repository.PrayerCompletionRepository
import site.praytogether.praytogetherapi.modules.prayer.domain.repository.PrayerContentRepository
import site.praytogether.praytogetherapi.modules.prayer.domain.repository.PrayerTitleRepository
import site.praytogether.praytogetherapi.modules.room.domain.repository.RoomRepository
import site.praytogether.praytogetherapi.modules.fcmtoken.domain.repository.FcmTokenRepository

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrateTestConfig::class)
abstract class IntegrateTest {
    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var roomRepository: RoomRepository

    @Autowired
    protected lateinit var memberRepository: MemberRepository

    @Autowired
    protected lateinit var memberRoomRepository: MemberRoomRepository

    @Autowired
    protected lateinit var prayerTitleRepository: PrayerTitleRepository

    @Autowired
    protected lateinit var prayerContentRepository: PrayerContentRepository

    @Autowired
    protected lateinit var invitationRepository: InvitationRepository

    @Autowired
    protected lateinit var prayerCompletionRepository: PrayerCompletionRepository

    @Autowired
    protected lateinit var prayerCompletionNotificationRepository: PrayerCompletionNotificationRepository

    @Autowired
    protected lateinit var fcmTokenRepository: FcmTokenRepository

    @Autowired
    protected lateinit var testUtils: TestUtils

    companion object {
        const val API_VERSION = "/api"
        const val ROOMS_API_URL = "$API_VERSION/rooms"
        const val PRAYERS_API_URL = "$API_VERSION/prayers"
        const val MEMBERS_API_URL = "$API_VERSION/members"
        const val INVITATIONS_API_URL = "$API_VERSION/invitations"
        const val AUTH_API_URL = "$API_VERSION/auth"
    }

    protected fun cleanRepository() {
        // delete order is very important
        prayerCompletionRepository.deleteAll()
        prayerCompletionNotificationRepository.deleteAll()
        invitationRepository.deleteAll()
        prayerContentRepository.deleteAll()
        prayerTitleRepository.deleteAll()
        memberRoomRepository.deleteAll()
        roomRepository.deleteAll()
        fcmTokenRepository.deleteAll()
        memberRepository.deleteAll()
    }
}