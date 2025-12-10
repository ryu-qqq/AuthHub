package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermission Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermission VO 테스트")
class RolePermissionTest {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @Nested
    @DisplayName("RolePermission 생성")
    class CreateTest {

        @Test
        @DisplayName("RolePermission을 생성한다")
        void shouldCreateRolePermission() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());

            // when
            RolePermission rolePermission = RolePermission.of(roleId, permissionId, FIXED_TIME);

            // then
            assertThat(rolePermission).isNotNull();
            assertThat(rolePermission.getRoleId()).isEqualTo(roleId);
            assertThat(rolePermission.getPermissionId()).isEqualTo(permissionId);
            assertThat(rolePermission.getGrantedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("RoleId가 null이면 예외 발생")
        void shouldThrowExceptionWhenRoleIdNull() {
            // given
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());

            // when & then
            assertThatThrownBy(() -> RolePermission.of(null, permissionId, FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleId");
        }

        @Test
        @DisplayName("PermissionId가 null이면 예외 발생")
        void shouldThrowExceptionWhenPermissionIdNull() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());

            // when & then
            assertThatThrownBy(() -> RolePermission.of(roleId, null, FIXED_TIME))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("PermissionId");
        }

        @Test
        @DisplayName("grantedAt이 null이면 예외 발생")
        void shouldThrowExceptionWhenGrantedAtNull() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());

            // when & then
            assertThatThrownBy(() -> RolePermission.of(roleId, permissionId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("grantedAt");
        }
    }

    @Nested
    @DisplayName("헬퍼 메서드")
    class HelperMethodsTest {

        @Test
        @DisplayName("roleIdValue는 UUID를 반환한다")
        void shouldReturnRoleIdValue() {
            // given
            UUID roleUuid = UUID.randomUUID();
            RoleId roleId = RoleId.of(roleUuid);
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());
            RolePermission rolePermission = RolePermission.of(roleId, permissionId, FIXED_TIME);

            // then
            assertThat(rolePermission.roleIdValue()).isEqualTo(roleUuid);
        }

        @Test
        @DisplayName("permissionIdValue는 UUID를 반환한다")
        void shouldReturnPermissionIdValue() {
            // given
            UUID permissionUuid = UUID.randomUUID();
            RoleId roleId = RoleId.of(UUID.randomUUID());
            PermissionId permissionId = PermissionId.of(permissionUuid);
            RolePermission rolePermission = RolePermission.of(roleId, permissionId, FIXED_TIME);

            // then
            assertThat(rolePermission.permissionIdValue()).isEqualTo(permissionUuid);
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 roleId와 permissionId를 가진 RolePermission은 동일하다")
        void shouldBeEqualWhenSameRoleIdAndPermissionId() {
            // given
            UUID roleUuid = UUID.randomUUID();
            UUID permissionUuid = UUID.randomUUID();
            RolePermission rp1 =
                    RolePermission.of(
                            RoleId.of(roleUuid), PermissionId.of(permissionUuid), FIXED_TIME);
            RolePermission rp2 =
                    RolePermission.of(
                            RoleId.of(roleUuid), PermissionId.of(permissionUuid), Instant.now());

            // then - grantedAt이 달라도 동일
            assertThat(rp1).isEqualTo(rp2);
            assertThat(rp1.hashCode()).isEqualTo(rp2.hashCode());
        }

        @Test
        @DisplayName("다른 roleId를 가진 RolePermission은 다르다")
        void shouldNotBeEqualWhenDifferentRoleId() {
            // given
            UUID permissionUuid = UUID.randomUUID();
            RolePermission rp1 =
                    RolePermission.of(
                            RoleId.of(UUID.randomUUID()),
                            PermissionId.of(permissionUuid),
                            FIXED_TIME);
            RolePermission rp2 =
                    RolePermission.of(
                            RoleId.of(UUID.randomUUID()),
                            PermissionId.of(permissionUuid),
                            FIXED_TIME);

            // then
            assertThat(rp1).isNotEqualTo(rp2);
        }

        @Test
        @DisplayName("다른 permissionId를 가진 RolePermission은 다르다")
        void shouldNotBeEqualWhenDifferentPermissionId() {
            // given
            UUID roleUuid = UUID.randomUUID();
            RolePermission rp1 =
                    RolePermission.of(
                            RoleId.of(roleUuid), PermissionId.of(UUID.randomUUID()), FIXED_TIME);
            RolePermission rp2 =
                    RolePermission.of(
                            RoleId.of(roleUuid), PermissionId.of(UUID.randomUUID()), FIXED_TIME);

            // then
            assertThat(rp1).isNotEqualTo(rp2);
        }
    }

    @Nested
    @DisplayName("reconstitute")
    class ReconstituteTest {

        @Test
        @DisplayName("reconstitute로 RolePermission을 복원한다")
        void shouldReconstituteRolePermission() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());
            PermissionId permissionId = PermissionId.of(UUID.randomUUID());

            // when
            RolePermission rolePermission =
                    RolePermission.reconstitute(roleId, permissionId, FIXED_TIME);

            // then
            assertThat(rolePermission.getRoleId()).isEqualTo(roleId);
            assertThat(rolePermission.getPermissionId()).isEqualTo(permissionId);
            assertThat(rolePermission.getGrantedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
