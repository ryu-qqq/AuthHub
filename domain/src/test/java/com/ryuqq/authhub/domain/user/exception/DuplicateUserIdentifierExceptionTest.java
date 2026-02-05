package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicateUserIdentifierException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicateUserIdentifierException 테스트")
class DuplicateUserIdentifierExceptionTest {

    @Nested
    @DisplayName("DuplicateUserIdentifierException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String identifier로 예외를 생성한다")
        void shouldCreateWithStringIdentifier() {
            // given
            String identifier = "user@test.com";

            // when
            DuplicateUserIdentifierException exception =
                    new DuplicateUserIdentifierException(identifier);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_USER_IDENTIFIER);
            assertThat(exception.code()).isEqualTo("USER-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("identifier", identifier);
        }

        @Test
        @DisplayName("Identifier로 예외를 생성한다")
        void shouldCreateWithIdentifier() {
            // given
            Identifier identifier = Identifier.of("user@test.com");

            // when
            DuplicateUserIdentifierException exception =
                    new DuplicateUserIdentifierException(identifier);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_USER_IDENTIFIER);
            assertThat(exception.code()).isEqualTo("USER-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("identifier", identifier.value());
        }
    }

    @Nested
    @DisplayName("DuplicateUserIdentifierException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 DUPLICATE_USER_IDENTIFIER이다")
        void errorCodeShouldBeDuplicateUserIdentifier() {
            // given
            DuplicateUserIdentifierException exception =
                    new DuplicateUserIdentifierException("identifier");

            // then
            assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_USER_IDENTIFIER);
            assertThat(exception.code()).isEqualTo("USER-002");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            DuplicateUserIdentifierException exception =
                    new DuplicateUserIdentifierException("identifier");

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
