package com.ryuqq.authhub.domain.userrole.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleErrorCode 테스트")
class UserRoleErrorCodeTest {

    @Nested
    @DisplayName("UserRoleErrorCode getCode 테스트")
    class GetCodeTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            // when & then
            assertThat(UserRoleErrorCode.USER_ROLE_NOT_FOUND.getCode()).isEqualTo("USER_ROLE-001");
            assertThat(UserRoleErrorCode.DUPLICATE_USER_ROLE.getCode()).isEqualTo("USER_ROLE-002");
            assertThat(UserRoleErrorCode.ROLE_IN_USE.getCode()).isEqualTo("USER_ROLE-003");
        }
    }

    @Nested
    @DisplayName("UserRoleErrorCode getHttpStatus 테스트")
    class GetHttpStatusTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            // when & then
            assertThat(UserRoleErrorCode.USER_ROLE_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(UserRoleErrorCode.DUPLICATE_USER_ROLE.getHttpStatus()).isEqualTo(409);
            assertThat(UserRoleErrorCode.ROLE_IN_USE.getHttpStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("UserRoleErrorCode getMessage 테스트")
    class GetMessageTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            // when & then
            assertThat(UserRoleErrorCode.USER_ROLE_NOT_FOUND.getMessage())
                    .isEqualTo("User-Role relation not found");
            assertThat(UserRoleErrorCode.DUPLICATE_USER_ROLE.getMessage())
                    .isEqualTo("User-Role relation already exists");
            assertThat(UserRoleErrorCode.ROLE_IN_USE.getMessage())
                    .isEqualTo("Role is currently assigned to users and cannot be deleted");
        }
    }

    @Nested
    @DisplayName("UserRoleErrorCode ErrorCode 인터페이스 테스트")
    class ErrorCodeInterfaceTests {

        @Test
        @DisplayName("UserRoleErrorCode는 ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            // when & then
            assertThat(UserRoleErrorCode.USER_ROLE_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }
}
