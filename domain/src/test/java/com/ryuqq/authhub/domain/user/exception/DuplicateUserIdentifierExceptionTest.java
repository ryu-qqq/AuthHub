package com.ryuqq.authhub.domain.user.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.UUID;
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
    @DisplayName("tenantId + organizationId + identifier 생성자")
    class FullConstructorTest {

        @Test
        @DisplayName("테넌트, 조직, 식별자로 예외를 생성한다")
        void shouldCreateExceptionWithFullParameters() {
            // given
            UUID tenantId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            String identifier = "duplicate-user@example.com";

            // when
            DuplicateUserIdentifierException exception =
                    new DuplicateUserIdentifierException(tenantId, organizationId, identifier);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("USER-003");
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
            assertThat(exception.args()).containsEntry("organizationId", organizationId);
            assertThat(exception.args()).containsEntry("identifier", identifier);
        }
    }

    @Nested
    @DisplayName("identifier 생성자")
    class IdentifierOnlyConstructorTest {

        @Test
        @DisplayName("식별자로만 예외를 생성한다")
        void shouldCreateExceptionWithIdentifierOnly() {
            // given
            String identifier = "duplicate-user@example.com";

            // when
            DuplicateUserIdentifierException exception =
                    new DuplicateUserIdentifierException(identifier);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("USER-003");
            assertThat(exception.args()).containsEntry("identifier", identifier);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            DuplicateUserIdentifierException exception =
                    new DuplicateUserIdentifierException("test");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            DuplicateUserIdentifierException exception =
                    new DuplicateUserIdentifierException("test");

            // then
            assertThat(exception.code())
                    .isEqualTo(UserErrorCode.DUPLICATE_USER_IDENTIFIER.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(UserErrorCode.DUPLICATE_USER_IDENTIFIER.getMessage());
        }
    }
}
