package com.ryuqq.authhub.domain.rolepermission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RolePermissionNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RolePermissionNotFoundException 테스트")
class RolePermissionNotFoundExceptionTest {

    @Nested
    @DisplayName("RolePermissionNotFoundException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("RoleId와 PermissionId로 예외를 생성한다")
        void shouldCreateWithRoleIdAndPermissionId() {
            // given
            RoleId roleId = RoleId.of(1L);
            PermissionId permissionId = PermissionId.of(1L);

            // when
            RolePermissionNotFoundException exception =
                    new RolePermissionNotFoundException(roleId, permissionId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("roleId", 1L);
            assertThat(exception.args()).containsEntry("permissionId", 1L);
        }

        @Test
        @DisplayName("Long 파라미터로 예외를 생성한다")
        void shouldCreateWithLongParameters() {
            // given
            Long roleId = 1L;
            Long permissionId = 1L;

            // when
            RolePermissionNotFoundException exception =
                    new RolePermissionNotFoundException(roleId, permissionId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("roleId", roleId);
            assertThat(exception.args()).containsEntry("permissionId", permissionId);
        }
    }

    @Nested
    @DisplayName("RolePermissionNotFoundException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 ROLE_PERMISSION_NOT_FOUND이다")
        void errorCodeShouldBeRolePermissionNotFound() {
            // given
            RolePermissionNotFoundException exception = new RolePermissionNotFoundException(1L, 1L);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.ROLE_PERMISSION_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void httpStatusShouldBe404() {
            // given
            RolePermissionNotFoundException exception = new RolePermissionNotFoundException(1L, 1L);

            // then
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
