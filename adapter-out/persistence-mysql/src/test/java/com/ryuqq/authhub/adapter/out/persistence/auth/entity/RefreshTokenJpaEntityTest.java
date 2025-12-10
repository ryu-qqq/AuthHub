package com.ryuqq.authhub.adapter.out.persistence.auth.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * RefreshTokenJpaEntity 테스트
 *
 * <p>RefreshToken JPA Entity 생성 및 속성 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("RefreshTokenJpaEntity 테스트")
class RefreshTokenJpaEntityTest {

    private static final Long ID = 1L;
    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refreshToken";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @Nested
    @DisplayName("forNew() 팩토리 메서드는")
    class ForNewMethod {

        @Test
        @DisplayName("신규 Entity를 생성한다")
        void shouldCreateNewEntity() {
            // When
            RefreshTokenJpaEntity entity = RefreshTokenJpaEntity.forNew(USER_ID, TOKEN, CREATED_AT);

            // Then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getToken()).isEqualTo(TOKEN);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(CREATED_AT); // 신규 생성 시 동일
        }
    }

    @Nested
    @DisplayName("of() 팩토리 메서드는")
    class OfMethod {

        @Test
        @DisplayName("모든 필드가 설정된 Entity를 생성한다")
        void shouldCreateEntityWithAllFields() {
            // When
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.of(ID, USER_ID, TOKEN, CREATED_AT, UPDATED_AT);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getToken()).isEqualTo(TOKEN);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }
    }

    @Nested
    @DisplayName("updateToken() 메서드는")
    class UpdateTokenMethod {

        @Test
        @DisplayName("토큰과 갱신 시각을 업데이트한다")
        void shouldUpdateTokenAndUpdatedAt() {
            // Given
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.of(ID, USER_ID, TOKEN, CREATED_AT, CREATED_AT);
            String newToken = "newRefreshTokenValue";
            LocalDateTime newUpdatedAt = LocalDateTime.of(2025, 1, 3, 12, 0, 0);

            // When
            entity.updateToken(newToken, newUpdatedAt);

            // Then
            assertThat(entity.getToken()).isEqualTo(newToken);
            assertThat(entity.getUpdatedAt()).isEqualTo(newUpdatedAt);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT); // 생성 시각은 유지
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("동일한 ID를 가진 Entity는 동등하다")
        void shouldBeEqualForSameId() {
            // Given
            RefreshTokenJpaEntity entity1 =
                    RefreshTokenJpaEntity.of(ID, USER_ID, TOKEN, CREATED_AT, UPDATED_AT);
            RefreshTokenJpaEntity entity2 =
                    RefreshTokenJpaEntity.of(
                            ID, UUID.randomUUID(), "differentToken", CREATED_AT, UPDATED_AT);

            // Then
            assertThat(entity1).isEqualTo(entity2);
        }

        @Test
        @DisplayName("다른 ID를 가진 Entity는 동등하지 않다")
        void shouldNotBeEqualForDifferentId() {
            // Given
            RefreshTokenJpaEntity entity1 =
                    RefreshTokenJpaEntity.of(ID, USER_ID, TOKEN, CREATED_AT, UPDATED_AT);
            RefreshTokenJpaEntity entity2 =
                    RefreshTokenJpaEntity.of(2L, USER_ID, TOKEN, CREATED_AT, UPDATED_AT);

            // Then
            assertThat(entity1).isNotEqualTo(entity2);
        }
    }
}
