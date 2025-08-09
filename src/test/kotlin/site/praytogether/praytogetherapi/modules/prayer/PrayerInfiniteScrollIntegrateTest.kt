package site.praytogether.praytogetherapi.modules.prayer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.util.UriComponentsBuilder
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerTitleInfo
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerTitleInfiniteScrollResponse
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest
import java.time.Instant
import java.util.*
import java.util.stream.Stream

@DisplayName("기도 제목 무한 스크롤 테스트")
class PrayerInfiniteScrollIntegrateTest : IntegrateTest() {

    private lateinit var headers: HttpHeaders
    private lateinit var member: Member
    private lateinit var room: Room
    private lateinit var prayerTitle: PrayerTitle
    
    // 상수 - 실제 상수값이 필요한 경우 코드에서 찾아 설정
    private val prayerTitlesInfiniteScrollSize = 10
    private val testCnt = prayerTitlesInfiniteScrollSize * 3

    private val after = "after"
    private val roomId = "roomId"

    @BeforeEach
    fun setup() {
        member = testUtils.createUniqueMember()
        memberRepository.save(member)

        room = testUtils.createUniqueRoom()
        roomRepository.save(room)

        val memberRoom = testUtils.createUniqueMemberRoom(member, room)
        memberRoomRepository.save(memberRoom)

        prayerTitle = PrayerTitle(roomId = room.id!!, memberId = member.id!!, title = "test-title")
        prayerTitleRepository.save(prayerTitle)

        for (i in 0 until testCnt) {
            val prayerTitle = PrayerTitle(roomId = room.id!!, memberId = member.id!!, title = "test-title$i")
            prayerTitleRepository.save(prayerTitle)
        }
        headers = testUtils.createAuthHttpHeader(member)
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("providePrayerInfiniteScrollParameters")
    @DisplayName("다양한 파라미터 조합 요청시 기본값으로 정상 처리되어 200 OK 응답")
    fun `fetch prayer contents list with default values for different params then return 200 ok`(
        test: String, after: String?
    ) {
        // given
        val uri = UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
            .queryParam(roomId, room.id)
            .queryParam(this.after, after)
            .toUriString()
        val requestEntity = HttpEntity<Void>(headers)

        // when
        var responseEntity = restTemplate.exchange(
            uri, HttpMethod.GET, requestEntity, PrayerTitleInfiniteScrollResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("$test: 기도 내용 목록 무한 스크롤 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)
        
        var response = responseEntity.body
        assertThat(response)
            .describedAs("$test: 기도 내용 목록 무한 스크롤 API 응답 결과가 NULL 입니다.")
            .isNotNull

        var titles = response!!.prayerTitles
        assertThat(titles.size)
            .describedAs("$test: 기도 내용 목록 무한 스크롤 API 응답 결과 데이터가 없습니다.")
            .isEqualTo(prayerTitlesInfiniteScrollSize)

        assertThat(titles)
            .describedAs("$test: 기도 내용 목록이 createdTime 기준으로 내림차순 정렬되지 않았습니다.")
            .isSortedAccordingTo(Comparator.comparing(PrayerTitleInfo::createdTime).reversed())

        var repeatCount = 1
        while (titles.isNotEmpty()) {
            // next given
            val lastTitle = titles[titles.size - 1]
            val lastAfter = lastTitle.createdTime

            val nextUri = UriComponentsBuilder.fromUriString(PRAYERS_API_URL)
                .queryParam(roomId, room.id)
                .queryParam(this.after, lastAfter)
                .build()
                .toUriString()

            // next when
            responseEntity = restTemplate.exchange(
                nextUri, HttpMethod.GET, requestEntity, PrayerTitleInfiniteScrollResponse::class.java
            )

            // next then
            assertThat(responseEntity.statusCode)
                .describedAs("$test: $repeatCount 번째 요청 응답 코드가 200 OK 아닙니다.")
                .isEqualTo(HttpStatus.OK)

            response = responseEntity.body
            assertThat(response)
                .describedAs("$test: $repeatCount 번째 요청 응답 body가 null입니다.")
                .isNotNull
            
            titles = response!!.prayerTitles
            repeatCount++
        }

        // --- 최종 검증 ---
        assertThat(titles)
            .describedAs("$test: 마지막 요청 결과가 빈 리스트가 아닙니다.")
            .isEmpty()
    }

    companion object {
        @JvmStatic
        fun providePrayerInfiniteScrollParameters(): Stream<Arguments> {
            return Stream.of(
                // 기본값 테스트 (after=0)
                Arguments.of("after=0", "0"),
                Arguments.of("after null", null),
                Arguments.of("after 빈값", "")
            )
        }
    }
}