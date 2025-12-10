package com.ryuqq.authhub.adapter.out.persistence.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Duration;
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
 * RefreshTokenCacheAdapter 단위 테스트
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Adapter의 동작을 검증합니다. Redis나 Spring Context 없이 빠르게 실행됩니다.
 *
 * <p><strong>테스트 전략:</strong>
 *
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)
 *   <li>RedisTemplate은 Mock으로 대체
 *   <li>각 Port 메서드의 동작 검증
 *   <li>양방향 조회 검증 (userId ↔ token)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("cache")
@Tag("persistence-layer")
@DisplayName("RefreshTokenCacheAdapter 단위 테스트")
class RefreshTokenCacheAdapterTest {

    private static final UUID TEST_USER_UUID =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String TEST_REFRESH_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.refreshToken";
    private static final long EXPIRES_IN_SECONDS = 1_209_600L; // 14 days

    @Mock private RedisTemplate<String, String> redisTemplate;

    @Mock private ValueOperations<String, String> valueOperations;

    private RefreshTokenCacheAdapter cacheAdapter;

    @BeforeEach
    void setUp() {
        cacheAdapter = new RefreshTokenCacheAdapter(redisTemplate);
    }

    @Nested
    @DisplayName("save() 메서드는")
    class SaveMethod {

        @Test
        @DisplayName("userId로 조회할 수 있도록 캐시에 저장한다")
        void shouldSaveRefreshTokenWithUserIdKey() {
            // Given
            UserId userId = UserId.of(TEST_USER_UUID);
            String expectedUserKey = "refresh_token::user::" + TEST_USER_UUID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            // When
            cacheAdapter.save(userId, TEST_REFRESH_TOKEN, EXPIRES_IN_SECONDS);

            // Then
            verify(valueOperations)
                    .set(
                            eq(expectedUserKey),
                            eq(TEST_REFRESH_TOKEN),
                            eq(Duration.ofSeconds(EXPIRES_IN_SECONDS)));
        }

        @Test
        @DisplayName("token으로 역조회할 수 있도록 캐시에 저장한다")
        void shouldSaveUserIdWithTokenKey() {
            // Given
            UserId userId = UserId.of(TEST_USER_UUID);
            String expectedTokenKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            // When
            cacheAdapter.save(userId, TEST_REFRESH_TOKEN, EXPIRES_IN_SECONDS);

            // Then
            verify(valueOperations)
                    .set(
                            eq(expectedTokenKey),
                            eq(TEST_USER_UUID.toString()),
                            eq(Duration.ofSeconds(EXPIRES_IN_SECONDS)));
        }
    }

    @Nested
    @DisplayName("findByUserId() 메서드는")
    class FindByUserIdMethod {

        @Test
        @DisplayName("캐시에서 refreshToken을 조회한다")
        void shouldReturnRefreshTokenWhenExists() {
            // Given
            UserId userId = UserId.of(TEST_USER_UUID);
            String expectedKey = "refresh_token::user::" + TEST_USER_UUID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(TEST_REFRESH_TOKEN);

            // When
            Optional<String> result = cacheAdapter.findByUserId(userId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(TEST_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("캐시에 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotExists() {
            // Given
            UserId userId = UserId.of(TEST_USER_UUID);
            String expectedKey = "refresh_token::user::" + TEST_USER_UUID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(null);

            // When
            Optional<String> result = cacheAdapter.findByUserId(userId);

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
            when(valueOperations.get(expectedKey)).thenReturn(TEST_USER_UUID.toString());

            // When
            Optional<UserId> result = cacheAdapter.findUserIdByToken(TEST_REFRESH_TOKEN);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(TEST_USER_UUID);
        }

        @Test
        @DisplayName("캐시에 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotExists() {
            // Given
            String expectedKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(expectedKey)).thenReturn(null);

            // When
            Optional<UserId> result = cacheAdapter.findUserIdByToken(TEST_REFRESH_TOKEN);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteByUserId() 메서드는")
    class DeleteByUserIdMethod {

        @Test
        @DisplayName("userId로 캐시를 삭제한다")
        void shouldDeleteByUserIdKey() {
            // Given
            UserId userId = UserId.of(TEST_USER_UUID);
            String userKey = "refresh_token::user::" + TEST_USER_UUID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(userKey)).thenReturn(TEST_REFRESH_TOKEN);

            // When
            cacheAdapter.deleteByUserId(userId);

            // Then
            verify(redisTemplate).delete(userKey);
        }

        @Test
        @DisplayName("연관된 토큰 키도 함께 삭제한다")
        void shouldDeleteAssociatedTokenKey() {
            // Given
            UserId userId = UserId.of(TEST_USER_UUID);
            String userKey = "refresh_token::user::" + TEST_USER_UUID;
            String tokenKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(userKey)).thenReturn(TEST_REFRESH_TOKEN);

            // When
            cacheAdapter.deleteByUserId(userId);

            // Then
            verify(redisTemplate).delete(userKey);
            verify(redisTemplate).delete(tokenKey);
        }
    }

    @Nested
    @DisplayName("deleteByToken() 메서드는")
    class DeleteByTokenMethod {

        @Test
        @DisplayName("token으로 캐시를 삭제한다")
        void shouldDeleteByTokenKey() {
            // Given
            String tokenKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(tokenKey)).thenReturn(TEST_USER_UUID.toString());

            // When
            cacheAdapter.deleteByToken(TEST_REFRESH_TOKEN);

            // Then
            verify(redisTemplate).delete(tokenKey);
        }

        @Test
        @DisplayName("연관된 사용자 키도 함께 삭제한다")
        void shouldDeleteAssociatedUserKey() {
            // Given
            String tokenKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;
            String userKey = "refresh_token::user::" + TEST_USER_UUID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(tokenKey)).thenReturn(TEST_USER_UUID.toString());

            // When
            cacheAdapter.deleteByToken(TEST_REFRESH_TOKEN);

            // Then
            verify(redisTemplate).delete(tokenKey);
            verify(redisTemplate).delete(userKey);
        }
    }
}
