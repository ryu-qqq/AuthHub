package com.ryuqq.authhub.adapter.out.persistence.role.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * UserRoleJpaEntity 테스트
 *
 * <p>User-Role 매핑 JPA Entity 생성 및 속성 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("UserRoleJpaEntity 테스트")
class UserRoleJpaEntityTest {

    private static final Long ID = 1L;
    private static final UUID USER_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long ROLE_ID = 10L;
    private static final LocalDateTime ASSIGNED_AT = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

    @Nested
    @DisplayName("of() 팩토리 메서드는")
    class OfMethod {

        @Test
        @DisplayName("모든 필드가 설정된 Entity를 생성한다")
        void shouldCreateEntityWithAllFields() {
            // When
            UserRoleJpaEntity entity = UserRoleJpaEntity.of(ID, USER_ID, ROLE_ID, ASSIGNED_AT);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getAssignedAt()).isEqualTo(ASSIGNED_AT);
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("동일한 ID를 가진 Entity는 동등하다")
        void shouldBeEqualForSameId() {
            // Given
            UserRoleJpaEntity entity1 = UserRoleJpaEntity.of(ID, USER_ID, ROLE_ID, ASSIGNED_AT);
            UserRoleJpaEntity entity2 =
                    UserRoleJpaEntity.of(ID, UUID.randomUUID(), 20L, LocalDateTime.now());

            // Then
            assertThat(entity1).isEqualTo(entity2);
        }

        @Test
        @DisplayName("다른 ID를 가진 Entity는 동등하지 않다")
        void shouldNotBeEqualForDifferentId() {
            // Given
            UserRoleJpaEntity entity1 = UserRoleJpaEntity.of(ID, USER_ID, ROLE_ID, ASSIGNED_AT);
            UserRoleJpaEntity entity2 = UserRoleJpaEntity.of(2L, USER_ID, ROLE_ID, ASSIGNED_AT);

            // Then
            assertThat(entity1).isNotEqualTo(entity2);
        }
    }
}
