package com.ryuqq.authhub.domain.rolepermission.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RolePermission Aggregate 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RolePermission Aggregate 테스트")
class RolePermissionTest {

    private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");

    @Nested
    @DisplayName("RolePermission 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("새로운 RolePermission을 성공적으로 생성한다")
        void shouldCreateRolePermissionSuccessfully() {
            // given
            RoleId roleId = RoleId.of(1L);
            PermissionId permissionId = PermissionId.of(1L);

            // when
            RolePermission rolePermission = RolePermission.create(roleId, permissionId, NOW);

            // then
            assertThat(rolePermission.roleIdValue()).isEqualTo(roleId.value());
            assertThat(rolePermission.permissionIdValue()).isEqualTo(permissionId.value());
            assertThat(rolePermission.isNew()).isTrue();
            assertThat(rolePermission.createdAt()).isEqualTo(NOW);
        }
    }

    @Nested
    @DisplayName("RolePermission Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("isNew는 ID가 없을 때 true를 반환한다")
        void isNewShouldReturnTrueWhenIdIsNull() {
            // given
            RolePermission newRolePermission = RolePermissionFixture.createNew();
            RolePermission existingRolePermission = RolePermissionFixture.create();

            // then
            assertThat(newRolePermission.isNew()).isTrue();
            assertThat(existingRolePermission.isNew()).isFalse();
        }

        @Test
        @DisplayName("roleIdValue는 역할 ID Long 값을 반환한다")
        void roleIdValueShouldReturnRoleIdLong() {
            // given
            RolePermission rolePermission = RolePermissionFixture.create();

            // then
            assertThat(rolePermission.roleIdValue())
                    .isEqualTo(RolePermissionFixture.defaultRoleIdValue());
        }

        @Test
        @DisplayName("permissionIdValue는 권한 ID Long 값을 반환한다")
        void permissionIdValueShouldReturnPermissionIdLong() {
            // given
            RolePermission rolePermission = RolePermissionFixture.create();

            // then
            assertThat(rolePermission.permissionIdValue())
                    .isEqualTo(RolePermissionFixture.defaultPermissionIdValue());
        }
    }

    @Nested
    @DisplayName("RolePermission equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 rolePermissionId를 가진 RolePermission은 동등하다")
        void shouldBeEqualWhenSameRolePermissionId() {
            // given
            RolePermission rolePermission1 = RolePermissionFixture.create();
            RolePermission rolePermission2 = RolePermissionFixture.create();

            // then
            assertThat(rolePermission1).isEqualTo(rolePermission2);
            assertThat(rolePermission1.hashCode()).isEqualTo(rolePermission2.hashCode());
        }

        @Test
        @DisplayName("ID가 없는 경우 roleId와 permissionId로 동등성을 판단한다")
        void shouldUseRoleIdAndPermissionIdWhenIdIsNull() {
            // given
            RolePermission newRolePermission1 =
                    RolePermissionFixture.createNewWithRoleAndPermission(1L, 1L);
            RolePermission newRolePermission2 =
                    RolePermissionFixture.createNewWithRoleAndPermission(1L, 1L);

            // then - 같은 roleId + permissionId이므로 동등함
            assertThat(newRolePermission1).isEqualTo(newRolePermission2);
        }

        @Test
        @DisplayName("roleId가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentRoleId() {
            // given
            RolePermission rolePermission1 =
                    RolePermissionFixture.createNewWithRoleAndPermission(1L, 1L);
            RolePermission rolePermission2 =
                    RolePermissionFixture.createNewWithRoleAndPermission(2L, 1L);

            // then
            assertThat(rolePermission1).isNotEqualTo(rolePermission2);
        }

        @Test
        @DisplayName("permissionId가 다르면 동등하지 않다 (ID가 없는 경우)")
        void shouldNotBeEqualWhenDifferentPermissionId() {
            // given
            RolePermission rolePermission1 =
                    RolePermissionFixture.createNewWithRoleAndPermission(1L, 1L);
            RolePermission rolePermission2 =
                    RolePermissionFixture.createNewWithRoleAndPermission(1L, 2L);

            // then
            assertThat(rolePermission1).isNotEqualTo(rolePermission2);
        }
    }
}
