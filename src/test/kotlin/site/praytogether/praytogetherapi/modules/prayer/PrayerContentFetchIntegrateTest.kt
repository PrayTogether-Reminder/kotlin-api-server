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
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerContent
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerContentInfo
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerContentsResponse
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest
import java.util.*

@DisplayName("기도 내용 조회 테스트")
class PrayerContentFetchIntegrateTest : IntegrateTest() {

    private lateinit var headers: HttpHeaders
    private lateinit var member: Member
    private lateinit var room: Room
    private lateinit var prayerTitle: PrayerTitle
    private val testCnt = 5

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
        prayerTitle = testUtils.createUniquePrayerTitle(room.id!!, member.id!!)
        prayerTitleRepository.save(prayerTitle)

        // 기도 내용 추가
        val prayerContent = PrayerContent(
            prayerTitleId = prayerTitle.id!!,
            content = "test-prayer-content"
        )
        prayerContentRepository.save(prayerContent)
        headers = testUtils.createAuthHttpHeader(member)
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("기도 제목에 해당하는 기도 내용 목록을 조회하여 200 OK 응답")
    fun `fetch prayer contents list then return 200 ok`() {
        // given
        // 회원 및 기도 내용 추가
        for (i in 1 until testCnt) {
            val newMember = testUtils.createUniqueMember()
            memberRepository.save(newMember)

            val prayerContent = PrayerContent(
                prayerTitleId = prayerTitle.id!!,
                content = "test-prayer-content${i + 'ㄱ'.code}"
            )
            prayerContentRepository.save(prayerContent)
        }
        
        val uri = UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .path("/{titleId}/contents")
            .buildAndExpand(prayerTitle.id)
            .toUriString()
        val requestEntity = HttpEntity<Void>(headers)

        // when
        val responseEntity = restTemplate.exchange(
            uri, HttpMethod.GET, requestEntity, PrayerContentsResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("기도 내용 목록 조회 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)
        
        val response = responseEntity.body
        assertThat(response)
            .describedAs("기도 내용 목록 조회 API 응답 결과가 NULL 입니다.")
            .isNotNull

        val prayerContents = response!!.prayerContents
        assertThat(prayerContents.size)
            .describedAs("기도 내용 목록 조회 API 응답 결과 데이터 개수가 기대값과 다릅니다.")
            .isEqualTo(testCnt)

        assertThat(prayerContents)
            .describedAs("기도 내용 목록이 memberName 기준으로 오름차순 정렬되지 않았습니다.")
            .isSortedAccordingTo(Comparator.comparing(PrayerContentInfo::memberName))
    }
}