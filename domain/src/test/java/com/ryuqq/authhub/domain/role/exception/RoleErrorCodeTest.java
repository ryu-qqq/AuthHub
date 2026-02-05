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
    @DisplayName("RoleErrorCode getCode 테스트")
    class GetCodeTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            // when & then
            assertThat(RoleErrorCode.ROLE_NOT_FOUND.getCode()).isEqualTo("ROLE-001");
            assertThat(RoleErrorCode.DUPLICATE_ROLE_NAME.getCode()).isEqualTo("ROLE-002");
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE.getCode()).isEqualTo("ROLE-003");
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE.getCode()).isEqualTo("ROLE-004");
            assertThat(RoleErrorCode.ROLE_IN_USE.getCode()).isEqualTo("ROLE-005");
        }
    }

    @Nested
    @DisplayName("RoleErrorCode getHttpStatus 테스트")
    class GetHttpStatusTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            // when & then
            assertThat(RoleErrorCode.ROLE_NOT_FOUND.getHttpStatus()).isEqualTo(404);
            assertThat(RoleErrorCode.DUPLICATE_ROLE_NAME.getHttpStatus()).isEqualTo(409);
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE.getHttpStatus()).isEqualTo(403);
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE.getHttpStatus()).isEqualTo(403);
            assertThat(RoleErrorCode.ROLE_IN_USE.getHttpStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("RoleErrorCode getMessage 테스트")
    class GetMessageTests {

        @Test
        @DisplayName("각 에러 코드는 올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            // when & then
            assertThat(RoleErrorCode.ROLE_NOT_FOUND.getMessage()).isEqualTo("Role not found");
            assertThat(RoleErrorCode.DUPLICATE_ROLE_NAME.getMessage())
                    .isEqualTo("Role name already exists");
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE.getMessage())
                    .isEqualTo("System role cannot be modified");
            assertThat(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE.getMessage())
                    .isEqualTo("System role cannot be deleted");
            assertThat(RoleErrorCode.ROLE_IN_USE.getMessage())
                    .isEqualTo("Role is currently in use and cannot be deleted");
        }
    }

    @Nested
    @DisplayName("RoleErrorCode ErrorCode 인터페이스 테스트")
    class ErrorCodeInterfaceTests {

        @Test
        @DisplayName("RoleErrorCode는 ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            // when & then
            assertThat(RoleErrorCode.ROLE_NOT_FOUND).isInstanceOf(ErrorCode.class);
        }
    }
}
