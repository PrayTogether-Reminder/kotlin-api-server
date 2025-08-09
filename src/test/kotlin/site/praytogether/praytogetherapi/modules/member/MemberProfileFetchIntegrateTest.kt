package site.praytogether.praytogetherapi.modules.member

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
import site.praytogether.praytogetherapi.modules.member.presentation.dto.MemberResponse
import site.praytogether.praytogetherapi.test_config.IntegrateTest

@DisplayName("Member 프로필 조회 통합 테스트")
class MemberProfileFetchIntegrateTest : IntegrateTest() {

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
    @DisplayName("자신의 프로필 조회 시 200 OK 응답")
    fun `fetch my profile then return 200 OK`() {
        // given
        val requestEntity = HttpEntity<Any>(headers)

        // when
        val response = restTemplate.exchange(
            "$MEMBERS_API_URL/profile",
            HttpMethod.GET,
            requestEntity,
            MemberResponse::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        
        val memberResponse = response.body!!
        assertThat(memberResponse.id).isEqualTo(member.id)
        assertThat(memberResponse.name).isEqualTo(member.name)
        assertThat(memberResponse.email).isEqualTo(member.email)
    }

    @Test
    @DisplayName("인증 헤더 없이 프로필 조회 시 401 Unauthorized 응답")
    fun `fetch profile without auth header then return 401`() {
        // given
        val requestEntity = HttpEntity<Any>(HttpHeaders())

        // when
        val response = restTemplate.exchange(
            "$MEMBERS_API_URL/profile",
            HttpMethod.GET,
            requestEntity,
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    @DisplayName("특정 회원 ID로 프로필 조회 시 200 OK 응답")
    fun `fetch member profile by id then return 200 OK`() {
        // given
        val requestEntity = HttpEntity<Any>(headers)

        // when
        val response = restTemplate.exchange(
            "$MEMBERS_API_URL/${member.id}",
            HttpMethod.GET,
            requestEntity,
            MemberResponse::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        
        val memberResponse = response.body!!
        assertThat(memberResponse.id).isEqualTo(member.id)
        assertThat(memberResponse.name).isEqualTo(member.name)
        assertThat(memberResponse.email).isEqualTo(member.email)
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID로 프로필 조회 시 404 Not Found 응답")
    fun `fetch non-existent member profile then return 404`() {
        // given
        val requestEntity = HttpEntity<Any>(headers)

        // when
        val response = restTemplate.exchange(
            "$MEMBERS_API_URL/999",
            HttpMethod.GET,
            requestEntity,
            Any::class.java
        )

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}