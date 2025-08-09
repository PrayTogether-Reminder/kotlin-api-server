package site.praytogether.praytogetherapi.modules.room

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.modules.room.domain.valueobject.RoomRole
import site.praytogether.praytogetherapi.modules.room.presentation.dto.CreateRoomRequest
import site.praytogether.praytogetherapi.modules.room.presentation.dto.RoomMemberResponse
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.test_config.IntegrateTest

@DisplayName("방 참가자 조회 통합 테스트")
class RoomMemberFetchIntegrateTest : IntegrateTest() {

    private lateinit var member: Member
    private lateinit var headers: HttpHeaders
    private val MEMBERS_URL = "/members"
    private lateinit var room: Room
    private val memberCount = 10

    @BeforeEach
    fun setup() {
        // 회원 생성 및 JWT 설정
        member = testUtils.createUniqueMember()
        memberRepository.save(member)
        headers = testUtils.createAuthHttpHeader(member)

        // 방 생성
        val createRequest = CreateRoomRequest(
            name = "test-name",
            description = "test-description"
        )
        val requestEntity = HttpEntity(createRequest, headers)
        restTemplate.postForEntity(
            ROOMS_API_URL,
            requestEntity,
            MessageResponse::class.java
        )

        // 방 정보 획득
        val allRoom = roomRepository.findAll()
        room = allRoom[0]

        // 방 참가자 생성 (본인 포함 총 memberCount 명)
        val memberRoomList = mutableListOf<MemberRoom>()
        val memberList = mutableListOf<Member>()
        for (i in 0 until memberCount - 1) {
            val newMember = testUtils.createUniqueMember()
            memberList.add(newMember)
            val memberRoom = MemberRoom(
                member = newMember,
                room = room,
                role = RoomRole.MEMBER,
                isNotification = true
            )
            memberRoomList.add(memberRoom)
        }
        memberRepository.saveAll(memberList)
        memberRoomRepository.saveAll(memberRoomList)
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("Room 참가자 조회 시 200 OK 응답")
    fun `fetch room members then return 200 ok`() {
        // when
        // API 요청
        val requestEntity = HttpEntity<Any>(headers)
        val responseEntity = restTemplate.exchange(
            "$ROOMS_API_URL/${room.id}$MEMBERS_URL",
            HttpMethod.GET,
            requestEntity,
            RoomMemberResponse::class.java
        )

        // then
        // 응답 상태 코드 검증
        assertThat(responseEntity.statusCode)
            .`as`("방 참가자 조회 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        val response = responseEntity.body

        // 응답 결과 검증
        val members = response!!.members
        assertThat(members).`as`("방 참가자 조회 API 응답 결과가 NULL 입니다.").isNotNull
        assertThat(members.size).`as`("방 참가자 조회 API 응답 결과, 방 참가자 수가 예상과 다릅니다.").isEqualTo(memberCount)
        
        // Owner member 확인
        assertThat(members.any { it.id == member.id && it.name == member.name })
            .`as`("방을 생성한 Member가 요청 방에 포함되지 않고 있습니다.")
            .isTrue()
    }
}