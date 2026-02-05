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
 * DuplicateRoleNameException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("DuplicateRoleNameException 테스트")
class DuplicateRoleNameExceptionTest {

    @Nested
    @DisplayName("DuplicateRoleNameException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String roleName으로 예외를 생성한다")
        void shouldCreateWithStringRoleName() {
            // given
            String roleName = "SUPER_ADMIN";

            // when
            DuplicateRoleNameException exception = new DuplicateRoleNameException(roleName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.DUPLICATE_ROLE_NAME);
            assertThat(exception.code()).isEqualTo("ROLE-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("roleName", roleName);
        }

        @Test
        @DisplayName("RoleName으로 예외를 생성한다")
        void shouldCreateWithRoleName() {
            // given
            RoleName roleName = RoleName.of("SUPER_ADMIN");

            // when
            DuplicateRoleNameException exception = new DuplicateRoleNameException(roleName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.DUPLICATE_ROLE_NAME);
            assertThat(exception.code()).isEqualTo("ROLE-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("roleName", roleName.value());
        }
    }

    @Nested
    @DisplayName("DuplicateRoleNameException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 DUPLICATE_ROLE_NAME이다")
        void errorCodeShouldBeDuplicateRoleName() {
            // given
            DuplicateRoleNameException exception = new DuplicateRoleNameException("SUPER_ADMIN");

            // then
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.DUPLICATE_ROLE_NAME);
            assertThat(exception.code()).isEqualTo("ROLE-002");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            DuplicateRoleNameException exception = new DuplicateRoleNameException("SUPER_ADMIN");

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
