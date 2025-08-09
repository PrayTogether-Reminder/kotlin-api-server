package site.praytogether.praytogetherapi.modules.invitation

import com.fasterxml.jackson.core.JsonProcessingException
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
import site.praytogether.praytogetherapi.modules.invitation.domain.entity.Invitation
import site.praytogether.praytogetherapi.modules.invitation.domain.valueobject.InvitationStatus
import site.praytogether.praytogetherapi.modules.invitation.presentation.dto.InvitationStatusUpdateRequest
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest
import java.util.stream.Stream

@DisplayName("방 초대 상태 변경 통합 테스트")
class InvitationUpdateStatusIntegrateTest : IntegrateTest() {
    
    private lateinit var memberInviter: Member
    private lateinit var memberInvitee: Member
    private lateinit var room: Room
    private lateinit var headers: HttpHeaders
    private lateinit var invitation: Invitation

    @BeforeEach
    fun setup() {
        // 초대자 회원 생성
        memberInviter = testUtils.createUniqueMember()
        memberRepository.save(memberInviter)

        // 초대받는 회원 생성
        memberInvitee = testUtils.createUniqueMember()
        memberRepository.save(memberInvitee)

        // 방 생성
        room = testUtils.createUniqueRoom()
        roomRepository.save(room)

        // 초대자-방 연관관계 생성
        val memberRoom = testUtils.createUniqueMemberRoom(memberInviter, room)
        memberRoomRepository.save(memberRoom)

        // 초대장 생성
        invitation = Invitation.create(memberInviter, memberInvitee, room)
        invitationRepository.save(invitation)

        // 생성된 초대장 조회
        invitation = invitationRepository.findAll()[0]

        // 테스트용 인증 헤더 설정
        headers = testUtils.createAuthHttpHeader(memberInvitee)
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @ParameterizedTest(name = "[{index}] 초대장 상태를 {0}으로 변경하면 200 OK 응답")
    @MethodSource("provideValidInvitationStatusUpdateParameters")
    @DisplayName("초대장 상태 업데이트 시 200 OK 응답")
    fun `update invitation status then return 200 ok`(test: String, updatedStatus: InvitationStatus) {
        // given
        val request = InvitationStatusUpdateRequest(status = updatedStatus)
        val requestEntity = HttpEntity(request, headers)
        val url = "$INVITATIONS_API_URL/${invitation.id}"

        val memberRoomCnt = memberRoomRepository.findAll().size

        // when
        val responseEntity = restTemplate.exchange(
            url, HttpMethod.PATCH, requestEntity, MessageResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("초대장 상태 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        // DB에서 초대장 확인
        val updatedInvitation = invitationRepository.findById(invitation.id!!)!!

        // 상태 확인
        assertThat(updatedInvitation.status)
            .describedAs("초대장 상태가 변경되지 않았습니다.")
            .isEqualTo(updatedStatus)

        // 응답 시간 확인
        assertThat(updatedInvitation.responseTime)
            .describedAs("초대장 응답 시간이 설정되지 않았습니다.")
            .isNotNull

        // 메시지 응답 확인
        val response = responseEntity.body
        assertThat(response)
            .describedAs("초대장 상태 변경 API 응답 결과가 NULL 입니다.")
            .isNotNull

        assertThat(response!!.message)
            .describedAs("초대장 상태 변경 API 응답 메시지가 예상과 다릅니다.")
            .contains(updatedStatus.koreanName)

        // 방 참가 인원 수 확인
        val allMemberRoom = memberRoomRepository.findAll()
        when (updatedStatus) {
            InvitationStatus.ACCEPTED -> {
                // 방 인원 증가
                assertThat(allMemberRoom.size)
                    .describedAs("초대장 수락시 방 참가 인원이 달라져야(1 증가해야) 합니다.")
                    .isEqualTo(memberRoomCnt + 1)
            }
            InvitationStatus.REJECTED -> {
                // 방 인원 유지
                assertThat(allMemberRoom.size)
                    .describedAs("초대장 거절시 방 참가 인원이 이전과 동일해야 합니다.")
                    .isEqualTo(memberRoomCnt)
            }
            else -> {
                // 다른 상태는 처리하지 않음
            }
        }
    }

    @ParameterizedTest(name = "[{index}] {0}인 경우 400 Bad Request 응답")
    @MethodSource("provideInvalidInvitationStatusUpdateParameters")
    @DisplayName("초대장 상태 업데이트 시 잘못된 값이면 400 Bad Request 응답")
    fun `update invitation status with invalid input then return 400 bad request`(
        test: String, invalidStatus: Any?
    ) {
        // given
        val requestMap = mutableMapOf<String, Any?>()
        requestMap["status"] = invalidStatus
        val requestBody = objectMapper.writeValueAsString(requestMap)
        val memberRoomCnt = memberRoomRepository.findAll().size

        val requestEntity = HttpEntity(requestBody, headers)
        val url = "$INVITATIONS_API_URL/${invitation.id}"

        // when
        val responseEntity = restTemplate.exchange(
            url, HttpMethod.PATCH, requestEntity, Any::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("잘못된 초대장 상태 변경 요청 시 400 Bad Request가 반환되어야 합니다.")
            .isEqualTo(HttpStatus.BAD_REQUEST)

        val response = responseEntity.body
        assertThat(response).isNotNull

        // DB에서 초대장 확인 - 상태가 변경되지 않아야 함
        val unchangedInvitation = invitationRepository.findById(invitation.id!!)!!

        // 상태가 PENDING 유지 확인
        assertThat(unchangedInvitation.status)
            .describedAs("잘못된 요청에도 초대장 상태가 변경되었습니다.")
            .isEqualTo(InvitationStatus.PENDING)

        // 응답 시간이 여전히 null인지 확인
        assertThat(unchangedInvitation.responseTime)
            .describedAs("잘못된 요청에도 초대장 응답 시간이 설정되었습니다.")
            .isNull()

        // 아무도 방에 초대되지 않음
        val allMemberRoom = memberRoomRepository.findAll()
        assertThat(allMemberRoom.size).isEqualTo(memberRoomCnt)
    }

    @ParameterizedTest(name = "[{index}] 이미 수락한 초대장을 {0}(으)로 변경 시도하면 400 Bad Request 응답")
    @MethodSource("provideAlreadyAcceptedInvitationParameters")
    @DisplayName("이미 수락한 초대장 상태 변경 시 400 Bad Request 응답")
    fun `update already accepted invitation status then return 400 bad request`(
        test: String, updatedStatus: InvitationStatus
    ) {
        // given
        invitation.accept()
        invitationRepository.save(invitation)

        // 이미 응답 시간이 설정되어 있는지 확인
        assertThat(invitationRepository.findById(invitation.id!!)!!.responseTime)
            .describedAs("초대장 응답 시간이 설정되지 않았습니다.")
            .isNotNull

        // 다시 상태 변경 요청
        val request = InvitationStatusUpdateRequest(status = updatedStatus)
        val requestEntity = HttpEntity(request, headers)
        val url = "$INVITATIONS_API_URL/${invitation.id}"

        // when
        val responseEntity = restTemplate.exchange(
            url, HttpMethod.PATCH, requestEntity, Any::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("이미 수락한 초대장 상태 변경 요청 시 400 Bad Request가 반환되어야 합니다.")
            .isEqualTo(HttpStatus.BAD_REQUEST)

        val response = responseEntity.body
        assertThat(response)
            .describedAs("예외 응답이 null입니다.")
            .isNotNull

        // DB에서 초대장 확인 - 상태가 변경되지 않아야 함
        val unchangedInvitation = invitationRepository.findById(invitation.id!!)!!

        // 상태가 ACCEPTED 그대로 유지되는지 확인
        assertThat(unchangedInvitation.status)
            .describedAs("이미 수락한 초대장의 상태가 변경되었습니다.")
            .isEqualTo(InvitationStatus.ACCEPTED)
    }

    companion object {
        @JvmStatic
        fun provideValidInvitationStatusUpdateParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("수락", InvitationStatus.ACCEPTED),
                Arguments.of("거절", InvitationStatus.REJECTED)
            )
        }

        @JvmStatic
        fun provideInvalidInvitationStatusUpdateParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("status가 빈 문자열", ""),
                Arguments.of("status가 소문자 accepted", "accepted"),
                Arguments.of("status가 소문자 rejected", "rejected"),
                Arguments.of("status가 존재하지 않는 값", "UNKNOWN_STATUS"),
                Arguments.of("status가 숫자", 123),
                Arguments.of("status가 불리언", true),
                Arguments.of("status가 null", null)
            )
        }

        @JvmStatic
        fun provideAlreadyAcceptedInvitationParameters(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("수락", InvitationStatus.ACCEPTED),
                Arguments.of("거절", InvitationStatus.REJECTED)
            )
        }
    }
}