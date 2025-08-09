package site.praytogether.praytogetherapi.modules.prayer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import site.praytogether.praytogetherapi.modules.member.domain.entity.Member
import site.praytogether.praytogetherapi.modules.memberroom.domain.entity.MemberRoom
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.CreatePrayerRequest
import site.praytogether.praytogetherapi.modules.prayer.presentation.dto.CreatePrayerResponse
import site.praytogether.praytogetherapi.modules.room.domain.entity.Room
import site.praytogether.praytogetherapi.test_config.IntegrateTest

@DisplayName("Prayer 생성 통합 테스트")
class PrayerCreateIntegrateTest : IntegrateTest() {

    private lateinit var member: Member
    private lateinit var room: Room
    private lateinit var memberRoom: MemberRoom
    private lateinit var headers: HttpHeaders

    @BeforeEach
    fun setup() {
        // 테스트 데이터 준비
        member = testUtils.createUniqueMember()
        memberRepository.save(member)

        room = testUtils.createUniqueRoom()
        roomRepository.save(room)

        memberRoom = testUtils.createUniqueMemberRoom(member, room)
        memberRoomRepository.save(memberRoom)

        headers = testUtils.createAuthHttpHeader(member)
    }

    @AfterEach
    fun cleanup() {
        cleanRepository()
    }

    @Test
    @DisplayName("Prayer 생성 시 201 Created 응답")
    fun `create prayer with valid input then return 201 created`() {
        // given
        val requestDto = CreatePrayerRequest(
            roomId = room.id!!,
            title = "오늘의 기도",
            contents = listOf("하나님 감사합니다", "건강하게 해주세요")
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when
        val response = restTemplate.postForEntity(
            PRAYERS_API_URL,
            requestEntity,
            CreatePrayerResponse::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.prayerId).isNotNull

        // 생성된 Prayer Title 확인
        val prayerTitles = prayerTitleRepository.findAll()
        assertThat(prayerTitles).hasSize(1)
        assertThat(prayerTitles[0].title).isEqualTo("오늘의 기도")
        assertThat(prayerTitles[0].roomId).isEqualTo(room.id)
        assertThat(prayerTitles[0].memberId).isEqualTo(member.id)

        // 생성된 Prayer Contents 확인
        val prayerContents = prayerContentRepository.findByPrayerTitleId(prayerTitles[0].id!!)
        assertThat(prayerContents).hasSize(2)
        assertThat(prayerContents.map { it.content }).containsExactly(
            "하나님 감사합니다",
            "건강하게 해주세요"
        )
    }

    @Test
    @DisplayName("존재하지 않는 방에 Prayer 생성 시 404 Not Found 응답")
    fun `create prayer with non-existent room then return 404`() {
        // given
        val requestDto = CreatePrayerRequest(
            roomId = 999L,
            title = "오늘의 기도",
            contents = listOf("기도 내용")
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when
        val response = restTemplate.postForEntity(
            PRAYERS_API_URL,
            requestEntity,
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)

        // Prayer가 생성되지 않았는지 확인
        assertThat(prayerTitleRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("빈 제목으로 Prayer 생성 시 400 Bad Request 응답")
    fun `create prayer with empty title then return 400`() {
        // given
        val requestDto = CreatePrayerRequest(
            roomId = room.id!!,
            title = "",
            contents = listOf("기도 내용")
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when
        val response = restTemplate.postForEntity(
            PRAYERS_API_URL,
            requestEntity,
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(prayerTitleRepository.findAll()).isEmpty()
    }

    @Test
    @DisplayName("빈 내용 리스트로 Prayer 생성 시 400 Bad Request 응답")
    fun `create prayer with empty contents then return 400`() {
        // given
        val requestDto = CreatePrayerRequest(
            roomId = room.id!!,
            title = "오늘의 기도",
            contents = emptyList()
        )
        val requestEntity = HttpEntity(requestDto, headers)

        // when
        val response = restTemplate.postForEntity(
            PRAYERS_API_URL,
            requestEntity,
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(prayerTitleRepository.findAll()).isEmpty()
    }
}