package com.ryuqq.authhub.domain.rolepermission.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionInUseException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("PermissionInUseException 테스트")
class PermissionInUseExceptionTest {

    @Nested
    @DisplayName("PermissionInUseException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("PermissionId로 예외를 생성한다")
        void shouldCreateWithPermissionId() {
            // given
            PermissionId permissionId = PermissionId.of(1L);

            // when
            PermissionInUseException exception = new PermissionInUseException(permissionId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.PERMISSION_IN_USE);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("permissionId", 1L);
        }

        @Test
        @DisplayName("Long 파라미터로 예외를 생성한다")
        void shouldCreateWithLongParameter() {
            // given
            Long permissionId = 1L;

            // when
            PermissionInUseException exception = new PermissionInUseException(permissionId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.PERMISSION_IN_USE);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-003");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("permissionId", permissionId);
        }
    }

    @Nested
    @DisplayName("PermissionInUseException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 PERMISSION_IN_USE이다")
        void errorCodeShouldBePermissionInUse() {
            // given
            PermissionInUseException exception = new PermissionInUseException(1L);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(RolePermissionErrorCode.PERMISSION_IN_USE);
            assertThat(exception.code()).isEqualTo("ROLE_PERMISSION-003");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            PermissionInUseException exception = new PermissionInUseException(1L);

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
