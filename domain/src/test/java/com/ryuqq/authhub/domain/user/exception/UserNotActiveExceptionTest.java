package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserNotActiveException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserNotActiveException 테스트")
class UserNotActiveExceptionTest {

    private static final String USER_ID = "01941234-5678-7000-8000-123456789999";

    @Nested
    @DisplayName("UserNotActiveException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String userId와 UserStatus로 예외를 생성한다")
        void shouldCreateWithStringUserIdAndStatus() {
            // given
            UserStatus status = UserStatus.INACTIVE;

            // when
            UserNotActiveException exception = new UserNotActiveException(USER_ID, status);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_ACTIVE);
            assertThat(exception.code()).isEqualTo("USER-004");
            assertThat(exception.httpStatus()).isEqualTo(403);
            assertThat(exception.args()).containsEntry("userId", USER_ID);
            assertThat(exception.args()).containsEntry("status", status.name());
        }

        @Test
        @DisplayName("UserId와 UserStatus로 예외를 생성한다")
        void shouldCreateWithUserIdAndStatus() {
            // given
            UserId userId = UserId.of(USER_ID);
            UserStatus status = UserStatus.SUSPENDED;

            // when
            UserNotActiveException exception = new UserNotActiveException(userId, status);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_SUSPENDED);
            assertThat(exception.code()).isEqualTo("USER-005");
            assertThat(exception.httpStatus()).isEqualTo(403);
            assertThat(exception.args()).containsEntry("userId", USER_ID);
            assertThat(exception.args()).containsEntry("status", status.name());
        }
    }

    @Nested
    @DisplayName("UserNotActiveException resolveErrorCode 테스트")
    class ResolveErrorCodeTests {

        @Test
        @DisplayName("SUSPENDED 상태일 때 USER_SUSPENDED 에러 코드를 사용한다")
        void suspendedStatusShouldUseUserSuspended() {
            // when
            UserNotActiveException exception =
                    new UserNotActiveException(USER_ID, UserStatus.SUSPENDED);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_SUSPENDED);
            assertThat(exception.code()).isEqualTo("USER-005");
        }

        @Test
        @DisplayName("INACTIVE 상태일 때 USER_NOT_ACTIVE 에러 코드를 사용한다")
        void inactiveStatusShouldUseUserNotActive() {
            // when
            UserNotActiveException exception =
                    new UserNotActiveException(USER_ID, UserStatus.INACTIVE);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_ACTIVE);
            assertThat(exception.code()).isEqualTo("USER-004");
        }

        @Test
        @DisplayName("ACTIVE 상태일 때 USER_NOT_ACTIVE 에러 코드를 사용한다 (default)")
        void activeStatusShouldUseUserNotActiveAsDefault() {
            // when
            UserNotActiveException exception =
                    new UserNotActiveException(USER_ID, UserStatus.ACTIVE);

            // then
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_ACTIVE);
            assertThat(exception.code()).isEqualTo("USER-004");
        }
    }
}
