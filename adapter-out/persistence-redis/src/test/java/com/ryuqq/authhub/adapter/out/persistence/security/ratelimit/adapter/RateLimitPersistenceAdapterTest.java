package com.ryuqq.authhub.adapter.out.persistence.security.ratelimit.adapter;

import com.ryuqq.authhub.application.security.ratelimit.port.out.LoadRateLimitPort;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

/**
 * RateLimitPersistenceAdapter 단위 테스트.
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Adapter의 동작을 검증합니다.
 * Redis나 Spring Context 없이 빠르게 실행됩니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)</li>
 *   <li>RedisTemplate은 Mock으로 대체</li>
 *   <li>각 Port 메서드의 동작 검증</li>
 *   <li>Null 안전성 및 유효성 검증</li>
 *   <li>원자성 보장 검증 (INCR + TTL)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RateLimitPersistenceAdapter 단위 테스트")
class RateLimitPersistenceAdapterTest {

    @Mock
    private RedisTemplate<String, Integer> redisTemplate;

    @Mock
    private ValueOperations<String, Integer> valueOperations;

    @InjectMocks
    private RateLimitPersistenceAdapter adapter;

    private static final String TEST_IP = "192.168.0.1";
    private static final String TEST_USER_ID = "user-123";
    private static final String TEST_ENDPOINT = "/api/auth/login";
    private static final long TEST_TTL_SECONDS = 60L;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Nested
    @DisplayName("loadCurrentCount 테스트")
    class LoadCurrentCountTests {

        @Test
        @DisplayName("카운트가 존재하면 해당 값을 반환한다")
        void loadCurrentCount_ExistingKey_ReturnsCount() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(valueOperations.get(expectedKey)).willReturn(5);

            // when
            final int count = adapter.loadCurrentCount(TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED);

            // then
            assertThat(count).isEqualTo(5);
            then(valueOperations).should(times(1)).get(expectedKey);
        }

        @Test
        @DisplayName("카운트가 존재하지 않으면 0을 반환한다")
        void loadCurrentCount_NonExistingKey_ReturnsZero() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(valueOperations.get(expectedKey)).willReturn(null);

            // when
            final int count = adapter.loadCurrentCount(TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED);

            // then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("USER_BASED 타입으로 올바른 Key를 생성한다")
        void loadCurrentCount_UserBased_GeneratesCorrectKey() {
            // given
            final String expectedKey = "rate_limit:user:user-123:/api/auth/login";
            given(valueOperations.get(expectedKey)).willReturn(10);

            // when
            final int count = adapter.loadCurrentCount(TEST_USER_ID, TEST_ENDPOINT, RateLimitType.USER_BASED);

            // then
            assertThat(count).isEqualTo(10);
            then(valueOperations).should(times(1)).get(expectedKey);
        }

        @Test
        @DisplayName("identifier가 null이면 예외를 발생시킨다")
        void loadCurrentCount_NullIdentifier_ThrowsException() {
            assertThatThrownBy(() -> adapter.loadCurrentCount(null, TEST_ENDPOINT, RateLimitType.IP_BASED))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("identifier cannot be null");
        }

        @Test
        @DisplayName("endpoint가 null이면 예외를 발생시킨다")
        void loadCurrentCount_NullEndpoint_ThrowsException() {
            assertThatThrownBy(() -> adapter.loadCurrentCount(TEST_IP, null, RateLimitType.IP_BASED))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("endpoint cannot be null");
        }

        @Test
        @DisplayName("type이 null이면 예외를 발생시킨다")
        void loadCurrentCount_NullType_ThrowsException() {
            assertThatThrownBy(() -> adapter.loadCurrentCount(TEST_IP, TEST_ENDPOINT, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("type cannot be null");
        }
    }

    @Nested
    @DisplayName("loadCountWithTtl 테스트")
    class LoadCountWithTtlTests {

        @Test
        @DisplayName("카운트와 TTL을 함께 조회한다")
        void loadCountWithTtl_ExistingKey_ReturnsCountWithTtl() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(valueOperations.get(expectedKey)).willReturn(5);
            given(redisTemplate.getExpire(expectedKey, TimeUnit.SECONDS)).willReturn(45L);

            // when
            final LoadRateLimitPort.CountWithTtl result = adapter.loadCountWithTtl(
                    TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED
            );

            // then
            assertThat(result.getCount()).isEqualTo(5);
            assertThat(result.getTtlSeconds()).isEqualTo(45L);
            assertThat(result.hasTtl()).isTrue();
        }

        @Test
        @DisplayName("키가 없으면 카운트 0과 TTL -1을 반환한다")
        void loadCountWithTtl_NonExistingKey_ReturnsDefaultValues() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(valueOperations.get(expectedKey)).willReturn(null);
            given(redisTemplate.getExpire(expectedKey, TimeUnit.SECONDS)).willReturn(-2L);

            // when
            final LoadRateLimitPort.CountWithTtl result = adapter.loadCountWithTtl(
                    TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED
            );

            // then
            assertThat(result.getCount()).isZero();
            assertThat(result.getTtlSeconds()).isEqualTo(-2L);
            assertThat(result.hasTtl()).isFalse();
        }
    }

    @Nested
    @DisplayName("incrementAndGet 테스트")
    class IncrementAndGetTests {

        @Test
        @DisplayName("첫 증가 시 카운트를 1로 증가시키고 TTL을 설정한다")
        void incrementAndGet_FirstIncrement_SetsTtl() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(valueOperations.increment(expectedKey)).willReturn(1L);
            given(redisTemplate.expire(expectedKey, TEST_TTL_SECONDS, TimeUnit.SECONDS)).willReturn(true);

            // when
            final long newCount = adapter.incrementAndGet(TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED, TEST_TTL_SECONDS);

            // then
            assertThat(newCount).isEqualTo(1L);
            then(valueOperations).should(times(1)).increment(expectedKey);
            then(redisTemplate).should(times(1)).expire(expectedKey, TEST_TTL_SECONDS, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("두 번째 이상 증가 시 TTL을 재설정하지 않는다")
        void incrementAndGet_SubsequentIncrement_DoesNotSetTtl() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(valueOperations.increment(expectedKey)).willReturn(5L);

            // when
            final long newCount = adapter.incrementAndGet(TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED, TEST_TTL_SECONDS);

            // then
            assertThat(newCount).isEqualTo(5L);
            then(valueOperations).should(times(1)).increment(expectedKey);
            then(redisTemplate).should(never()).expire(anyString(), anyLong(), any(TimeUnit.class));
        }

        @Test
        @DisplayName("ttlSeconds가 0 이하면 예외를 발생시킨다")
        void incrementAndGet_InvalidTtl_ThrowsException() {
            assertThatThrownBy(() -> adapter.incrementAndGet(TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED, 0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ttlSeconds must be positive");
        }

        @Test
        @DisplayName("increment가 null을 반환하면 예외를 발생시킨다")
        void incrementAndGet_NullResult_ThrowsException() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(valueOperations.increment(expectedKey)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> adapter.incrementAndGet(TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED, TEST_TTL_SECONDS))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Failed to increment count for key");
        }
    }

    @Nested
    @DisplayName("incrementByAndGet 테스트")
    class IncrementByAndGetTests {

        @Test
        @DisplayName("지정된 값만큼 증가시키고 TTL을 설정한다")
        void incrementByAndGet_BatchIncrement_Success() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            final long incrementBy = 3L;
            given(valueOperations.increment(expectedKey, incrementBy)).willReturn(incrementBy);
            given(redisTemplate.expire(expectedKey, TEST_TTL_SECONDS, TimeUnit.SECONDS)).willReturn(true);

            // when
            final long newCount = adapter.incrementByAndGet(
                    TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED, incrementBy, TEST_TTL_SECONDS
            );

            // then
            assertThat(newCount).isEqualTo(incrementBy);
            then(valueOperations).should(times(1)).increment(expectedKey, incrementBy);
            then(redisTemplate).should(times(1)).expire(expectedKey, TEST_TTL_SECONDS, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("incrementBy가 0 이하면 예외를 발생시킨다")
        void incrementByAndGet_InvalidIncrementBy_ThrowsException() {
            assertThatThrownBy(() -> adapter.incrementByAndGet(
                    TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED, 0L, TEST_TTL_SECONDS
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("incrementBy must be positive");
        }
    }

    @Nested
    @DisplayName("reset 테스트")
    class ResetTests {

        @Test
        @DisplayName("카운트를 삭제하고 true를 반환한다")
        void reset_ExistingKey_ReturnsTrue() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(redisTemplate.delete(expectedKey)).willReturn(true);

            // when
            final boolean result = adapter.reset(TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED);

            // then
            assertThat(result).isTrue();
            then(redisTemplate).should(times(1)).delete(expectedKey);
        }

        @Test
        @DisplayName("키가 없으면 false를 반환한다")
        void reset_NonExistingKey_ReturnsFalse() {
            // given
            final String expectedKey = "rate_limit:ip:192.168.0.1:/api/auth/login";
            given(redisTemplate.delete(expectedKey)).willReturn(false);

            // when
            final boolean result = adapter.reset(TEST_IP, TEST_ENDPOINT, RateLimitType.IP_BASED);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("resetAll 테스트")
    class ResetAllTests {

        @Test
        @DisplayName("특정 식별자의 모든 엔드포인트 카운트를 삭제한다")
        void resetAll_MultipleKeys_DeletesAll() {
            // given
            final String pattern = "rate_limit:ip:192.168.0.1:*";
            final Set<String> keys = Set.of(
                    "rate_limit:ip:192.168.0.1:/api/login",
                    "rate_limit:ip:192.168.0.1:/api/users"
            );
            given(redisTemplate.keys(pattern)).willReturn(keys);
            given(redisTemplate.delete(keys)).willReturn(2L);

            // when
            final int deletedCount = adapter.resetAll(TEST_IP, RateLimitType.IP_BASED);

            // then
            assertThat(deletedCount).isEqualTo(2);
            then(redisTemplate).should(times(1)).keys(pattern);
            then(redisTemplate).should(times(1)).delete(keys);
        }

        @Test
        @DisplayName("매칭되는 키가 없으면 0을 반환한다")
        void resetAll_NoMatchingKeys_ReturnsZero() {
            // given
            final String pattern = "rate_limit:ip:192.168.0.1:*";
            given(redisTemplate.keys(pattern)).willReturn(Set.of());

            // when
            final int deletedCount = adapter.resetAll(TEST_IP, RateLimitType.IP_BASED);

            // then
            assertThat(deletedCount).isZero();
            then(redisTemplate).should(never()).delete(anyString());
        }
    }

    @Nested
    @DisplayName("resetByEndpoint 테스트")
    class ResetByEndpointTests {

        @Test
        @DisplayName("특정 엔드포인트의 모든 카운트를 삭제한다")
        void resetByEndpoint_MultipleKeys_DeletesAll() {
            // given
            final String pattern = "rate_limit:ip:*:/api/auth/login";
            final Set<String> keys = Set.of(
                    "rate_limit:ip:192.168.0.1:/api/auth/login",
                    "rate_limit:ip:192.168.0.2:/api/auth/login"
            );
            given(redisTemplate.keys(pattern)).willReturn(keys);
            given(redisTemplate.delete(keys)).willReturn(2L);

            // when
            final int deletedCount = adapter.resetByEndpoint(TEST_ENDPOINT, RateLimitType.IP_BASED);

            // then
            assertThat(deletedCount).isEqualTo(2);
            then(redisTemplate).should(times(1)).keys(pattern);
            then(redisTemplate).should(times(1)).delete(keys);
        }
    }
}
