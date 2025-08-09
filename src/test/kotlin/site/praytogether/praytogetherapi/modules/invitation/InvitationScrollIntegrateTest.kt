package site.praytogether.praytogetherapi.modules.invitation

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
import site.praytogether.praytogetherapi.modules.invitation.domain.entity.Invitation
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import site.praytogether.praytogetherapi.modules.invitation.presentation.dto.InvitationInfo
import site.praytogether.praytogetherapi.modules.invitation.presentation.dto.InvitationInfoScrollResponse
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest

@DisplayName("방 초대 목록 조회 통합 테스트")
class InvitationScrollIntegrateTest : IntegrateTest() {

    private lateinit var inviteeMember: Member
    private lateinit var headers: HttpHeaders
    private val invitationCount = 5

    @BeforeEach
    fun setup() {
        // 초대 받을 회원 생성
        inviteeMember = testUtils.createUniqueMember()
        memberRepository.save(inviteeMember)
        headers = testUtils.createAuthHttpHeader(inviteeMember)

        // 초대한 회원들 생성 및 초대장 생성
        for (i in 0 until invitationCount) {
            // 초대자 생성
            val inviterMember = testUtils.createUniqueMember()
            memberRepository.save(inviterMember)

            // 방 생성
            val room = testUtils.createUniqueRoom()
            roomRepository.save(room)

            // 초대장 생성
            val invitation = Invitation.create(inviterMember, inviteeMember, room)
            invitationRepository.save(invitation)
        }
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("회원의 초대 목록 조회 시 200 OK 응답 및 초대 목록 확인")
    fun `fetch invitation scroll then return 200 ok`() {
        // given
        val requestEntity = HttpEntity<Void>(headers)

        // when
        val responseEntity = restTemplate.exchange(
            INVITATIONS_API_URL, HttpMethod.GET, requestEntity, InvitationInfoScrollResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("초대 목록 조회 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        val response = responseEntity.body
        assertThat(response)
            .describedAs("초대 목록 조회 API 응답 결과가 NULL 입니다.")
            .isNotNull

        val invitations = response!!.invitations
        assertThat(invitations)
            .describedAs("초대 목록 조회 API 응답 결과 데이터가 NULL 입니다.")
            .isNotNull

        assertThat(invitations.size)
            .describedAs("초대 목록 조회 API 응답 결과 개수가 기대값과 다릅니다.")
            .isEqualTo(invitationCount)

        assertThat(invitations)
            .describedAs("초대 목록이 createdTime 기준으로 오름차순 정렬되지 않았습니다.")
            .isSortedAccordingTo { i1, i2 -> i1.createdTime.compareTo(i2.createdTime) }

        // 초대 항목 데이터 검증
        for (invitation in invitations) {
            assertThat(invitation.invitationId)
                .describedAs("초대 ID가 NULL 입니다.")
                .isNotNull

            assertThat(invitation.inviterName)
                .describedAs("초대자 이름이 NULL 입니다.")
                .isNotNull

            assertThat(invitation.roomName)
                .describedAs("방 이름이 NULL 입니다.")
                .isNotNull

            assertThat(invitation.roomDescription)
                .describedAs("방 설명이 NULL 입니다.")
                .isNotNull

            assertThat(invitation.createdTime)
                .describedAs("초대 생성 시간이 NULL 입니다.")
                .isNotNull
        }
    }
}