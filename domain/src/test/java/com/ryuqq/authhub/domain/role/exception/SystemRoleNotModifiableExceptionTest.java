package com.ryuqq.authhub.domain.role.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SystemRoleNotModifiableException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("SystemRoleNotModifiableException 테스트")
class SystemRoleNotModifiableExceptionTest {

    @Nested
    @DisplayName("SystemRoleNotModifiableException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String roleName으로 예외를 생성한다")
        void shouldCreateWithStringRoleName() {
            // given
            String roleName = "SUPER_ADMIN";

            // when
            SystemRoleNotModifiableException exception =
                    new SystemRoleNotModifiableException(roleName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE);
            assertThat(exception.code()).isEqualTo("ROLE-003");
            assertThat(exception.httpStatus()).isEqualTo(403);
            assertThat(exception.args()).containsEntry("roleName", roleName);
        }

        @Test
        @DisplayName("RoleName으로 예외를 생성한다")
        void shouldCreateWithRoleName() {
            // given
            RoleName roleName = RoleName.of("SUPER_ADMIN");

            // when
            SystemRoleNotModifiableException exception =
                    new SystemRoleNotModifiableException(roleName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE);
            assertThat(exception.code()).isEqualTo("ROLE-003");
            assertThat(exception.httpStatus()).isEqualTo(403);
            assertThat(exception.args()).containsEntry("roleName", roleName.value());
        }
    }

    @Nested
    @DisplayName("SystemRoleNotModifiableException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 SYSTEM_ROLE_NOT_MODIFIABLE이다")
        void errorCodeShouldBeSystemRoleNotModifiable() {
            // given
            SystemRoleNotModifiableException exception =
                    new SystemRoleNotModifiableException("SUPER_ADMIN");

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE);
            assertThat(exception.code()).isEqualTo("ROLE-003");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 403이다")
        void httpStatusShouldBe403() {
            // given
            SystemRoleNotModifiableException exception =
                    new SystemRoleNotModifiableException("SUPER_ADMIN");

            // then
            assertThat(exception.httpStatus()).isEqualTo(403);
        }
    }
}
