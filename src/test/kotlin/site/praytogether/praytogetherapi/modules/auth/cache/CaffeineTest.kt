package site.praytogether.praytogetherapi.modules.auth.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Ticker
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import site.praytogether.praytogetherapi.common.constant.CoreConstant.OtpConstant.OTP_TTL_MINUTE
import site.praytogether.praytogetherapi.common.exception.AuthException
import site.praytogether.praytogetherapi.modules.auth.infrastructure.cache.OtpCacheCaffeine
import java.util.concurrent.TimeUnit

@DisplayName("OTP 캐시 (Caffeine) 단위 테스트")
class CaffeineTest {

    private lateinit var otpCache: OtpCacheCaffeine
    private lateinit var ticker: Ticker

    private val TEST_EMAIL = "test@example.com"
    private val TEST_OTP = "123456"
    private val TEST_OTP_UPDATED = "654321"

    @BeforeEach
    fun setUp() {
        ticker = mock(Ticker::class.java)

        otpCache = OtpCacheCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(OTP_TTL_MINUTE.toLong(), TimeUnit.MINUTES)
                .ticker(ticker)
                .build()
        )
    }

    @Test
    @DisplayName("캐시 저장 후 만료 시간이 지나면 AuthException 예외가 발생해야 함")
    fun `when cache expired then throw exception`() {
        // given
        otpCache.put(TEST_EMAIL, TEST_OTP)

        // 캐시 만료 설정
        `when`(ticker.read()).thenReturn(TimeUnit.MINUTES.toNanos(OTP_TTL_MINUTE.toLong()) + 100)

        // when & then
        // 만료된 캐시에서 값을 가져오려고 하면 AuthException 발생해야 함
        assertThatThrownBy { otpCache.get(TEST_EMAIL) }
            .`as`("만료된 캐시에 접근 시 AuthException이 발생해야 합니다.")
            .isInstanceOf(AuthException::class.java)
    }

    @Test
    @DisplayName("같은 키로 새로운 값 저장 시 이전 값은 제거되고 새 값만 유지되어야 함")
    fun `when cache overwritten then return latest value`() {
        // given
        otpCache.put(TEST_EMAIL, TEST_OTP)

        // when
        // 같은 키로 새로운 값 저장
        otpCache.put(TEST_EMAIL, TEST_OTP_UPDATED)

        // then
        // 새로운 값만 조회되어야 함
        val otp = otpCache.get(TEST_EMAIL)
        assertThat(otp)
            .`as`("같은 키로 저장한 최신 OTP 값이 조회되어야 합니다.")
            .isEqualTo(TEST_OTP_UPDATED)
            .`as`("이전에 저장한 OTP 값이 남아있으면 안됩니다.")
            .isNotEqualTo(TEST_OTP)
    }

    @Test
    @DisplayName("캐시에서 키를 삭제하면 해당 데이터는 더 이상 접근할 수 없어야 함")
    fun `when cache deleted then throw exception`() {
        // given
        otpCache.put(TEST_EMAIL, TEST_OTP)

        // when
        otpCache.delete(TEST_EMAIL)

        // then
        assertThatThrownBy { otpCache.get(TEST_EMAIL) }
            .`as`("삭제된 캐시 키에 접근 시 AuthException이 발생해야 합니다.")
            .isInstanceOf(AuthException::class.java)
    }

    @Test
    @DisplayName("캐시에 저장한 데이터는 만료 전까지 정상적으로 조회되어야 함")
    fun `when cache valid then return value`() {
        // given
        otpCache.put(TEST_EMAIL, TEST_OTP)

        // when
        val retrievedOtp = otpCache.get(TEST_EMAIL)

        // then
        assertThat(retrievedOtp)
            .`as`("저장한 OTP 값이 정확히 조회되어야 합니다.")
            .isEqualTo(TEST_OTP)
    }
}