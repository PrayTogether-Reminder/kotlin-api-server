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
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.presentation.dto.CreateRoomRequest
import site.praytogether.praytogetherapi.test_config.IntegrateTest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.stream.Stream

@DisplayName("Room 삭제 통합 테스트")
class RoomDeleteIntegrateTest : IntegrateTest() {
    
    private lateinit var member: Member
    private lateinit var headers: HttpHeaders
    private lateinit var testRoom: Room

    @BeforeEach
    fun setup() {
        member = testUtils.createUniqueMember()
        memberRepository.save(member)
        headers = testUtils.createAuthHttpHeader(member)
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("Room 삭제 시 200 OK 응답 / 연관된 MemberRoom 삭제")
    fun `delete room when room exists then return 200 ok`() {
        // given
        // 방 생성
        val requestDto = CreateRoomRequest(
            name = "삭제 예정 방",
            description = "테스트를 위해 삭제하려는 방 입니다."
        )
        val requestEntity = HttpEntity(requestDto, headers)
        restTemplate.postForEntity(ROOMS_API_URL, requestEntity, MessageResponse::class.java)
        
        // 방 정보 획득
        val allRooms = roomRepository.findAll()
        testRoom = allRooms[0]

        val deleteRequestEntity = HttpEntity<Void>(headers)

        // when
        val deleteResponse = restTemplate.exchange(
            "$ROOMS_API_URL/${testRoom.id}",
            HttpMethod.DELETE,
            deleteRequestEntity,
            MessageResponse::class.java
        )

        // then
        // 삭제 응답 상태 검증
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)

        // memberRoom 삭제 확인
        val allMemberRooms = memberRoomRepository.findAll()
        assertThat(allMemberRooms).isEmpty()
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidRoomDeleteParameters")
    @DisplayName("Room 삭제시 유효하지 않은 ID인 경우 400 Bad Request 응답")
    fun `delete room with invalid id then return 400 bad request`(test: String, encodedUrl: String) {
        // given
        val deleteRequestEntity = HttpEntity<Void>(headers)
        val url = "$ROOMS_API_URL/$encodedUrl"

        // when
        val response = restTemplate.exchange(
            url, HttpMethod.DELETE, deleteRequestEntity, Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull
    }

    @Test
    @DisplayName("없는 Room ID로 삭제 요청 시 404 Not Found 응답")
    fun `delete room with nonexistent id then return 404 not found`() {
        // given
        val nonExistentId = 999999L // 존재하지 않는 ID
        val deleteRequestEntity = HttpEntity<Void>(headers)

        // when
        val response = restTemplate.exchange(
            "$ROOMS_API_URL/$nonExistentId",
            HttpMethod.DELETE,
            deleteRequestEntity,
            MessageResponse::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        val errorResponse = response.body
        assertThat(errorResponse).isNotNull
    }

    companion object {
        @JvmStatic
        fun provideInvalidRoomDeleteParameters(): Stream<Arguments> {
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