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
import org.springframework.http.HttpStatus
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.modules.room.presentation.dto.CreateRoomRequest
import site.praytogether.praytogetherapi.test_config.IntegrateTest
import java.util.stream.Stream

@DisplayName("Room 생성 통합 테스트")
class RoomCreateIntegrateTest : IntegrateTest() {

    private lateinit var member: Member
    private lateinit var headers: HttpHeaders

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
    @DisplayName("Room 생성 시 201 Created 응답")
    fun `create room with valid input then return 201 created`() {
        // given - Request Body 준비
        val requestDto = CreateRoomRequest(
            name = "테스트 방",
            description = "테스트를 위한 방입니다."
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when - API 요청
        val response = restTemplate.postForEntity(
            ROOMS_API_URL,
            requestEntity,
            MessageResponse::class.java
        )

        // then
        // API 응답 검증
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        // 생성된 Room 확인
        val allRooms = roomRepository.findAll()
        assertThat(allRooms).isNotEmpty

        val createdRoom = allRooms[0]
        assertThat(createdRoom.name).isEqualTo("테스트 방")
        assertThat(createdRoom.description).isEqualTo("테스트를 위한 방입니다.")

        // 생성된 Member-Room 확인
        val memberRooms = memberRoomRepository.findAll()
        assertThat(memberRooms).isNotEmpty
        assertThat(memberRooms[0].member.id).isEqualTo(member.id)
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideInvalidRoomCreateParameters")
    @DisplayName("Room 생성 시 유효하지 않은 파라미터인 경우 400 Bad Request 응답")
    fun `create room with invalid input then return 400 bad request`(
        test: String,
        name: String?,
        description: String?
    ) {
        // given
        val requestDto = CreateRoomRequest(
            name = name ?: "",
            description = description ?: ""
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when
        val response = restTemplate.postForEntity(
            ROOMS_API_URL,
            requestEntity,
            MessageResponse::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        val errorResponse = response.body
        assertThat(errorResponse).isNotNull

        // 방이 생성되지 않았는지 확인
        assertThat(roomRepository.findAll()).isEmpty()
    }

    companion object {
        @JvmStatic
        fun provideInvalidRoomCreateParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("방 이름 빈 문자열", "", "정상적인 방 설명입니다."),
                Arguments.of("방 이름 공백만 포함", "   ", "정상적인 방 설명입니다."),
                Arguments.of("방 이름 최대 길이 초과(151자)", "a".repeat(151), "정상적인 방 설명입니다."),
                Arguments.of("방 설명 빈 문자열", "정상적인 방 이름", ""),
                Arguments.of("방 설명 최대 길이 초과(766자)", "정상적인 방 이름", "a".repeat(766))
            )
        }
    }
}