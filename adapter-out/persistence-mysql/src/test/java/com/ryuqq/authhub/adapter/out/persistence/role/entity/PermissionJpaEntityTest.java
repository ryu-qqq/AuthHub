package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PermissionJpaEntity 테스트
 *
 * <p>Permission JPA Entity 생성 및 속성 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("PermissionJpaEntity 테스트")
class PermissionJpaEntityTest {

    private static final Long ID = 1L;
    private static final String CODE = "user:read";
    private static final String DESCRIPTION = "Read user information";

    @Nested
    @DisplayName("of() 팩토리 메서드는")
    class OfMethod {

        @Test
        @DisplayName("모든 필드가 설정된 Entity를 생성한다")
        void shouldCreateEntityWithAllFields() {
            // When
            PermissionJpaEntity entity = PermissionJpaEntity.of(ID, CODE, DESCRIPTION);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getCode()).isEqualTo(CODE);
            assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
        }

        @Test
        @DisplayName("설명 없이 Entity를 생성한다")
        void shouldCreateEntityWithoutDescription() {
            // When
            PermissionJpaEntity entity = PermissionJpaEntity.of(ID, CODE, null);

            // Then
            assertThat(entity.getDescription()).isNull();
        }

        @Test
        @DisplayName("와일드카드 권한 코드 Entity를 생성한다")
        void shouldCreateEntityWithWildcardPermission() {
            // When
            PermissionJpaEntity entity =
                    PermissionJpaEntity.of(ID, "user:*", "All user permissions");

            // Then
            assertThat(entity.getCode()).isEqualTo("user:*");
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("동일한 ID를 가진 Entity는 동등하다")
        void shouldBeEqualForSameId() {
            // Given
            PermissionJpaEntity entity1 = PermissionJpaEntity.of(ID, CODE, DESCRIPTION);
            PermissionJpaEntity entity2 =
                    PermissionJpaEntity.of(ID, "user:write", "Different desc");

            // Then
            assertThat(entity1).isEqualTo(entity2);
        }

        @Test
        @DisplayName("다른 ID를 가진 Entity는 동등하지 않다")
        void shouldNotBeEqualForDifferentId() {
            // Given
            PermissionJpaEntity entity1 = PermissionJpaEntity.of(ID, CODE, DESCRIPTION);
            PermissionJpaEntity entity2 = PermissionJpaEntity.of(2L, CODE, DESCRIPTION);

            // Then
            assertThat(entity1).isNotEqualTo(entity2);
        }
    }
}
