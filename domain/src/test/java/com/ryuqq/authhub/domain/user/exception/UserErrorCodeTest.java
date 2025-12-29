package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserErrorCode 테스트")
class UserErrorCodeTest {

    @Nested
    @DisplayName("USER_NOT_FOUND")
    class UserNotFoundTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(UserErrorCode.USER_NOT_FOUND.getCode()).isEqualTo("USER-001");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(UserErrorCode.USER_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(UserErrorCode.USER_NOT_FOUND.getMessage()).isEqualTo("User not found");
        }
    }

    @Nested
    @DisplayName("INVALID_USER_STATE")
    class InvalidUserStateTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(UserErrorCode.INVALID_USER_STATE.getCode()).isEqualTo("USER-002");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(UserErrorCode.INVALID_USER_STATE.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(UserErrorCode.INVALID_USER_STATE.getMessage())
                    .isEqualTo("Invalid user state");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_USER_IDENTIFIER")
    class DuplicateUserIdentifierTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(UserErrorCode.DUPLICATE_USER_IDENTIFIER.getCode()).isEqualTo("USER-003");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(UserErrorCode.DUPLICATE_USER_IDENTIFIER.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(UserErrorCode.DUPLICATE_USER_IDENTIFIER.getMessage())
                    .isEqualTo("User identifier already exists");
        }
    }

    @Nested
    @DisplayName("USER_ROLE_NOT_FOUND")
    class UserRoleNotFoundTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(UserErrorCode.USER_ROLE_NOT_FOUND.getCode()).isEqualTo("USER-004");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(UserErrorCode.USER_ROLE_NOT_FOUND.getHttpStatus()).isEqualTo(404);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(UserErrorCode.USER_ROLE_NOT_FOUND.getMessage())
                    .isEqualTo("User role not found");
        }
    }

    @Nested
    @DisplayName("DUPLICATE_USER_ROLE")
    class DuplicateUserRoleTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(UserErrorCode.DUPLICATE_USER_ROLE.getCode()).isEqualTo("USER-005");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(UserErrorCode.DUPLICATE_USER_ROLE.getHttpStatus()).isEqualTo(409);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(UserErrorCode.DUPLICATE_USER_ROLE.getMessage())
                    .isEqualTo("User role already assigned");
        }
    }

    @Nested
    @DisplayName("INVALID_PASSWORD")
    class InvalidPasswordTest {

        @Test
        @DisplayName("올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            assertThat(UserErrorCode.INVALID_PASSWORD.getCode()).isEqualTo("USER-006");
        }

        @Test
        @DisplayName("올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            assertThat(UserErrorCode.INVALID_PASSWORD.getHttpStatus()).isEqualTo(400);
        }

        @Test
        @DisplayName("올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            assertThat(UserErrorCode.INVALID_PASSWORD.getMessage()).isEqualTo("Invalid password");
        }
    }

    @Nested
    @DisplayName("ErrorCode 인터페이스 구현")
    class ErrorCodeInterfaceTest {

        @Test
        @DisplayName("ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            assertThat(UserErrorCode.USER_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }

        @Test
        @DisplayName("모든 에러 코드가 존재한다")
        void shouldHaveAllErrorCodes() {
            UserErrorCode[] values = UserErrorCode.values();

            assertThat(values).hasSize(7);
            assertThat(values)
                    .containsExactly(
                            UserErrorCode.USER_NOT_FOUND,
                            UserErrorCode.INVALID_USER_STATE,
                            UserErrorCode.DUPLICATE_USER_IDENTIFIER,
                            UserErrorCode.USER_ROLE_NOT_FOUND,
                            UserErrorCode.DUPLICATE_USER_ROLE,
                            UserErrorCode.INVALID_PASSWORD,
                            UserErrorCode.DUPLICATE_USER_PHONE_NUMBER);
        }
    }
}
