package com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.fixture.RolePermissionJpaEntityFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Entity는 데이터 컨테이너 → create()/of() 팩토리, getter 검증
 *   <li>Mock 불필요 (순수 객체 생성/조회)
 *   <li>Soft Delete 미적용 (Hard Delete) 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionJpaEntity 단위 테스트")
class RolePermissionJpaEntityTest {

    private static final Long ROLE_PERMISSION_ID = 1L;
    private static final Long ROLE_ID = 1L;
    private static final Long PERMISSION_ID = 1L;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");

    @Nested
    @DisplayName("create() 팩토리 메서드")
    class CreateFactoryMethod {

        @Test
        @DisplayName("성공: rolePermissionId가 null로 설정됨")
        void shouldSetRolePermissionIdToNull() {
            // when
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.create(ROLE_ID, PERMISSION_ID, CREATED_AT);

            // then
            assertThat(entity.getRolePermissionId()).isNull();
        }

        @Test
        @DisplayName("모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.create(ROLE_ID, PERMISSION_ID, CREATED_AT);

            // then
            assertThat(entity.getRolePermissionId()).isNull();
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getPermissionId()).isEqualTo(PERMISSION_ID);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
        }
    }

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            RolePermissionJpaEntity entity =
                    RolePermissionJpaEntity.of(
                            ROLE_PERMISSION_ID, ROLE_ID, PERMISSION_ID, CREATED_AT);

            // then
            assertThat(entity.getRolePermissionId()).isEqualTo(ROLE_PERMISSION_ID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getPermissionId()).isEqualTo(PERMISSION_ID);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
        }
    }

    @Nested
    @DisplayName("Getter 메서드")
    class GetterMethods {

        @Test
        @DisplayName("모든 getter가 올바르게 동작")
        void shouldReturnCorrectValues() {
            // given
            RolePermissionJpaEntity entity = RolePermissionJpaEntityFixture.create();

            // then
            assertThat(entity.getRolePermissionId())
                    .isEqualTo(RolePermissionJpaEntityFixture.defaultRolePermissionId());
            assertThat(entity.getRoleId())
                    .isEqualTo(RolePermissionJpaEntityFixture.defaultRoleId());
            assertThat(entity.getPermissionId())
                    .isEqualTo(RolePermissionJpaEntityFixture.defaultPermissionId());
            assertThat(entity.getCreatedAt()).isEqualTo(RolePermissionJpaEntityFixture.fixedTime());
        }
    }
}
