package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * RoleJpaEntity 테스트
 *
 * <p>Role JPA Entity 생성 및 속성 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("RoleJpaEntity 테스트")
class RoleJpaEntityTest {

    private static final Long ID = 1L;
    private static final Long TENANT_ID = 100L;
    private static final String NAME = "ROLE_ADMIN";
    private static final String DESCRIPTION = "Administrator role";
    private static final boolean IS_SYSTEM = true;

    @Nested
    @DisplayName("of() 팩토리 메서드는")
    class OfMethod {

        @Test
        @DisplayName("모든 필드가 설정된 Entity를 생성한다")
        void shouldCreateEntityWithAllFields() {
            // When
            RoleJpaEntity entity = RoleJpaEntity.of(ID, TENANT_ID, NAME, DESCRIPTION, IS_SYSTEM);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
            assertThat(entity.isSystem()).isEqualTo(IS_SYSTEM);
        }

        @Test
        @DisplayName("Tenant 없이 Entity를 생성한다 (시스템 전역 역할)")
        void shouldCreateEntityWithoutTenant() {
            // When
            RoleJpaEntity entity = RoleJpaEntity.of(ID, null, NAME, DESCRIPTION, IS_SYSTEM);

            // Then
            assertThat(entity.getTenantId()).isNull();
        }

        @Test
        @DisplayName("비시스템 역할 Entity를 생성한다")
        void shouldCreateNonSystemRoleEntity() {
            // When
            RoleJpaEntity entity =
                    RoleJpaEntity.of(ID, TENANT_ID, "ROLE_CUSTOM", "Custom role", false);

            // Then
            assertThat(entity.isSystem()).isFalse();
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("동일한 ID를 가진 Entity는 동등하다")
        void shouldBeEqualForSameId() {
            // Given
            RoleJpaEntity entity1 = RoleJpaEntity.of(ID, TENANT_ID, NAME, DESCRIPTION, IS_SYSTEM);
            RoleJpaEntity entity2 =
                    RoleJpaEntity.of(ID, 200L, "ROLE_USER", "Different desc", false);

            // Then
            assertThat(entity1).isEqualTo(entity2);
        }

        @Test
        @DisplayName("다른 ID를 가진 Entity는 동등하지 않다")
        void shouldNotBeEqualForDifferentId() {
            // Given
            RoleJpaEntity entity1 = RoleJpaEntity.of(ID, TENANT_ID, NAME, DESCRIPTION, IS_SYSTEM);
            RoleJpaEntity entity2 = RoleJpaEntity.of(2L, TENANT_ID, NAME, DESCRIPTION, IS_SYSTEM);

            // Then
            assertThat(entity1).isNotEqualTo(entity2);
        }
    }
}
