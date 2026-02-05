package com.ryuqq.authhub.domain.tenantservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DuplicateTenantServiceException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DuplicateTenantServiceException 테스트")
class DuplicateTenantServiceExceptionTest {

    @Nested
    @DisplayName("DuplicateTenantServiceException 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("tenantId와 serviceId로 예외를 생성한다")
        void shouldCreateWithTenantIdAndServiceId() {
            // given
            String tenantId = "01941234-5678-7000-8000-123456789abc";
            Long serviceId = 1L;

            // when
            DuplicateTenantServiceException exception =
                    new DuplicateTenantServiceException(tenantId, serviceId);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception.getErrorCode())
                    .isEqualTo(TenantServiceErrorCode.DUPLICATE_TENANT_SERVICE);
            assertThat(exception.code()).isEqualTo("TENANT_SERVICE-002");
            assertThat(exception.httpStatus()).isEqualTo(409);
            assertThat(exception.args()).containsEntry("tenantId", tenantId);
            assertThat(exception.args()).containsEntry("serviceId", serviceId);
        }
    }

    @Nested
    @DisplayName("DuplicateTenantServiceException 에러 코드 테스트")
    class ErrorCodeTests {

        @Test
        @DisplayName("에러 코드는 DUPLICATE_TENANT_SERVICE이다")
        void errorCodeShouldBeDuplicateTenantService() {
            // given
            DuplicateTenantServiceException exception =
                    new DuplicateTenantServiceException("tenant-1", 1L);

            // then
            assertThat(exception.getErrorCode())
                    .isEqualTo(TenantServiceErrorCode.DUPLICATE_TENANT_SERVICE);
            assertThat(exception.code()).isEqualTo("TENANT_SERVICE-002");
        }

        @Test
        @DisplayName("HTTP 상태 코드는 409이다")
        void httpStatusShouldBe409() {
            // given
            DuplicateTenantServiceException exception =
                    new DuplicateTenantServiceException("tenant-1", 1L);

            // then
            assertThat(exception.httpStatus()).isEqualTo(409);
        }
    }
}
