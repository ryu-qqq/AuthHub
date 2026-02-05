package com.ryuqq.authhub.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantNotFoundException 테스트")
class TenantNotFoundExceptionTest {

    @Nested
    @DisplayName("TenantNotFoundException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("String identifier로 예외를 생성한다")
        void shouldCreateWithStringIdentifier() {
            // given
            String identifier = "01941234-5678-7000-8000-123456789999";

            // when
            TenantNotFoundException exception = new TenantNotFoundException(identifier);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(TenantErrorCode.TENANT_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("TENANT-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("identifier", identifier);
        }

        @Test
        @DisplayName("TenantId로 예외를 생성한다")
        void shouldCreateWithTenantId() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-123456789999");

            // when
            TenantNotFoundException exception = new TenantNotFoundException(tenantId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(TenantErrorCode.TENANT_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("TENANT-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("tenantId", tenantId.value());
        }

        @Test
        @DisplayName("UUID로 예외를 생성한다")
        void shouldCreateWithUuid() {
            // given
            UUID tenantId = UUID.randomUUID();

            // when
            TenantNotFoundException exception = new TenantNotFoundException(tenantId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode()).isEqualTo(TenantErrorCode.TENANT_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("TENANT-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
        }
    }

    @Nested
    @DisplayName("TenantNotFoundException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 TENANT_NOT_FOUND이다")
        void errorCodeShouldBeTenantNotFound() {
            // given
            TenantNotFoundException exception = new TenantNotFoundException("identifier");

            // then
            assertThat(exception.getErrorCode()).isEqualTo(TenantErrorCode.TENANT_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("TENANT-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void httpStatusShouldBe404() {
            // given
            TenantNotFoundException exception = new TenantNotFoundException("identifier");

            // then
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
