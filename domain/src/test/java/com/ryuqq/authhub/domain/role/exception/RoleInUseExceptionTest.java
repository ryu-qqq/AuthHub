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
 * RoleInUseException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RoleInUseException 테스트")
class RoleInUseExceptionTest {

    @Nested
    @DisplayName("RoleInUseException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("Long roleId로 예외를 생성한다")
        void shouldCreateWithLongRoleId() {
            // given
            Long roleId = 1L;

            // when
            RoleInUseException exception = new RoleInUseException(roleId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_IN_USE);
            assertThat(exception.code()).isEqualTo("ROLE-005");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("roleId", roleId);
        }

        @Test
        @DisplayName("RoleId로 예외를 생성한다")
        void shouldCreateWithRoleId() {
            // given
            RoleId roleId = RoleId.of(1L);

            // when
            RoleInUseException exception = new RoleInUseException(roleId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_IN_USE);
            assertThat(exception.code()).isEqualTo("ROLE-005");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("roleId", roleId.value());
        }

        @Test
        @DisplayName("RoleName으로 예외를 생성한다")
        void shouldCreateWithRoleName() {
            // given
            RoleName roleName = RoleName.of("SUPER_ADMIN");

            // when
            RoleInUseException exception = new RoleInUseException(roleName);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_IN_USE);
            assertThat(exception.code()).isEqualTo("ROLE-005");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("roleName", roleName.value());
        }
    }

    @Nested
    @DisplayName("RoleInUseException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 ROLE_IN_USE이다")
        void errorCodeShouldBeRoleInUse() {
            // given
            RoleInUseException exception = new RoleInUseException(1L);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(RoleErrorCode.ROLE_IN_USE);
            assertThat(exception.code()).isEqualTo("ROLE-005");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            RoleInUseException exception = new RoleInUseException(1L);

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
