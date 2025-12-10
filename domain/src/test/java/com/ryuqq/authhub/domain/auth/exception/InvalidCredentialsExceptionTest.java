package com.ryuqq.authhub.domain.auth.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InvalidCredentialsException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InvalidCredentialsException 테스트")
class InvalidCredentialsExceptionTest {

    @Nested
    @DisplayName("기본 생성자")
    class DefaultConstructorTest {

        @Test
        @DisplayName("기본 생성자로 예외를 생성한다")
        void shouldCreateExceptionWithDefaultConstructor() {
            // when
            InvalidCredentialsException exception = new InvalidCredentialsException();

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("AUTH-001");
            assertThat(exception.getMessage()).isEqualTo("Invalid credentials");
        }

        @Test
        @DisplayName("기본 생성자로 생성 시 args는 비어있다")
        void shouldHaveEmptyArgsWithDefaultConstructor() {
            // when
            InvalidCredentialsException exception = new InvalidCredentialsException();

            // then
            assertThat(exception.args()).isEmpty();
        }
    }

    @Nested
    @DisplayName("tenantId, identifier 생성자")
    class TenantIdIdentifierConstructorTest {

        @Test
        @DisplayName("tenantId와 identifier로 예외를 생성한다")
        void shouldCreateExceptionWithTenantIdAndIdentifier() {
            // given
            UUID tenantId = UUID.randomUUID();
            String identifier = "test@example.com";

            // when
            InvalidCredentialsException exception =
                    new InvalidCredentialsException(tenantId, identifier);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("AUTH-001");
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
            assertThat(exception.args()).containsEntry("identifier", identifier);
        }

        @Test
        @DisplayName("args에 tenantId와 identifier가 포함된다")
        void shouldContainTenantIdAndIdentifierInArgs() {
            // given
            UUID tenantId = UUID.randomUUID();
            String identifier = "user123";

            // when
            InvalidCredentialsException exception =
                    new InvalidCredentialsException(tenantId, identifier);

            // then
            assertThat(exception.args()).hasSize(2);
            assertThat(exception.args()).containsKey("tenantId");
            assertThat(exception.args()).containsKey("identifier");
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            InvalidCredentialsException exception = new InvalidCredentialsException();

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            InvalidCredentialsException exception = new InvalidCredentialsException();

            // then
            assertThat(exception.code()).isEqualTo(AuthErrorCode.INVALID_CREDENTIALS.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(AuthErrorCode.INVALID_CREDENTIALS.getMessage());
        }
    }
}
