package site.praytogether.praytogetherapi.modules.prayer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.util.UriComponentsBuilder
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.notification.domain.entity.PrayerCompletionNotification
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerCompletion
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerCompletionCreateRequest
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest

@DisplayName("기도 완료 처리 통합 테스트")
class PrayerCompletionIntegrateTest : IntegrateTest() {

    private lateinit var member: Member
    private lateinit var headers: HttpHeaders
    private lateinit var room: Room
    private lateinit var prayerTitle: PrayerTitle
    private val completionUrlFormat = "/{prayerTitleId}/completion"

    // 추가 회원 생성
    private val additionalMembersCount = 3
    private lateinit var additionalMembers: Array<Member>

    @BeforeEach
    fun setup() {
        // 회원 생성
        member = testUtils.createUniqueMember()
        memberRepository.save(member)

        // 방 생성
        room = testUtils.createUniqueRoom()
        roomRepository.save(room)

        // 회원-방 연관관계 생성
        val memberRoom = testUtils.createUniqueMemberRoom(member, room)
        memberRoomRepository.save(memberRoom)

        // 기도 제목 생성
        prayerTitle = PrayerTitle.create("test-prayer-title", room.id!!, member.id!!)
        prayerTitleRepository.save(prayerTitle)

        // 추가 회원 생성 및 방에 참여시키기
        additionalMembers = Array(additionalMembersCount) { 
            testUtils.createUniqueMember() 
        }
        
        for (i in 0 until additionalMembersCount) {
            memberRepository.save(additionalMembers[i])

            val additionalMemberRoom = testUtils.createUniqueMemberRoom(additionalMembers[i], room)
            memberRoomRepository.save(additionalMemberRoom)
        }

        // 인증 헤더 생성
        headers = testUtils.createAuthHttpHeader(member)
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("기도 완료 처리 시 200 OK 응답 및 알림 생성 확인")
    fun `complete prayer then create notifications and return 200 ok`() {
        // given
        val uri = UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .path(completionUrlFormat)
            .buildAndExpand(prayerTitle.id)
            .toUriString()

        val request = PrayerCompletionCreateRequest(roomId = room.id!!)
        val requestEntity = HttpEntity(request, headers)

        // when
        val responseEntity = restTemplate.exchange(
            uri, HttpMethod.POST, requestEntity, MessageResponse::class.java
        )

        // then
        // 응답 검증
        assertThat(responseEntity.statusCode)
            .describedAs("기도 완료 처리 API 응답 상태 코드가 200 OK이 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        val response = responseEntity.body
        assertThat(response)
            .describedAs("기도 완료 처리 API 응답 결과가 NULL 입니다.")
            .isNotNull

        // 기도 완료 엔티티 생성 검증
        val completions = prayerCompletionRepository.findAll()
        assertThat(completions)
            .describedAs("기도 완료 정보가 저장되지 않았습니다.")
            .isNotEmpty

        assertThat(completions.size)
            .describedAs("기도 완료 정보 개수가 예상과 다릅니다.")
            .isEqualTo(1)

        val completion = completions[0]
        assertThat(completion.prayerId)
            .describedAs("기도 완료 정보의 기도자 ID가 예상과 다릅니다.")
            .isEqualTo(member.id)

        assertThat(completion.prayerTitle.id)
            .describedAs("기도 완료 정보의 기도 제목 ID가 예상과 다릅니다.")
            .isEqualTo(prayerTitle.id)

        // 알림 생성 검증
        val notifications = prayerCompletionNotificationRepository.findAll()
        assertThat(notifications)
            .describedAs("기도 완료 알림이 생성되지 않았습니다.")
            .isNotEmpty

        assertThat(notifications.size)
            .describedAs("생성된 알림 개수가 예상과 다릅니다. (알림은 자신을 제외한 다른 멤버들에게만 전송됨)")
            .isEqualTo(additionalMembersCount)

        for (notification in notifications) {
            assertThat(notification.senderId)
                .describedAs("알림의 발신자 ID가 예상과 다릅니다.")
                .isEqualTo(member.id)

            assertThat(notification.prayerTitleId)
                .describedAs("알림의 기도 제목 ID가 예상과 다릅니다.")
                .isEqualTo(prayerTitle.id)

            assertThat(notification.message)
                .describedAs("알림의 메시지가 NULL입니다.")
                .isNotNull

            // 알림 메시지 형식 검증
            val expectedMessage = "${member.name}님이 ${prayerTitle.title} 기도제목으로 기도했습니다.\n기도로 함께 동참해 주세요!"
            assertThat(notification.message)
                .describedAs("알림 메시지가 예상 형식과 다릅니다.")
                .isEqualTo(expectedMessage)
        }
    }
}