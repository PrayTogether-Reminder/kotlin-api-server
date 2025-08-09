package site.praytogether.praytogetherapi.modules.room

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
import org.springframework.web.util.UriComponentsBuilder
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomInfo
import site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomInfiniteScrollResponse
import site.praytogether.praytogetherapi.test_config.IntegrateTest
import java.util.stream.Stream

@DisplayName("Room 무한 스크롤 테스트")
class RoomInfiniteScrollIntegrateTest : IntegrateTest() {

    private lateinit var member: Member
    private lateinit var headers: HttpHeaders

    private val orderBy = "orderBy"
    private val after = "after"
    private val dir = "dir"
    
    private val orderByTime = "time"
    private val dirDesc = "desc"

    @BeforeEach
    fun setup() {
        // member1 생성
        member = testUtils.createUniqueMember()
        memberRepository.save(member)

        // room 30개 생성
        for (i in 0 until 30) {
            val testRoom = Room(name = "test${i + 1}", description = "test-description${i + 1}")
            roomRepository.save(testRoom)
        }

        val allRoom = roomRepository.findAll()

        // member1는 홀수 ID 방 추가
        for (i in 0 until 30) {
            val room = allRoom[i]
            if (room.id!! % 2 == 0L) continue
            val memberRoom = MemberRoom(
                member = member,
                room = room,
                role = RoomRole.MEMBER,
                isNotification = true
            )
            memberRoomRepository.save(memberRoom)
        }

        // member2 생성
        val member2 = testUtils.createUniqueMember()
        memberRepository.save(member2)
        
        // member2는 짝수 ID 방 추가
        for (i in 0 until 30) {
            val room = allRoom[i]
            if (room.id!! % 2 == 1L) continue
            val memberRoom = MemberRoom(
                member = member2,
                room = room,
                role = RoomRole.MEMBER,
                isNotification = true
            )
            memberRoomRepository.save(memberRoom)
        }
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideRoomInfiniteScrollParameters")
    @DisplayName("다양한 파라미터 조합 요청시 기본값으로 정상 처리되어 200 OK 응답")
    fun `fetch rooms list with default values for different params then return 200 ok`(
        test: String, orderBy: String?, after: String?, dir: String?
    ) {
        // given
        headers = testUtils.createAuthHttpHeader(member)
        val uri = UriComponentsBuilder.fromUriString(ROOMS_API_URL)
            .queryParam(this.orderBy, orderBy)
            .queryParam(this.after, after)
            .queryParam(this.dir, dir)
            .build()
            .toUriString()
        val requestEntity = HttpEntity<Void>(headers)

        // when
        val responseEntity = restTemplate.exchange(
            uri, HttpMethod.GET, requestEntity, RoomInfiniteScrollResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("방 목록 무한 스크롤 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)
        
        val response = responseEntity.body
        assertThat(response)
            .describedAs("방 목록 무한 스크롤 API 응답 결과가 NULL 입니다.")
            .isNotNull
        
        val rooms = response!!.rooms
        assertThat(rooms.size)
            .describedAs("방 목록 무한 스크롤 API 응답 결과 데이터가 없습니다.")
            .isGreaterThan(0)

        assertThat(rooms)
            .describedAs("방 목록이 joinedTime 기준으로 내림차순 정렬되지 않았습니다.")
            .isSortedAccordingTo { room1, room2 -> room2.joinedTime.compareTo(room1.joinedTime) }

        assertThat(rooms)
            .describedAs("모든 방의 ID가 홀수여야 합니다.")
            .allMatch { room -> room.id % 2 == 1L }
    }

    @Test
    @DisplayName("time 기준 desc 정렬로 연속 요청시 데이터가 정상적으로 페이징되고 마지막에는 빈 컬렉션 응답")
    fun `fetch rooms list with sequential requests time desc and empty final response`() {
        // given
        headers = testUtils.createAuthHttpHeader(member)
        var uri = UriComponentsBuilder.fromUriString(ROOMS_API_URL)
            .queryParam(orderBy, orderByTime)
            .queryParam(after, "0")
            .queryParam(dir, dirDesc)
            .build()
            .toUriString()
        val requestEntity = HttpEntity<Void>(headers)

        // 첫 번째 요청
        var responseEntity = restTemplate.exchange(
            uri, HttpMethod.GET, requestEntity, RoomInfiniteScrollResponse::class.java
        )

        assertThat(responseEntity.statusCode)
            .describedAs("첫 번째 요청: 방 목록 무한 스크롤 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        var rooms = responseEntity.body!!.rooms

        // 모든 방을 가져올 때까지 반복 요청
        while (rooms.isNotEmpty()) {
            // 현재 응답 검증
            assertThat(rooms)
                .describedAs("방 목록이 joinedTime 기준으로 내림차순 정렬되지 않았습니다.")
                .isSortedAccordingTo { room1, room2 -> room2.joinedTime.compareTo(room1.joinedTime) }

            assertThat(rooms)
                .describedAs("모든 방의 ID가 홀수여야 합니다.")
                .allMatch { room -> room.id % 2 == 1L }

            // 다음 요청 준비
            val lastRoom = rooms[rooms.size - 1]
            uri = UriComponentsBuilder.fromUriString(ROOMS_API_URL)
                .queryParam(orderBy, orderByTime)
                .queryParam(after, lastRoom.joinedTime.toString())
                .queryParam(dir, dirDesc)
                .build()
                .toUriString()

            // 다음 요청 수행
            responseEntity = restTemplate.exchange(
                uri, HttpMethod.GET, requestEntity, RoomInfiniteScrollResponse::class.java
            )

            assertThat(responseEntity.statusCode)
                .describedAs("연속 요청: 방 목록 무한 스크롤 API 응답 상태 코드가 200 OK가 아닙니다.")
                .isEqualTo(HttpStatus.OK)

            assertThat(responseEntity.body)
                .describedAs("연속 요청: 방 목록 무한 스크롤 API 응답 결과가 NULL 입니다.")
                .isNotNull

            rooms = responseEntity.body!!.rooms
        }

        assertThat(rooms)
            .describedAs("마지막 응답: 빈 컬렉션이어야 합니다.")
            .isEmpty()
    }

    companion object {
        @JvmStatic
        fun provideRoomInfiniteScrollParameters(): Stream<Arguments> {
            return Stream.of(
                // 기본값 테스트
                Arguments.of("기본값", "time", "0", "desc"),

                // orderBy 파라미터 변형
                Arguments.of("orderBy null", null, "0", "desc"),
                Arguments.of("orderBy 빈값", "", "0", "desc"),

                // after 파라미터 변형
                Arguments.of("after null", "time", null, "desc"),
                Arguments.of("after 빈값", "time", "", "desc"),

                // dir 파라미터 변형
                Arguments.of("dir null", "time", "0", null),
                Arguments.of("dir 빈값", "time", "0", ""),

                // 여러 파라미터 조합
                Arguments.of("모든 값 null", null, null, null), // 모든 파라미터 null
                Arguments.of("모든 값 빈값", "", "", "") // 모든 파라미터 빈 문자열
            )
        }
    }
}