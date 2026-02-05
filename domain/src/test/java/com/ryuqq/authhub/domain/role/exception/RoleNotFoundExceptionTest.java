package com.ryuqq.authhub.domain.role.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RoleNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RoleNotFoundException 테스트")
class RoleNotFoundExceptionTest {

    @Nested
    @DisplayName("RoleNotFoundException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("Long roleId로 예외를 생성한다")
        void shouldCreateWithLongRoleId() {
            // given
            Long roleId = 1L;

            // when
            RoleNotFoundException exception = new RoleNotFoundException(roleId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ROLE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("roleId", roleId);
        }

        @Test
        @DisplayName("RoleId로 예외를 생성한다")
        void shouldCreateWithRoleId() {
            // given
            RoleId roleId = RoleId.of(1L);

            // when
            RoleNotFoundException exception = new RoleNotFoundException(roleId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ROLE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("roleId", roleId.value());
        }

        @Test
        @DisplayName("RoleName으로 예외를 생성한다")
        void shouldCreateWithRoleName() {
            // given
            RoleName roleName = RoleName.of("SUPER_ADMIN");

            // when
            RoleNotFoundException exception = new RoleNotFoundException(roleName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ROLE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("roleName", roleName.value());
        }

        @Test
        @DisplayName("String roleName으로 예외를 생성한다")
        void shouldCreateWithStringRoleName() {
            // given
            String roleName = "SUPER_ADMIN";

            // when
            RoleNotFoundException exception = new RoleNotFoundException(roleName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ROLE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("roleName", roleName);
        }
    }

    @Nested
    @DisplayName("RoleNotFoundException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 ROLE_NOT_FOUND이다")
        void errorCodeShouldBeRoleNotFound() {
            // given
            RoleNotFoundException exception = new RoleNotFoundException(1L);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("ROLE-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void httpStatusShouldBe404() {
            // given
            RoleNotFoundException exception = new RoleNotFoundException(1L);

            // then
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
