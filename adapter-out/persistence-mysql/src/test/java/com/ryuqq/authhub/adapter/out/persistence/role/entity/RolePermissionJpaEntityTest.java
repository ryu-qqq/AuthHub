package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionJpaEntity 테스트
 *
 * <p>Role-Permission 매핑 JPA Entity 생성 및 속성 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("RolePermissionJpaEntity 테스트")
class RolePermissionJpaEntityTest {

    private static final Long ID = 1L;
    private static final Long ROLE_ID = 10L;
    private static final Long PERMISSION_ID = 100L;

    @Nested
    @DisplayName("of() 팩토리 메서드는")
    class OfMethod {

        @Test
        @DisplayName("모든 필드가 설정된 Entity를 생성한다")
        void shouldCreateEntityWithAllFields() {
            // When
            RolePermissionJpaEntity entity = RolePermissionJpaEntity.of(ID, ROLE_ID, PERMISSION_ID);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getPermissionId()).isEqualTo(PERMISSION_ID);
        }
    }

    @Nested
    @DisplayName("forNew() 팩토리 메서드는")
    class ForNewMethod {

        @Test
        @DisplayName("ID 없이 신규 Entity를 생성한다")
        void shouldCreateEntityWithoutId() {
            // When
            RolePermissionJpaEntity entity = RolePermissionJpaEntity.forNew(ROLE_ID, PERMISSION_ID);

            // Then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getPermissionId()).isEqualTo(PERMISSION_ID);
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("동일한 ID를 가진 Entity는 동등하다")
        void shouldBeEqualForSameId() {
            // Given
            RolePermissionJpaEntity entity1 =
                    RolePermissionJpaEntity.of(ID, ROLE_ID, PERMISSION_ID);
            RolePermissionJpaEntity entity2 = RolePermissionJpaEntity.of(ID, 20L, 200L);

            // Then
            assertThat(entity1).isEqualTo(entity2);
        }

        @Test
        @DisplayName("다른 ID를 가진 Entity는 동등하지 않다")
        void shouldNotBeEqualForDifferentId() {
            // Given
            RolePermissionJpaEntity entity1 =
                    RolePermissionJpaEntity.of(ID, ROLE_ID, PERMISSION_ID);
            RolePermissionJpaEntity entity2 =
                    RolePermissionJpaEntity.of(2L, ROLE_ID, PERMISSION_ID);

            // Then
            assertThat(entity1).isNotEqualTo(entity2);
        }
    }
}
