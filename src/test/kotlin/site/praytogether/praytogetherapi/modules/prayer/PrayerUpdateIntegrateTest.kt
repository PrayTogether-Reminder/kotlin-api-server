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
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.common.dto.MessageResponse
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerContent
import site.praytogether.praytogetherapi.modules.prayer.domain.entity.PrayerTitle
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerUpdateContent
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.PrayerUpdateRequest
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest

@DisplayName("기도 변경 통합 테스트")
class PrayerUpdateIntegrateTest : IntegrateTest() {

    private lateinit var member: Member
    private lateinit var room: Room
    private lateinit var memberRoom: MemberRoom
    private lateinit var headers: HttpHeaders
    private lateinit var prayerTitle: PrayerTitle
    private val prayerContents = mutableListOf<PrayerContent>()

    private val testCnt = 5

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
        prayerTitle = PrayerTitle(roomId = room.id!!, memberId = member.id!!, title = "original-prayer-title")
        prayerTitleRepository.save(prayerTitle)

        // 기도 내용 생성
        for (i in 0 until testCnt) {
            val content = PrayerContent(
                prayerTitleId = prayerTitle.id!!,
                content = "original-prayer-content-$i"
            )
            prayerContentRepository.save(content)
            prayerContents.add(content)
        }
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("기도(제목+내용) 변경 시 200 OK 응답")
    fun `update prayer then return 200 ok`() {
        // given
        val newTitle = "updated-prayer-title"
        val newContent = "updated-prayer-content"

        val updateContents = listOf(
            PrayerUpdateContent(
                id = prayerContents[0].id,
                memberId = member.id!!,
                memberName = member.name,
                content = newContent
            )
        )

        val requestDto = PrayerUpdateRequest(
            title = newTitle,
            contents = updateContents
        )

        val requestEntity = HttpEntity(requestDto, headers)
        val url = "$PRAYERS_API_URL/${prayerTitle.id}"

        // when
        val responseEntity = restTemplate.exchange(
            url, HttpMethod.PUT, requestEntity, MessageResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("기도 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        // 변경된 기도 제목 확인
        val updatedTitle = prayerTitleRepository.findById(prayerTitle.id!!)!!
        assertThat(updatedTitle.title)
            .describedAs("기도 제목이 업데이트되지 않았습니다.")
            .isEqualTo(newTitle)

        // 변경된 기도 내용 확인
        val updatedContents = prayerContentRepository.findByPrayerTitleId(prayerTitle.id!!)
        assertThat(updatedContents.size)
            .describedAs("변경된 기도 내용의 개수가 예상과 다릅니다.")
            .isEqualTo(1)
        assertThat(updatedContents[0].content)
            .describedAs("기도 내용이 업데이트되지 않았습니다.")
            .isEqualTo(newContent)
    }

    @Test
    @DisplayName("기도 내용 추가 변경 시 200 OK 응답")
    fun `update prayer add content then return 200 ok`() {
        // given
        val newTitle = "updated-prayer-title"
        val existingContent = "existing-content"
        val newContent = "new-content"

        // 기존 content 업데이트
        val updateContents = mutableListOf<PrayerUpdateContent>()
        updateContents.add(
            PrayerUpdateContent(
                id = prayerContents[0].id,
                memberId = member.id!!,
                memberName = member.name,
                content = existingContent
            )
        )

        // 새로운 content 추가
        updateContents.add(
            PrayerUpdateContent(
                memberId = member.id!!,
                memberName = member.name,
                content = newContent
            )
        )

        val requestDto = PrayerUpdateRequest(
            title = newTitle,
            contents = updateContents
        )

        val requestEntity = HttpEntity(requestDto, headers)
        val url = "$PRAYERS_API_URL/${prayerTitle.id}"

        // when
        val responseEntity = restTemplate.exchange(
            url, HttpMethod.PUT, requestEntity, MessageResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("기도 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        // 변경된 기도 제목 확인
        val updatedTitle = prayerTitleRepository.findById(prayerTitle.id!!)!!
        assertThat(updatedTitle.title)
            .describedAs("기도 제목이 업데이트되지 않았습니다.")
            .isEqualTo(newTitle)

        // 변경된 기도 내용 확인
        val updatedContents = prayerContentRepository.findByPrayerTitleId(prayerTitle.id!!)
        assertThat(updatedContents.size)
            .describedAs("변경된 기도 내용의 개수가 예상과 다릅니다.")
            .isEqualTo(2)

        // 내용 확인
        val hasExistingContent = updatedContents.any { it.content == existingContent }
        val hasNewContent = updatedContents.any { it.content == newContent }

        assertThat(hasExistingContent)
            .describedAs("기존 기도 내용이 업데이트되지 않았습니다.")
            .isTrue
        assertThat(hasNewContent)
            .describedAs("새로운 기도 내용이 추가되지 않았습니다.")
            .isTrue
    }

    @Test
    @DisplayName("기도 내용 삭제 변경 시 200 OK 응답")
    fun `update prayer remove content then return 200 ok`() {
        // given
        val newTitle = "updated-prayer-title"

        // 빈 내용 목록으로 업데이트 (기존 내용 삭제)
        val updateContents = emptyList<PrayerUpdateContent>()

        val requestDto = PrayerUpdateRequest(
            title = newTitle,
            contents = updateContents
        )

        val requestEntity = HttpEntity(requestDto, headers)
        val url = "$PRAYERS_API_URL/${prayerTitle.id}"

        // when
        val responseEntity = restTemplate.exchange(
            url, HttpMethod.PUT, requestEntity, MessageResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("기도 변경 API 응답 상태 코드가 200 OK가 아닙니다.")
            .isEqualTo(HttpStatus.OK)

        // 변경된 기도 제목 확인
        val updatedTitle = prayerTitleRepository.findById(prayerTitle.id!!)!!
        assertThat(updatedTitle.title)
            .describedAs("기도 제목이 업데이트되지 않았습니다.")
            .isEqualTo(newTitle)

        // 변경된 기도 내용 확인
        val updatedContents = prayerContentRepository.findByPrayerTitleId(prayerTitle.id!!)
        assertThat(updatedContents.size)
            .describedAs("기도 내용이 모두 삭제되지 않았습니다.")
            .isZero
    }

    @Test
    @DisplayName("존재하지 않는 기도 제목 ID로 변경 요청 시 404 Not Found 응답")
    fun `update prayer with nonexistent id then return 404 not found`() {
        // given
        val nonExistentId = 999999L

        val requestDto = PrayerUpdateRequest(
            title = "updated-prayer-title",
            contents = emptyList()
        )

        val requestEntity = HttpEntity(requestDto, headers)
        val url = "$PRAYERS_API_URL/$nonExistentId"

        // when
        val responseEntity = restTemplate.exchange(
            url, HttpMethod.PUT, requestEntity, MessageResponse::class.java
        )

        // then
        assertThat(responseEntity.statusCode)
            .describedAs("존재하지 않는 기도 제목 ID로 변경 요청 시 404 Not Found가 반환되어야 합니다.")
            .isEqualTo(HttpStatus.NOT_FOUND)
    }
}