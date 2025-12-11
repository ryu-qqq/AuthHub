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
    @DisplayName("기본 생성자")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void shouldCreateExceptionWithDefaultConstructor() {
            // when
            InvalidPasswordException exception = new InvalidPasswordException();

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.args()).isEmpty();
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            InvalidPasswordException exception = new InvalidPasswordException();

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            InvalidPasswordException exception = new InvalidPasswordException();

            // then
            assertThat(exception.code()).isEqualTo(UserErrorCode.INVALID_USER_STATE.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(UserErrorCode.INVALID_USER_STATE.getMessage());
        }
    }
}
