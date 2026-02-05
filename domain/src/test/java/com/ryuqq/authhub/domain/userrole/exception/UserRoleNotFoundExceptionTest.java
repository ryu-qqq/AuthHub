package com.ryuqq.authhub.domain.userrole.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserRoleNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UserRoleNotFoundException 테스트")
class UserRoleNotFoundExceptionTest {

    @Nested
    @DisplayName("UserRoleNotFoundException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("UserId와 RoleId로 예외를 생성한다")
        void shouldCreateWithUserIdAndRoleId() {
            // given
            UserId userId = UserId.of("01941234-5678-7000-8000-123456789001");
            RoleId roleId = RoleId.of(1L);

            // when
            UserRoleNotFoundException exception = new UserRoleNotFoundException(userId, roleId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserRoleErrorCode.USER_ROLE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("USER_ROLE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("userId", userId.value());
            assertThat(exception.args()).containsEntry("roleId", roleId.value());
        }

        @Test
        @DisplayName("String userId와 Long roleId로 예외를 생성한다")
        void shouldCreateWithStringUserIdAndLongRoleId() {
            // given
            String userId = "01941234-5678-7000-8000-123456789001";
            Long roleId = 1L;

            // when
            UserRoleNotFoundException exception = new UserRoleNotFoundException(userId, roleId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserRoleErrorCode.USER_ROLE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("USER_ROLE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("userId", userId);
            assertThat(exception.args()).containsEntry("roleId", roleId);
        }
    }

    @Nested
    @DisplayName("UserRoleNotFoundException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 USER_ROLE_NOT_FOUND이다")
        void errorCodeShouldBeUserRoleNotFound() {
            // given
            UserRoleNotFoundException exception =
                    new UserRoleNotFoundException("01941234-5678-7000-8000-123456789001", 1L);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(UserRoleErrorCode.USER_ROLE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("USER_ROLE-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void httpStatusShouldBe404() {
            // given
            UserRoleNotFoundException exception =
                    new UserRoleNotFoundException("01941234-5678-7000-8000-123456789001", 1L);

            // then
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
