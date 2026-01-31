package com.ryuqq.authhub.adapter.out.persistence.redis.auth.adapter;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.domain.user.id.UserId;
import java.time.Duration;
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
 * RefreshTokenCacheCommandAdapter 단위 테스트
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Command Adapter의 동작을 검증합니다.
 *
 * <p><strong>테스트 전략:</strong>
 *
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)
 *   <li>RedisTemplate은 Mock으로 대체
 *   <li>Command 메서드(save, delete)의 동작 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("cache")
@Tag("persistence-layer")
@DisplayName("RefreshTokenCacheCommandAdapter 단위 테스트")
class RefreshTokenCacheCommandAdapterTest {

    private static final String TEST_USER_ID = UUID.randomUUID().toString();
    private static final String TEST_REFRESH_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.refreshToken";
    private static final long EXPIRES_IN_SECONDS = 1_209_600L; // 14 days

    @Mock private RedisTemplate<String, String> redisTemplate;

    @Mock private ValueOperations<String, String> valueOperations;

    private RefreshTokenCacheCommandAdapter commandAdapter;

    @BeforeEach
    void setUp() {
        commandAdapter = new RefreshTokenCacheCommandAdapter(redisTemplate);
    }

    @Nested
    @DisplayName("save() 메서드는")
    class SaveMethod {

        @Test
        @DisplayName("userId로 조회할 수 있도록 캐시에 저장한다")
        void shouldSaveRefreshTokenWithUserIdKey() {
            // Given
            UserId userId = UserId.of(TEST_USER_ID);
            String expectedUserKey = "refresh_token::user::" + TEST_USER_ID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            // When
            commandAdapter.save(userId, TEST_REFRESH_TOKEN, EXPIRES_IN_SECONDS);

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
            UserId userId = UserId.of(TEST_USER_ID);
            String expectedTokenKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            // When
            commandAdapter.save(userId, TEST_REFRESH_TOKEN, EXPIRES_IN_SECONDS);

            // Then
            verify(valueOperations)
                    .set(
                            eq(expectedTokenKey),
                            eq(TEST_USER_ID),
                            eq(Duration.ofSeconds(EXPIRES_IN_SECONDS)));
        }
    }

    @Nested
    @DisplayName("deleteByUserId() 메서드는")
    class DeleteByUserIdMethod {

        @Test
        @DisplayName("userId로 캐시를 삭제한다")
        void shouldDeleteByUserIdKey() {
            // Given
            UserId userId = UserId.of(TEST_USER_ID);
            String userKey = "refresh_token::user::" + TEST_USER_ID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(userKey)).thenReturn(TEST_REFRESH_TOKEN);

            // When
            commandAdapter.deleteByUserId(userId);

            // Then
            verify(redisTemplate).delete(userKey);
        }

        @Test
        @DisplayName("연관된 토큰 키도 함께 삭제한다")
        void shouldDeleteAssociatedTokenKey() {
            // Given
            UserId userId = UserId.of(TEST_USER_ID);
            String userKey = "refresh_token::user::" + TEST_USER_ID;
            String tokenKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(userKey)).thenReturn(TEST_REFRESH_TOKEN);

            // When
            commandAdapter.deleteByUserId(userId);

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
            when(valueOperations.get(tokenKey)).thenReturn(TEST_USER_ID);

            // When
            commandAdapter.deleteByToken(TEST_REFRESH_TOKEN);

            // Then
            verify(redisTemplate).delete(tokenKey);
        }

        @Test
        @DisplayName("연관된 사용자 키도 함께 삭제한다")
        void shouldDeleteAssociatedUserKey() {
            // Given
            String tokenKey = "refresh_token::token::" + TEST_REFRESH_TOKEN;
            String userKey = "refresh_token::user::" + TEST_USER_ID;

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(tokenKey)).thenReturn(TEST_USER_ID);

            // When
            commandAdapter.deleteByToken(TEST_REFRESH_TOKEN);

            // Then
            verify(redisTemplate).delete(tokenKey);
            verify(redisTemplate).delete(userKey);
        }
    }
}
