package site.praytogether.praytogetherapi.modules.invitation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import site.praytogether.praytogetherapi.modules.invitation.presentation.dto.InvitationCreateRequest
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest

@DisplayName("Invitation 생성 통합 테스트")
class InvitationCreateIntegrateTest : IntegrateTest() {

    private lateinit var inviter: Member
    private lateinit var invitee: Member
    private lateinit var room: Room
    private lateinit var memberRoom: MemberRoom
    private lateinit var headers: HttpHeaders

    @BeforeEach
    fun setup() {
        // 초대하는 사람
        inviter = testUtils.createUniqueMember()
        memberRepository.save(inviter)

        // 초대받는 사람
        invitee = testUtils.createUniqueMember()
        memberRepository.save(invitee)

        // 방 생성
        room = testUtils.createUniqueRoom()
        roomRepository.save(room)

        // 초대하는 사람을 방에 추가
        memberRoom = testUtils.createUniqueMemberRoom(inviter, room)
        memberRoomRepository.save(memberRoom)

        headers = testUtils.createAuthHttpHeader(inviter)
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("초대 생성 시 201 Created 응답")
    fun `create invitation with valid input then return 201 created`() {
        // given
        val requestDto = InvitationCreateRequest(
            roomId = room.id!!,
            email = invitee.email
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when
        val response = restTemplate.postForEntity(
            INVITATIONS_API_URL,
            requestEntity,
            MessageResponse::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.message).contains("초대를 완료했습니다")

        // 생성된 Invitation 확인
        val invitations = invitationRepository.findAll()
        assertThat(invitations).hasSize(1)
        
        val invitation = invitations[0]
        assertThat(invitation.inviter.id).isEqualTo(inviter.id)
        assertThat(invitation.invitee.id).isEqualTo(invitee.id)
        assertThat(invitation.room.id).isEqualTo(room.id)
        assertThat(invitation.status).isEqualTo(InvitationStatus.PENDING)
    }

    @Test
    @DisplayName("중복 초대 시 409 Conflict 응답")
    fun `create duplicate invitation then return 409 conflict`() {
        // given
        // 첫 번째 초대 생성
        val firstRequest = InvitationCreateRequest(
            roomId = room.id!!,
            email = invitee.email
        )
        restTemplate.postForEntity(
            INVITATIONS_API_URL,
            HttpEntity(firstRequest, headers),
            MessageResponse::class.java
        )

        // when
        // 같은 사람을 같은 방에 다시 초대
        val duplicateRequest = InvitationCreateRequest(
            roomId = room.id!!,
            email = invitee.email
        )
        val response = restTemplate.postForEntity(
            INVITATIONS_API_URL,
            HttpEntity(duplicateRequest, headers),
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        
        // 초대가 하나만 생성되었는지 확인
        val invitations = invitationRepository.findAll()
        assertThat(invitations).hasSize(1)
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 초대 시 404 Not Found 응답")
    fun `create invitation with non-existent email then return 404`() {
        // given
        val requestDto = InvitationCreateRequest(
            roomId = room.id!!,
            email = "nonexistent@example.com"
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when
        val response = restTemplate.postForEntity(
            INVITATIONS_API_URL,
            requestEntity,
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        
        // 초대가 생성되지 않았는지 확인
        assertThat(invitationRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("방에 속하지 않은 사용자가 초대 시 403 Forbidden 응답")
    fun `create invitation by non-member then return 403`() {
        // given
        val otherMember = testUtils.createUniqueMember()
        memberRepository.save(otherMember)
        
        val otherHeaders = testUtils.createAuthHttpHeader(otherMember)
        
        val requestDto = InvitationCreateRequest(
            roomId = room.id!!,
            email = invitee.email
        )
        val requestEntity = HttpEntity(requestDto, otherHeaders)

        // when
        val response = restTemplate.postForEntity(
            INVITATIONS_API_URL,
            requestEntity,
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(invitationRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("이미 방에 속한 사용자를 초대 시 409 Conflict 응답")
    fun `create invitation for existing member then return 409`() {
        // given
        // invitee를 이미 방에 추가
        val inviteeMemberRoom = testUtils.createUniqueMemberRoom(invitee, room)
        memberRoomRepository.save(inviteeMemberRoom)

        val requestDto = InvitationCreateRequest(
            roomId = room.id!!,
            email = invitee.email
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when
        val response = restTemplate.postForEntity(
            INVITATIONS_API_URL,
            requestEntity,
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertThat(invitationRepository.findAll()).isEmpty()
    }
}