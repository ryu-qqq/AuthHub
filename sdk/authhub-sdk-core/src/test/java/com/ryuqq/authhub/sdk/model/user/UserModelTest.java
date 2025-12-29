package com.ryuqq.authhub.sdk.model.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User Model Tests")
class UserModelTest {

    @Nested
    @DisplayName("CreateUserRequest")
    class CreateUserRequestTest {

        @Test
        @DisplayName("유효한 입력으로 생성 성공")
        void shouldCreateWithValidInput() {
            CreateUserRequest request =
                    new CreateUserRequest(
                            "tenant-1", "org-1", "user@test.com", "010-1234-5678", "password123");

            assertThat(request.tenantId()).isEqualTo("tenant-1");
            assertThat(request.organizationId()).isEqualTo("org-1");
            assertThat(request.identifier()).isEqualTo("user@test.com");
            assertThat(request.phoneNumber()).isEqualTo("010-1234-5678");
            assertThat(request.password()).isEqualTo("password123");
        }

        @Test
        @DisplayName("tenantId가 null이면 예외 발생")
        void shouldThrowWhenTenantIdIsNull() {
            assertThatThrownBy(
                            () ->
                                    new CreateUserRequest(
                                            null,
                                            "org-1",
                                            "user@test.com",
                                            "010-1234-5678",
                                            "password123"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("tenantId");
        }

        @Test
        @DisplayName("organizationId가 null이면 예외 발생")
        void shouldThrowWhenOrganizationIdIsNull() {
            assertThatThrownBy(
                            () ->
                                    new CreateUserRequest(
                                            "tenant-1",
                                            null,
                                            "user@test.com",
                                            "010-1234-5678",
                                            "password123"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("organizationId");
        }

        @Test
        @DisplayName("identifier가 null이면 예외 발생")
        void shouldThrowWhenIdentifierIsNull() {
            assertThatThrownBy(
                            () ->
                                    new CreateUserRequest(
                                            "tenant-1",
                                            "org-1",
                                            null,
                                            "010-1234-5678",
                                            "password123"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("identifier");
        }

        @Test
        @DisplayName("identifier가 빈 문자열이면 예외 발생")
        void shouldThrowWhenIdentifierIsBlank() {
            assertThatThrownBy(
                            () ->
                                    new CreateUserRequest(
                                            "tenant-1",
                                            "org-1",
                                            "   ",
                                            "010-1234-5678",
                                            "password123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("identifier must not be blank");
        }

        @Test
        @DisplayName("password가 null이면 예외 발생")
        void shouldThrowWhenPasswordIsNull() {
            assertThatThrownBy(
                            () ->
                                    new CreateUserRequest(
                                            "tenant-1",
                                            "org-1",
                                            "user@test.com",
                                            "010-1234-5678",
                                            null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("password");
        }

        @Test
        @DisplayName("password가 8자 미만이면 예외 발생")
        void shouldThrowWhenPasswordIsTooShort() {
            assertThatThrownBy(
                            () ->
                                    new CreateUserRequest(
                                            "tenant-1",
                                            "org-1",
                                            "user@test.com",
                                            "010-1234-5678",
                                            "short"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("password must be at least 8 characters");
        }

        @Test
        @DisplayName("phoneNumber가 null이어도 생성 가능")
        void shouldCreateWithNullPhoneNumber() {
            CreateUserRequest request =
                    new CreateUserRequest(
                            "tenant-1", "org-1", "user@test.com", null, "password123");

            assertThat(request.phoneNumber()).isNull();
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            CreateUserRequest request1 =
                    new CreateUserRequest(
                            "tenant-1", "org-1", "user@test.com", "010-1234-5678", "password123");
            CreateUserRequest request2 =
                    new CreateUserRequest(
                            "tenant-1", "org-1", "user@test.com", "010-1234-5678", "password123");

            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }
    }

    @Nested
    @DisplayName("UserResponse")
    class UserResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            UserResponse response =
                    new UserResponse(
                            "user-1",
                            "tenant-1",
                            "org-1",
                            "user@test.com",
                            "010-1234-5678",
                            "ACTIVE",
                            now,
                            now);

            assertThat(response.userId()).isEqualTo("user-1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.organizationId()).isEqualTo("org-1");
            assertThat(response.identifier()).isEqualTo("user@test.com");
            assertThat(response.phoneNumber()).isEqualTo("010-1234-5678");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            Instant now = Instant.now();
            UserResponse response1 =
                    new UserResponse(
                            "user-1",
                            "tenant-1",
                            "org-1",
                            "user@test.com",
                            "010-1234-5678",
                            "ACTIVE",
                            now,
                            now);
            UserResponse response2 =
                    new UserResponse(
                            "user-1",
                            "tenant-1",
                            "org-1",
                            "user@test.com",
                            "010-1234-5678",
                            "ACTIVE",
                            now,
                            now);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("toString 포함 필드 확인")
        void shouldIncludeFieldsInToString() {
            Instant now = Instant.now();
            UserResponse response =
                    new UserResponse(
                            "user-1",
                            "tenant-1",
                            "org-1",
                            "user@test.com",
                            "010-1234-5678",
                            "ACTIVE",
                            now,
                            now);

            String str = response.toString();
            assertThat(str).contains("user-1");
            assertThat(str).contains("tenant-1");
            assertThat(str).contains("user@test.com");
        }
    }

    @Nested
    @DisplayName("UserSummaryResponse")
    class UserSummaryResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            UserSummaryResponse response =
                    new UserSummaryResponse(
                            "user-1",
                            "tenant-1",
                            "Test Tenant",
                            "org-1",
                            "Test Org",
                            "user@test.com",
                            "010-1234-5678",
                            "ACTIVE",
                            3,
                            now,
                            now);

            assertThat(response.userId()).isEqualTo("user-1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.tenantName()).isEqualTo("Test Tenant");
            assertThat(response.organizationId()).isEqualTo("org-1");
            assertThat(response.organizationName()).isEqualTo("Test Org");
            assertThat(response.identifier()).isEqualTo("user@test.com");
            assertThat(response.phoneNumber()).isEqualTo("010-1234-5678");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.roleCount()).isEqualTo(3);
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("UserDetailResponse")
    class UserDetailResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            Instant now = Instant.now();
            List<UserRoleSummaryResponse> roles =
                    List.of(
                            new UserRoleSummaryResponse(
                                    "role-1", "Admin", "관리자 역할", "GLOBAL", "SYSTEM"));

            UserDetailResponse response =
                    new UserDetailResponse(
                            "user-1",
                            "tenant-1",
                            "Test Tenant",
                            "org-1",
                            "Test Org",
                            "user@test.com",
                            "010-1234-5678",
                            "ACTIVE",
                            roles,
                            now,
                            now);

            assertThat(response.userId()).isEqualTo("user-1");
            assertThat(response.tenantId()).isEqualTo("tenant-1");
            assertThat(response.tenantName()).isEqualTo("Test Tenant");
            assertThat(response.organizationId()).isEqualTo("org-1");
            assertThat(response.organizationName()).isEqualTo("Test Org");
            assertThat(response.identifier()).isEqualTo("user@test.com");
            assertThat(response.phoneNumber()).isEqualTo("010-1234-5678");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.roles()).hasSize(1);
            assertThat(response.createdAt()).isEqualTo(now);
            assertThat(response.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("빈 역할 목록으로 생성 가능")
        void shouldCreateWithEmptyRoles() {
            Instant now = Instant.now();
            UserDetailResponse response =
                    new UserDetailResponse(
                            "user-1",
                            "tenant-1",
                            "Test Tenant",
                            "org-1",
                            "Test Org",
                            "user@test.com",
                            "010-1234-5678",
                            "ACTIVE",
                            List.of(),
                            now,
                            now);

            assertThat(response.roles()).isEmpty();
        }
    }

    @Nested
    @DisplayName("UserRoleSummaryResponse")
    class UserRoleSummaryResponseTest {

        @Test
        @DisplayName("모든 필드로 생성 성공")
        void shouldCreateWithAllFields() {
            UserRoleSummaryResponse response =
                    new UserRoleSummaryResponse("role-1", "Admin", "관리자 역할", "GLOBAL", "SYSTEM");

            assertThat(response.roleId()).isEqualTo("role-1");
            assertThat(response.name()).isEqualTo("Admin");
            assertThat(response.description()).isEqualTo("관리자 역할");
            assertThat(response.scope()).isEqualTo("GLOBAL");
            assertThat(response.type()).isEqualTo("SYSTEM");
        }
    }

    @Nested
    @DisplayName("CreateUserResponse")
    class CreateUserResponseTest {

        @Test
        @DisplayName("userId로 생성 성공")
        void shouldCreateWithUserId() {
            CreateUserResponse response = new CreateUserResponse(123L);

            assertThat(response.userId()).isEqualTo(123L);
        }
    }

    @Nested
    @DisplayName("UpdateUserRequest")
    class UpdateUserRequestTest {

        @Test
        @DisplayName("identifier로 생성 성공")
        void shouldCreateWithIdentifier() {
            UpdateUserRequest request = new UpdateUserRequest("new@test.com");

            assertThat(request.identifier()).isEqualTo("new@test.com");
        }

        @Test
        @DisplayName("null identifier도 허용")
        void shouldAllowNullIdentifier() {
            UpdateUserRequest request = new UpdateUserRequest(null);

            assertThat(request.identifier()).isNull();
        }
    }

    @Nested
    @DisplayName("UpdateUserStatusRequest")
    class UpdateUserStatusRequestTest {

        @Test
        @DisplayName("status로 생성 성공")
        void shouldCreateWithStatus() {
            UpdateUserStatusRequest request = new UpdateUserStatusRequest("INACTIVE");

            assertThat(request.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("UpdateUserPasswordRequest")
    class UpdateUserPasswordRequestTest {

        @Test
        @DisplayName("유효한 입력으로 생성 성공")
        void shouldCreateWithValidInput() {
            UpdateUserPasswordRequest request =
                    new UpdateUserPasswordRequest("currentPass123", "newPassword123");

            assertThat(request.currentPassword()).isEqualTo("currentPass123");
            assertThat(request.newPassword()).isEqualTo("newPassword123");
        }

        @Test
        @DisplayName("currentPassword가 null이면 예외 발생")
        void shouldThrowWhenCurrentPasswordIsNull() {
            assertThatThrownBy(() -> new UpdateUserPasswordRequest(null, "newPassword123"))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("currentPassword");
        }

        @Test
        @DisplayName("newPassword가 null이면 예외 발생")
        void shouldThrowWhenNewPasswordIsNull() {
            assertThatThrownBy(() -> new UpdateUserPasswordRequest("currentPass123", null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("newPassword");
        }

        @Test
        @DisplayName("newPassword가 8자 미만이면 예외 발생")
        void shouldThrowWhenNewPasswordIsTooShort() {
            assertThatThrownBy(() -> new UpdateUserPasswordRequest("currentPass123", "short"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("newPassword must be at least 8 characters");
        }
    }

    @Nested
    @DisplayName("AssignUserRoleRequest")
    class AssignUserRoleRequestTest {

        @Test
        @DisplayName("roleId로 생성 성공")
        void shouldCreateWithRoleId() {
            AssignUserRoleRequest request = new AssignUserRoleRequest(123L);

            assertThat(request.roleId()).isEqualTo(123L);
        }

        @Test
        @DisplayName("roleId가 null이면 예외 발생")
        void shouldThrowWhenRoleIdIsNull() {
            assertThatThrownBy(() -> new AssignUserRoleRequest(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("roleId");
        }
    }
}
