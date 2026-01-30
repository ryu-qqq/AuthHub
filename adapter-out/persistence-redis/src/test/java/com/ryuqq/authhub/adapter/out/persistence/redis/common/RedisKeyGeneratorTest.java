package com.ryuqq.authhub.adapter.out.persistence.redis.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RedisKeyGenerator 단위 테스트
 *
 * <p>Redis 키 생성 유틸리티의 키 패턴을 검증합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("cache")
@DisplayName("RedisKeyGenerator 단위 테스트")
class RedisKeyGeneratorTest {

    private static final String TEST_USER_ID = UUID.randomUUID().toString();
    private static final String TEST_TOKEN = "test.refresh.token";

    @Nested
    @DisplayName("refreshTokenByUser() 메서드는")
    class RefreshTokenByUserMethod {

        @Test
        @DisplayName("올바른 키 패턴을 생성한다")
        void shouldGenerateCorrectKeyPattern() {
            // When
            String key = RedisKeyGenerator.refreshTokenByUser(TEST_USER_ID);

            // Then
            assertThat(key).isEqualTo("refresh_token::user::" + TEST_USER_ID);
        }

        @Test
        @DisplayName("키 접두사가 refresh_token::user::로 시작한다")
        void shouldStartWithCorrectPrefix() {
            // When
            String key = RedisKeyGenerator.refreshTokenByUser(TEST_USER_ID);

            // Then
            assertThat(key).startsWith("refresh_token::user::");
        }
    }

    @Nested
    @DisplayName("refreshTokenByToken() 메서드는")
    class RefreshTokenByTokenMethod {

        @Test
        @DisplayName("올바른 키 패턴을 생성한다")
        void shouldGenerateCorrectKeyPattern() {
            // When
            String key = RedisKeyGenerator.refreshTokenByToken(TEST_TOKEN);

            // Then
            assertThat(key).isEqualTo("refresh_token::token::" + TEST_TOKEN);
        }

        @Test
        @DisplayName("키 접두사가 refresh_token::token::로 시작한다")
        void shouldStartWithCorrectPrefix() {
            // When
            String key = RedisKeyGenerator.refreshTokenByToken(TEST_TOKEN);

            // Then
            assertThat(key).startsWith("refresh_token::token::");
        }
    }
}
