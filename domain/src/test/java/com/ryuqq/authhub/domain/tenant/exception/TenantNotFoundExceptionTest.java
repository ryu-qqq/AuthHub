package com.ryuqq.authhub.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantNotFoundException 테스트")
class TenantNotFoundExceptionTest {

    @Nested
    @DisplayName("TenantId 생성자")
    class TenantIdConstructorTest {

        @Test
        @DisplayName("TenantId로 예외를 생성한다")
        void shouldCreateExceptionWithTenantId() {
            // given
            UUID uuid = UUID.randomUUID();
            TenantId tenantId = TenantId.of(uuid);

            // when
            TenantNotFoundException exception = new TenantNotFoundException(tenantId);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("TENANT-001");
            assertThat(exception.args()).containsEntry("tenantId", uuid);
        }
    }

    @Nested
    @DisplayName("UUID 생성자")
    class UuidConstructorTest {

        @Test
        @DisplayName("UUID로 예외를 생성한다")
        void shouldCreateExceptionWithUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            TenantNotFoundException exception = new TenantNotFoundException(uuid);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("TENANT-001");
            assertThat(exception.args()).containsEntry("tenantId", uuid);
        }
    }

    @Nested
    @DisplayName("String 생성자")
    class StringConstructorTest {

        @Test
        @DisplayName("문자열 식별자로 예외를 생성한다")
        void shouldCreateExceptionWithStringIdentifier() {
            // given
            String identifier = "test-identifier";

            // when
            TenantNotFoundException exception = new TenantNotFoundException(identifier);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("TENANT-001");
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
            TenantNotFoundException exception = new TenantNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            TenantNotFoundException exception = new TenantNotFoundException(UUID.randomUUID());

            // then
            assertThat(exception.code()).isEqualTo(TenantErrorCode.TENANT_NOT_FOUND.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(TenantErrorCode.TENANT_NOT_FOUND.getMessage());
        }
    }
}
