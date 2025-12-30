package com.ryuqq.authhub.sdk.model.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Role Model Tests")
class RoleModelTest {

    @Nested
    @DisplayName("CreateRoleRequest")
    class CreateRoleRequestTest {

        @Test
        @DisplayName("유효한 입력으로 생성 성공")
        void shouldCreateWithValidInput() {
            CreateRoleRequest request = new CreateRoleRequest("tenant-1", "Admin", "관리자 역할");

            assertThat(request.tenantId()).isEqualTo("tenant-1");
            assertThat(request.name()).isEqualTo("Admin");
            assertThat(request.description()).isEqualTo("관리자 역할");
        }

        @Test
        @DisplayName("tenantId가 null이면 예외 발생")
        void shouldThrowWhenTenantIdIsNull() {
            assertThatThrownBy(() -> new CreateRoleRequest(null, "Admin", "관리자 역할"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("tenantId");
        }

        @Test
        @DisplayName("name이 null이면 예외 발생")
        void shouldThrowWhenNameIsNull() {
            assertThatThrownBy(() -> new CreateRoleRequest("tenant-1", null, "관리자 역할"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("name");
        }

        @Test
        @DisplayName("name이 빈 문자열이면 예외 발생")
        void shouldThrowWhenNameIsBlank() {
            assertThatThrownBy(() -> new CreateRoleRequest("tenant-1", "   ", "관리자 역할"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name must not be blank");
        }

        @Test
        @DisplayName("description이 null이어도 생성 가능")
        void shouldCreateWithNullDescription() {
            CreateRoleRequest request = new CreateRoleRequest("tenant-1", "Admin", null);

            assertThat(request.description()).isNull();
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            CreateRoleRequest request1 = new CreateRoleRequest("tenant-1", "Admin", "관리자 역할");
            CreateRoleRequest request2 = new CreateRoleRequest("tenant-1", "Admin", "관리자 역할");

            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }
    }

    @Nested
    @DisplayName("CreateRoleResponse")
    class CreateRoleResponseTest {

        @Test
        @DisplayName("roleId로 생성 성공")
        void shouldCreateWithRoleId() {
            CreateRoleResponse response = new CreateRoleResponse("123");

            assertThat(response.roleId()).isEqualTo("123");
        }
    }

    @Nested
    @DisplayName("RoleResponse")
    class RoleResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            RoleResponse response = new RoleResponse("1", "tenant-1", "Admin", "관리자 역할", now, now);

            assertThat(response.roleId()).isEqualTo("1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.name()).isEqualTo("Admin");
            assertThat(response.description()).isEqualTo("관리자 역할");
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            Instant now = Instant.now();
            RoleResponse response1 = new RoleResponse("1", "tenant-1", "Admin", "관리자 역할", now, now);
            RoleResponse response2 = new RoleResponse("1", "tenant-1", "Admin", "관리자 역할", now, now);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("RoleSummaryResponse")
    class RoleSummaryResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            RoleSummaryResponse response =
                    new RoleSummaryResponse(
                            "1", "tenant-1", "Test Tenant", "Admin", "관리자 역할", 5, 10, now, now);

            assertThat(response.roleId()).isEqualTo("1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.tenantName()).isEqualTo("Test Tenant");
            assertThat(response.name()).isEqualTo("Admin");
            assertThat(response.description()).isEqualTo("관리자 역할");
            assertThat(response.permissionCount()).isEqualTo(5);
            assertThat(response.userCount()).isEqualTo(10);
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("RoleDetailResponse")
    class RoleDetailResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            List<RolePermissionSummaryResponse> permissions =
                    List.of(
                            new RolePermissionSummaryResponse(
                                    "perm-1", "user:read", "사용자 조회 권한", "/users", "READ"));

            RoleDetailResponse response =
                    new RoleDetailResponse(
                            "role-1",
                            "tenant-1",
                            "Test Tenant",
                            "Admin",
                            "관리자 역할",
                            "GLOBAL",
                            "SYSTEM",
                            permissions,
                            10,
                            now,
                            now);

            assertThat(response.roleId()).isEqualTo("role-1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.tenantName()).isEqualTo("Test Tenant");
            assertThat(response.name()).isEqualTo("Admin");
            assertThat(response.description()).isEqualTo("관리자 역할");
            assertThat(response.scope()).isEqualTo("GLOBAL");
            assertThat(response.type()).isEqualTo("SYSTEM");
            assertThat(response.permissions()).hasSize(1);
            assertThat(response.userCount()).isEqualTo(10);
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("빈 권한 목록으로 생성 가능")
        void shouldCreateWithEmptyPermissions() {
            Instant now = Instant.now();
            RoleDetailResponse response =
                    new RoleDetailResponse(
                            "role-1",
                            "tenant-1",
                            "Test Tenant",
                            "Admin",
                            "관리자 역할",
                            "GLOBAL",
                            "SYSTEM",
                            List.of(),
                            0,
                            now,
                            now);

            assertThat(response.permissions()).isEmpty();
            assertThat(response.userCount()).isZero();
        }
    }

    @Nested
    @DisplayName("RolePermissionSummaryResponse")
    class RolePermissionSummaryResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            RolePermissionSummaryResponse response =
                    new RolePermissionSummaryResponse(
                            "perm-1", "user:read", "사용자 조회 권한", "/users", "READ");

            assertThat(response.permissionId()).isEqualTo("perm-1");
            assertThat(response.permissionKey()).isEqualTo("user:read");
            assertThat(response.description()).isEqualTo("사용자 조회 권한");
            assertThat(response.resource()).isEqualTo("/users");
            assertThat(response.action()).isEqualTo("READ");
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            RolePermissionSummaryResponse response1 =
                    new RolePermissionSummaryResponse(
                            "perm-1", "user:read", "사용자 조회 권한", "/users", "READ");
            RolePermissionSummaryResponse response2 =
                    new RolePermissionSummaryResponse(
                            "perm-1", "user:read", "사용자 조회 권한", "/users", "READ");

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("UpdateRoleRequest")
    class UpdateRoleRequestTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            UpdateRoleRequest request = new UpdateRoleRequest("NewAdmin", "새로운 관리자 역할");

            assertThat(request.name()).isEqualTo("NewAdmin");
            assertThat(request.description()).isEqualTo("새로운 관리자 역할");
        }
    }

    @Nested
    @DisplayName("GrantRolePermissionRequest")
    class GrantRolePermissionRequestTest {

        @Test
        @DisplayName("유효한 permissionIds로 생성 성공")
        void shouldCreateWithValidPermissionIds() {
            List<Long> permissionIds = List.of(1L, 2L, 3L);
            GrantRolePermissionRequest request = new GrantRolePermissionRequest(permissionIds);

            assertThat(request.permissionIds()).containsExactly(1L, 2L, 3L);
        }

        @Test
        @DisplayName("permissionIds가 null이면 예외 발생")
        void shouldThrowWhenPermissionIdsIsNull() {
            assertThatThrownBy(() -> new GrantRolePermissionRequest(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("permissionIds");
        }

        @Test
        @DisplayName("permissionIds가 빈 리스트면 예외 발생")
        void shouldThrowWhenPermissionIdsIsEmpty() {
            assertThatThrownBy(() -> new GrantRolePermissionRequest(List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("permissionIds must not be empty");
        }

        @Test
        @DisplayName("방어적 복사로 불변성 유지")
        void shouldMakeDefensiveCopy() {
            List<Long> originalIds = new java.util.ArrayList<>(List.of(1L, 2L));
            GrantRolePermissionRequest request = new GrantRolePermissionRequest(originalIds);
            originalIds.add(3L);

            assertThat(request.permissionIds()).hasSize(2);
            assertThat(request.permissionIds()).containsExactly(1L, 2L);
        }
    }
}
