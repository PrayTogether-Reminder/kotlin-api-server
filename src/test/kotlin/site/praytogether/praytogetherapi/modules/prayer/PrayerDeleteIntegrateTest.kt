package site.praytogether.praytogetherapi.modules.prayer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerContent
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.stream.Stream

@DisplayName("기도 삭제 통합 테스트")
class PrayerDeleteIntegrateTest : IntegrateTest() {

    private lateinit var member: Member
    private lateinit var room: Room
    private lateinit var memberRoom: MemberRoom
    private lateinit var headers: HttpHeaders
    private lateinit var prayerTitle: PrayerTitle
    private lateinit var prayerContent: PrayerContent

    @BeforeEach
    fun setup() {
        // 회원 생성
        member = testUtils.createUniqueMember()
        memberRepository.save(member)

        // 방 생성
        room = testUtils.createUniqueRoom()
        roomRepository.save(room)

        // 방 연관관계 생성
        memberRoom = testUtils.createUniqueMemberRoom(member, room)
        memberRoomRepository.save(memberRoom)

        // 인증 헤더 생성
        headers = testUtils.createAuthHttpHeader(member)

        // 기도 제목 생성
        prayerTitle = PrayerTitle(roomId = room.id!!, memberId = member.id!!, title = "test-prayer-title")
        prayerTitleRepository.save(prayerTitle)

        // 기도 내용 생성
        for (i in 0 until 5) {
            val newMember = testUtils.createUniqueMember()
            memberRepository.save(newMember)

            prayerContent = PrayerContent(
                prayerTitleId = prayerTitle.id!!,
                content = "test-prayer-content$i"
            )

            prayerContentRepository.save(prayerContent)
        }
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("기도 제목 삭제 시 제목+내용 삭제 후 200 OK 응답")
    fun `delete prayer then return 200 ok`() {
        // given
        val deleteRequestEntity = HttpEntity<Void>(headers)
        val url = "$PRAYERS_API_URL/${prayerTitle.id}"

        // when
        val responseEntity = restTemplate.exchange(
            url, HttpMethod.DELETE, deleteRequestEntity, MessageResponse::class.java
        )

        // then
        // 삭제 응답 상태 검증
        assertThat(responseEntity.statusCode)
            .describedAs("기도 삭제 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        // 기도 제목이 삭제되었는지 확인
        assertThat(prayerTitleRepository.findById(prayerTitle.id!!))
            .describedAs("기도 제목이 삭제되지 않았습니다.")
            .isNull()

        // 연관된 기도 내용이 삭제되었는지 확인
        val remainingContents = prayerContentRepository.findAll()
        assertThat(remainingContents)
            .describedAs("연관된 기도 내용이 삭제되지 않았습니다.")
            .isEmpty()
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidPrayerDeleteParameters")
    @DisplayName("기도 삭제 시 유효하지 않은 ID인 경우 400 Bad Request 응답")
    fun `delete prayer with invalid id then return 400 bad request`(test: String, encodedUrl: String) {
        // given
        val deleteRequestEntity = HttpEntity<Void>(headers)
        val url = "$PRAYERS_API_URL/$encodedUrl"

        // when
        val response = restTemplate.exchange(
            url, HttpMethod.DELETE, deleteRequestEntity, Any::class.java
        )

        // then
        assertThat(response.statusCode)
            .describedAs("유효하지 않은 ID로 기도 삭제 요청 시 400 Bad Request가 반환되어야 합니다.")
            .isEqualTo(HttpStatus.BAD_REQUEST)

        assertThat(response.body).isNotNull
    }

    @Test
    @DisplayName("다른 방의 회원이 기도 삭제 요청 시 404 Not Found 응답")
    fun `delete prayer by member from different room then return 404 not found`() {
        // given
        // 새로운 회원 생성
        val anotherMember = testUtils.createUniqueMember()
        memberRepository.save(anotherMember)

        // 새로운 회원의 인증 헤더 생성
        val anotherHeaders = testUtils.createAuthHttpHeader(anotherMember)
        val deleteRequestEntity = HttpEntity<Void>(anotherHeaders)
        val url = "$PRAYERS_API_URL/${prayerTitle.id}"

        // when
        val response = restTemplate.exchange(
            url, HttpMethod.DELETE, deleteRequestEntity, Any::class.java
        )

        // then
        assertThat(response.statusCode)
            .describedAs("다른 방의 회원이 기도 삭제 요청 시 404 Not Found가 반환되어야 합니다.")
            .isEqualTo(HttpStatus.NOT_FOUND)

        assertThat(response.body).isNotNull
    }

    companion object {
        @JvmStatic
        fun provideInvalidPrayerDeleteParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("음수 ID", URLEncoder.encode("-1", StandardCharsets.UTF_8)),
                Arguments.of("0 ID", URLEncoder.encode("0", StandardCharsets.UTF_8)),
                Arguments.of("문자열 ID", URLEncoder.encode("abc", StandardCharsets.UTF_8)),
                Arguments.of("특수문자 ID", URLEncoder.encode("!@#", StandardCharsets.UTF_8)),
                Arguments.of("소수점 ID", URLEncoder.encode("1.5", StandardCharsets.UTF_8)),
                Arguments.of("공백 ID", URLEncoder.encode(" ", StandardCharsets.UTF_8)),
                Arguments.of("null", "null") // null은 특별히 처리
            )
        }
    }
}