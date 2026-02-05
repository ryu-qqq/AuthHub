package com.ryuqq.authhub.domain.tenantservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceNotFoundException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceNotFoundException 테스트")
class TenantServiceNotFoundExceptionTest {

    @Nested
    @DisplayName("TenantServiceNotFoundException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("TenantServiceId로 예외를 생성한다")
        void shouldCreateWithTenantServiceId() {
            // given
            TenantServiceId tenantServiceId = TenantServiceId.of(1L);

            // when
            TenantServiceNotFoundException exception =
                    new TenantServiceNotFoundException(tenantServiceId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("TENANT_SERVICE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("tenantServiceId", 1L);
        }

        @Test
        @DisplayName("tenantId와 serviceId로 예외를 생성한다")
        void shouldCreateWithTenantIdAndServiceId() {
            // given
            String tenantId = "01941234-5678-7000-8000-123456789abc";
            Long serviceId = 1L;

            // when
            TenantServiceNotFoundException exception =
                    new TenantServiceNotFoundException(tenantId, serviceId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("TENANT_SERVICE-001");
            assertThat(exception.httpStatus()).isEqualTo(404);
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
            assertThat(exception.args()).containsEntry("serviceId", serviceId);
        }
    }

    @Nested
    @DisplayName("TenantServiceNotFoundException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 TENANT_SERVICE_NOT_FOUND이다")
        void errorCodeShouldBeTenantServiceNotFound() {
            // given
            TenantServiceNotFoundException exception =
                    new TenantServiceNotFoundException("tenant-1", 1L);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND);
            assertThat(exception.code()).isEqualTo("TENANT_SERVICE-001");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 404이다")
        void httpStatusShouldBe404() {
            // given
            TenantServiceNotFoundException exception =
                    new TenantServiceNotFoundException("tenant-1", 1L);

            // then
            assertThat(exception.httpStatus()).isEqualTo(404);
        }
    }
}
