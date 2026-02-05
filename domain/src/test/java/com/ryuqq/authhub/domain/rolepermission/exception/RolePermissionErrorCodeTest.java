package com.ryuqq.authhub.domain.rolepermission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionErrorCode 테스트")
class RolePermissionErrorCodeTest {

    @Nested
    @DisplayName("RolePermissionErrorCode 인터페이스 구현 테스트")
    class InterfaceImplementationTests {

        @Test
        @DisplayName("RolePermissionErrorCode는 ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            // given
            RolePermissionErrorCode errorCode = RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND;

            // then
            assertThat(errorCode).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("RolePermissionErrorCode getCode() 테스트")
    class GetCodeTests {

        @Test
        @DisplayName("모든 ErrorCode는 올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            // then
            assertThat(RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND.getCode())
                    .isEqualTo("ROLE_PERMISSION-001");
            assertThat(RolePermissionErrorCode.DUPLICATE_ROLE_PERMISSION.getCode())
                    .isEqualTo("ROLE_PERMISSION-002");
            assertThat(RolePermissionErrorCode.PERMISSION_IN_USE.getCode())
                    .isEqualTo("ROLE_PERMISSION-003");
        }
    }

    @Nested
    @DisplayName("RolePermissionErrorCode getHttpStatus() 테스트")
    class GetHttpStatusTests {

        @Test
        @DisplayName("모든 ErrorCode는 올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            // then
            assertThat(RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(RolePermissionErrorCode.DUPLICATE_ROLE_PERMISSION.getHttpStatus())
                    .isEqualTo(409);
            assertThat(RolePermissionErrorCode.PERMISSION_IN_USE.getHttpStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("RolePermissionErrorCode getMessage() 테스트")
    class GetMessageTests {

        @Test
        @DisplayName("모든 ErrorCode는 올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            // then
            assertThat(RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND.getMessage())
                    .isEqualTo("역할-권한 관계를 찾을 수 없습니다");
            assertThat(RolePermissionErrorCode.DUPLICATE_ROLE_PERMISSION.getMessage())
                    .isEqualTo("이미 부여된 권한입니다");
            assertThat(RolePermissionErrorCode.PERMISSION_IN_USE.getMessage())
                    .isEqualTo("권한이 역할에 부여되어 있어 삭제할 수 없습니다");
        }
    }
}
