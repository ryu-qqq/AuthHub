package com.ryuqq.authhub.adapter.out.persistence.token.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RefreshTokenJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Entity는 데이터 컨테이너 → 팩토리 메서드, getter, 상태 변경 메서드 검증
 *   <li>Mock 불필요 (순수 객체 생성/조회)
 *   <li>UUID PK 전략 (UUIDv7)
 *   <li>updateToken() 상태 변경 메서드 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RefreshTokenJpaEntity 단위 테스트")
class RefreshTokenJpaEntityTest {

    private static final UUID REFRESH_TOKEN_ID =
            UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final UUID USER_ID = UUID.fromString("01941234-5678-7000-8000-123456789001");
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.of(
                            REFRESH_TOKEN_ID, USER_ID, TOKEN, CREATED_AT, UPDATED_AT);

            // then
            assertThat(entity.getRefreshTokenId()).isEqualTo(REFRESH_TOKEN_ID);
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getToken()).isEqualTo(TOKEN);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("createdAt과 updatedAt이 다를 수 있음")
        void shouldAllowDifferentCreatedAndUpdatedAt() {
            // when
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.of(
                            REFRESH_TOKEN_ID, USER_ID, TOKEN, CREATED_AT, UPDATED_AT);

            // then
            assertThat(entity.getCreatedAt()).isNotEqualTo(entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("forNew() 팩토리 메서드")
    class ForNewFactoryMethod {

        @Test
        @DisplayName("성공: 신규 Entity는 createdAt과 updatedAt이 동일")
        void shouldSetSameCreatedAndUpdatedAt_ForNewEntity() {
            // when
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(REFRESH_TOKEN_ID, USER_ID, TOKEN, CREATED_AT);

            // then
            assertThat(entity.getRefreshTokenId()).isEqualTo(REFRESH_TOKEN_ID);
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getToken()).isEqualTo(TOKEN);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(CREATED_AT);
        }
    }

    @Nested
    @DisplayName("updateToken 메서드")
    class UpdateToken {

        @Test
        @DisplayName("성공: 토큰과 updatedAt이 갱신됨")
        void shouldUpdateTokenAndUpdatedAt() {
            // given
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(REFRESH_TOKEN_ID, USER_ID, TOKEN, CREATED_AT);
            String newToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.new_token";
            Instant newUpdatedAt = Instant.parse("2025-01-03T00:00:00Z");

            // when
            entity.updateToken(newToken, newUpdatedAt);

            // then
            assertThat(entity.getToken()).isEqualTo(newToken);
            assertThat(entity.getUpdatedAt()).isEqualTo(newUpdatedAt);
        }

        @Test
        @DisplayName("다른 필드는 변경되지 않음")
        void shouldNotChangeOtherFields() {
            // given
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(REFRESH_TOKEN_ID, USER_ID, TOKEN, CREATED_AT);
            String newToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.new_token";
            Instant newUpdatedAt = Instant.parse("2025-01-03T00:00:00Z");

            // when
            entity.updateToken(newToken, newUpdatedAt);

            // then
            assertThat(entity.getRefreshTokenId()).isEqualTo(REFRESH_TOKEN_ID);
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("동일한 ID면 equals true")
        void shouldReturnTrue_WhenSameId() {
            // given
            RefreshTokenJpaEntity entity1 =
                    RefreshTokenJpaEntity.forNew(REFRESH_TOKEN_ID, USER_ID, TOKEN, CREATED_AT);
            RefreshTokenJpaEntity entity2 =
                    RefreshTokenJpaEntity.of(
                            REFRESH_TOKEN_ID,
                            UUID.randomUUID(),
                            "different_token",
                            CREATED_AT,
                            UPDATED_AT);

            // then
            assertThat(entity1).isEqualTo(entity2);
            assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        }

        @Test
        @DisplayName("다른 ID면 equals false")
        void shouldReturnFalse_WhenDifferentId() {
            // given
            RefreshTokenJpaEntity entity1 =
                    RefreshTokenJpaEntity.forNew(REFRESH_TOKEN_ID, USER_ID, TOKEN, CREATED_AT);
            RefreshTokenJpaEntity entity2 =
                    RefreshTokenJpaEntity.forNew(UUID.randomUUID(), USER_ID, TOKEN, CREATED_AT);

            // then
            assertThat(entity1).isNotEqualTo(entity2);
        }

        @Test
        @DisplayName("null과 비교하면 false")
        void shouldReturnFalse_WhenComparedWithNull() {
            // given
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(REFRESH_TOKEN_ID, USER_ID, TOKEN, CREATED_AT);

            // then
            assertThat(entity).isNotEqualTo(null);
        }
    }

    @Nested
    @DisplayName("UUID 필드")
    class UuidFields {

        @Test
        @DisplayName("refreshTokenId가 올바르게 설정됨")
        void shouldSetRefreshTokenId() {
            // given
            UUID customId = UUID.randomUUID();

            // when
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(customId, USER_ID, TOKEN, CREATED_AT);

            // then
            assertThat(entity.getRefreshTokenId()).isEqualTo(customId);
        }

        @Test
        @DisplayName("userId가 올바르게 설정됨")
        void shouldSetUserId() {
            // given
            UUID customUserId = UUID.randomUUID();

            // when
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(REFRESH_TOKEN_ID, customUserId, TOKEN, CREATED_AT);

            // then
            assertThat(entity.getUserId()).isEqualTo(customUserId);
        }
    }
}
