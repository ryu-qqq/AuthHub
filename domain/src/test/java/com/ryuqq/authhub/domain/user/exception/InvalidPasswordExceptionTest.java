package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InvalidPasswordException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InvalidPasswordException 테스트")
class InvalidPasswordExceptionTest {

    @Nested
    @DisplayName("InvalidPasswordException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("무인자로 예외를 생성한다")
        void shouldCreateWithNoArgs() {
            // when
            InvalidPasswordException exception = new InvalidPasswordException();

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD);
            assertThat(exception.code()).isEqualTo("USER-007");
            assertThat(exception.httpStatus()).isEqualTo(401);
            assertThat(exception.args()).isEmpty();
        }

        @Test
        @DisplayName("String identifier로 예외를 생성한다")
        void shouldCreateWithIdentifier() {
            // given
            String identifier = "user@test.com";

            // when
            InvalidPasswordException exception = new InvalidPasswordException(identifier);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD);
            assertThat(exception.code()).isEqualTo("USER-007");
            assertThat(exception.httpStatus()).isEqualTo(401);
            assertThat(exception.args()).containsEntry("identifier", identifier);
        }
    }

    @Nested
    @DisplayName("InvalidPasswordException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 INVALID_PASSWORD이다")
        void errorCodeShouldBeInvalidPassword() {
            // given
            InvalidPasswordException exception = new InvalidPasswordException();

            // then
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_PASSWORD);
            assertThat(exception.code()).isEqualTo("USER-007");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 401이다")
        void httpStatusShouldBe401() {
            // given
            InvalidPasswordException exception = new InvalidPasswordException();

            // then
            assertThat(exception.httpStatus()).isEqualTo(401);
        }
    }
}
