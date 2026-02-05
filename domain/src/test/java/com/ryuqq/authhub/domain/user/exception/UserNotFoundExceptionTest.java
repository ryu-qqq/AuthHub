package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserNotFoundException 테스트")
class UserNotFoundExceptionTest {

    @Nested
    @DisplayName("UserNotFoundException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String userId로 예외를 생성한다")
        void shouldCreateWithStringUserId() {
            // given
            String userId = "01941234-5678-7000-8000-123456789999";

            // when
            UserNotFoundException exception = new UserNotFoundException(userId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("USER-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("userId", userId);
        }

        @Test
        @DisplayName("UserId로 예외를 생성한다")
        void shouldCreateWithUserId() {
            // given
            UserId userId = UserId.of("01941234-5678-7000-8000-123456789999");

            // when
            UserNotFoundException exception = new UserNotFoundException(userId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("USER-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("userId", userId.value());
        }

        @Test
        @DisplayName("Identifier로 예외를 생성한다")
        void shouldCreateWithIdentifier() {
            // given
            Identifier identifier = Identifier.of("user@test.com");

            // when
            UserNotFoundException exception = new UserNotFoundException(identifier);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("USER-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("identifier", identifier.value());
        }
    }

    @Nested
    @DisplayName("UserNotFoundException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 USER_NOT_FOUND이다")
        void errorCodeShouldBeUserNotFound() {
            // given
            UserNotFoundException exception = new UserNotFoundException("userId");

            // then
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("USER-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void httpStatusShouldBe404() {
            // given
            UserNotFoundException exception = new UserNotFoundException("userId");

            // then
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
