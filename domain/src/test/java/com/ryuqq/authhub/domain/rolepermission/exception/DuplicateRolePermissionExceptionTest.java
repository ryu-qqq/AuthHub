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
 * DuplicateRolePermissionException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("DuplicateRolePermissionException 테스트")
class DuplicateRolePermissionExceptionTest {

    @Nested
    @DisplayName("DuplicateRolePermissionException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("RoleId와 PermissionId로 예외를 생성한다")
        void shouldCreateWithRoleIdAndPermissionId() {
            // given
            RoleId roleId = RoleId.of(1L);
            PermissionId permissionId = PermissionId.of(1L);

            // when
            DuplicateRolePermissionException exception =
                    new DuplicateRolePermissionException(roleId, permissionId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.DUPLICATE_ROLE_PERMISSION);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
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
            DuplicateRolePermissionException exception =
                    new DuplicateRolePermissionException(roleId, permissionId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.DUPLICATE_ROLE_PERMISSION);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("roleId", roleId);
            assertThat(exception.args()).containsEntry("permissionId", permissionId);
        }
    }

    @Nested
    @DisplayName("DuplicateRolePermissionException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 DUPLICATE_ROLE_PERMISSION이다")
        void errorCodeShouldBeDuplicateRolePermission() {
            // given
            DuplicateRolePermissionException exception =
                    new DuplicateRolePermissionException(1L, 1L);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.DUPLICATE_ROLE_PERMISSION);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-002");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            DuplicateRolePermissionException exception =
                    new DuplicateRolePermissionException(1L, 1L);

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
