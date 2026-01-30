package com.ryuqq.authhub.adapter.out.persistence.redis.auth.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * RefreshTokenCacheQueryAdapter 단위 테스트
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Query Adapter의 동작을 검증합니다.
 *
 * <p><strong>테스트 전략:</strong>
 *
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)
 *   <li>RedisTemplate은 Mock으로 대체
 *   <li>Query 메서드(find)의 동작 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("cache")
@Tag("persistence-layer")
@DisplayName("RefreshTokenCacheQueryAdapter 단위 테스트")
class RefreshTokenCacheQueryAdapterTest {

    private static final String TEST_USER_ID = UUID.randomUUID().toString();
    private static final String TEST_REFRESH_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.refreshToken";

    @Mock private RedisTemplate<String, String> redisTemplate;

    @Mock private ValueOperations<String, String> valueOperations;

    private RefreshTokenCacheQueryAdapter queryAdapter;

    @BeforeEach
    void setUp() {
        queryAdapter = new RefreshTokenCacheQueryAdapter(redisTemplate);
    }

    @Nested
    @DisplayName("findByUserId() 메서드는")
    class FindByUserIdMethod {

        @Test
        @DisplayName("캐시에서 refreshToken을 조회한다")
        void shouldReturnRefreshTokenWhenExists() {
            // Given
            UserId userId = UserId.of(TEST_USER_ID);
            String expectedKey = "refresh_token::user::" + TEST_USER_ID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(TEST_REFRESH_TOKEN);

            // When
            Optional<String> result = queryAdapter.findByUserId(userId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(TEST_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("캐시에 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotExists() {
            // Given
            UserId userId = UserId.of(TEST_USER_ID);
            String expectedKey = "refresh_token::user::" + TEST_USER_ID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(null);

            // When
            Optional<String> result = queryAdapter.findByUserId(userId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUserIdByToken() 메서드는")
    class FindUserIdByTokenMethod {

        @Test
        @DisplayName("캐시에서 UserId를 조회한다")
        void shouldReturnUserIdWhenExists() {
            // Given
            String expectedKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(TEST_USER_ID);

            // When
            Optional<UserId> result = queryAdapter.findUserIdByToken(TEST_REFRESH_TOKEN);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(TEST_USER_ID);
        }

        @Test
        @DisplayName("캐시에 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotExists() {
            // Given
            String expectedKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(null);

            // When
            Optional<UserId> result = queryAdapter.findUserIdByToken(TEST_REFRESH_TOKEN);

            // Then
            assertThat(result).isEmpty();
        }
    }
}
