package com.ryuqq.authhub.domain.role.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleErrorCode 테스트")
class RoleErrorCodeTest {

    @Nested
    @DisplayName("ROLE_NOT_FOUND")
    class RoleNotFoundTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(RoleErrorCode.ROLE_NOT_FOUND.getCode()).isEqualTo("ROLE-001");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(RoleErrorCode.ROLE_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(RoleErrorCode.ROLE_NOT_FOUND.getMessage()).isEqualTo("Role not found");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_ROLE_NAME")
    class DuplicateRoleNameTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(RoleErrorCode.DUPLICATE_ROLE_NAME.getCode()).isEqualTo("ROLE-002");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(RoleErrorCode.DUPLICATE_ROLE_NAME.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(RoleErrorCode.DUPLICATE_ROLE_NAME.getMessage())
                    .isEqualTo("Role name already exists");
        }
    }

    @Nested
    @DisplayName("SYSTEM_ROLE_NOT_MODIFIABLE")
    class SystemRoleNotModifiableTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE.getCode()).isEqualTo("ROLE-003");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE.getMessage())
                    .isEqualTo("System role cannot be modified");
        }
    }

    @Nested
    @DisplayName("SYSTEM_ROLE_NOT_DELETABLE")
    class SystemRoleNotDeletableTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE.getCode()).isEqualTo("ROLE-004");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE.getMessage())
                    .isEqualTo("System role cannot be deleted");
        }
    }

    @Nested
    @DisplayName("INVALID_ROLE_SCOPE")
    class InvalidRoleScopeTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(RoleErrorCode.INVALID_ROLE_SCOPE.getCode()).isEqualTo("ROLE-005");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(RoleErrorCode.INVALID_ROLE_SCOPE.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(RoleErrorCode.INVALID_ROLE_SCOPE.getMessage())
                    .isEqualTo("Invalid role scope for this operation");
        }
    }

    @Nested
    @DisplayName("ROLE_PERMISSION_NOT_FOUND")
    class RolePermissionNotFoundTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(RoleErrorCode.ROLE_PERMISSION_NOT_FOUND.getCode()).isEqualTo("ROLE-006");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(RoleErrorCode.ROLE_PERMISSION_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(RoleErrorCode.ROLE_PERMISSION_NOT_FOUND.getMessage())
                    .isEqualTo("Role permission not found");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_ROLE_PERMISSION")
    class DuplicateRolePermissionTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(RoleErrorCode.DUPLICATE_ROLE_PERMISSION.getCode()).isEqualTo("ROLE-007");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(RoleErrorCode.DUPLICATE_ROLE_PERMISSION.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(RoleErrorCode.DUPLICATE_ROLE_PERMISSION.getMessage())
                    .isEqualTo("Role permission already granted");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            assertThat(RoleErrorCode.ROLE_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }

        @Test
        @DisplayName("모든 에러 코드가 존재한다")
        void shouldHaveAllErrorCodes() {
            RoleErrorCode[] values = RoleErrorCode.values();

            assertThat(values).hasSize(7);
            assertThat(values)
                    .containsExactly(
                            RoleErrorCode.ROLE_NOT_FOUND,
                            RoleErrorCode.DUPLICATE_ROLE_NAME,
                            RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE,
                            RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE,
                            RoleErrorCode.INVALID_ROLE_SCOPE,
                            RoleErrorCode.ROLE_PERMISSION_NOT_FOUND,
                            RoleErrorCode.DUPLICATE_ROLE_PERMISSION);
        }

        @Test
        @DisplayName("모든 에러 코드는 ROLE- 접두사를 가진다")
        void shouldHaveRolePrefix() {
            for (RoleErrorCode errorCode : RoleErrorCode.values()) {
                assertThat(errorCode.getCode()).startsWith("ROLE-");
            }
        }
    }
}
