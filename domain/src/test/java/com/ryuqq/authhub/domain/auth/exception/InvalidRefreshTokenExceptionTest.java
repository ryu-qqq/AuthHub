package com.ryuqq.authhub.domain.auth.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InvalidRefreshTokenException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InvalidRefreshTokenException 테스트")
class InvalidRefreshTokenExceptionTest {

    @Nested
    @DisplayName("기본 생성자")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void shouldCreateExceptionWithDefaultConstructor() {
            // when
            InvalidRefreshTokenException exception = new InvalidRefreshTokenException();

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("AUTH-002");
            assertThat(exception.getMessage()).isEqualTo("Invalid refresh token");
        }

        @Test
        @DisplayName("기본 생성자로 생성 시 args는 비어있다")
        void shouldHaveEmptyArgsWithDefaultConstructor() {
            // when
            InvalidRefreshTokenException exception = new InvalidRefreshTokenException();

            // then
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
            InvalidRefreshTokenException exception = new InvalidRefreshTokenException();

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            InvalidRefreshTokenException exception = new InvalidRefreshTokenException();

            // then
            assertThat(exception.code()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN.getMessage());
        }
    }
}
