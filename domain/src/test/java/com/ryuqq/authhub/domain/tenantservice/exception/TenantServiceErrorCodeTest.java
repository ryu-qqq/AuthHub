package com.ryuqq.authhub.domain.tenantservice.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceErrorCode 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceErrorCode 테스트")
class TenantServiceErrorCodeTest {

    @Nested
    @DisplayName("TenantServiceErrorCode 인터페이스 구현 테스트")
    class InterfaceImplementationTests {

        @Test
        @DisplayName("TenantServiceErrorCode는 ErrorCode 인터페이스를 구현한다")
        void shouldImplementErrorCodeInterface() {
            // given
            TenantServiceErrorCode errorCode = TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND;

            // then
            assertThat(errorCode).isInstanceOf(ErrorCode.class);
        }
    }

    @Nested
    @DisplayName("TenantServiceErrorCode getCode() 테스트")
    class GetCodeTests {

        @Test
        @DisplayName("모든 ErrorCode는 올바른 코드를 반환한다")
        void shouldReturnCorrectCode() {
            // then
            assertThat(TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND.getCode())
                    .isEqualTo("TENANT_SERVICE-001");
            assertThat(TenantServiceErrorCode.DUPLICATE_TENANT_SERVICE.getCode())
                    .isEqualTo("TENANT_SERVICE-002");
            assertThat(TenantServiceErrorCode.INVALID_TENANT_SERVICE_STATE.getCode())
                    .isEqualTo("TENANT_SERVICE-003");
            assertThat(TenantServiceErrorCode.TENANT_NOT_ACTIVE.getCode())
                    .isEqualTo("TENANT_SERVICE-004");
            assertThat(TenantServiceErrorCode.SERVICE_NOT_ACTIVE.getCode())
                    .isEqualTo("TENANT_SERVICE-005");
        }
    }

    @Nested
    @DisplayName("TenantServiceErrorCode getHttpStatus() 테스트")
    class GetHttpStatusTests {

        @Test
        @DisplayName("모든 ErrorCode는 올바른 HTTP 상태 코드를 반환한다")
        void shouldReturnCorrectHttpStatus() {
            // then
            assertThat(TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND.getHttpStatus())
                    .isEqualTo(404);
            assertThat(TenantServiceErrorCode.DUPLICATE_TENANT_SERVICE.getHttpStatus())
                    .isEqualTo(409);
            assertThat(TenantServiceErrorCode.INVALID_TENANT_SERVICE_STATE.getHttpStatus())
                    .isEqualTo(400);
            assertThat(TenantServiceErrorCode.TENANT_NOT_ACTIVE.getHttpStatus()).isEqualTo(400);
            assertThat(TenantServiceErrorCode.SERVICE_NOT_ACTIVE.getHttpStatus()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("TenantServiceErrorCode getMessage() 테스트")
    class GetMessageTests {

        @Test
        @DisplayName("모든 ErrorCode는 올바른 메시지를 반환한다")
        void shouldReturnCorrectMessage() {
            // then
            assertThat(TenantServiceErrorCode.TENANT_SERVICE_NOT_FOUND.getMessage())
                    .isEqualTo("Tenant service subscription not found");
            assertThat(TenantServiceErrorCode.DUPLICATE_TENANT_SERVICE.getMessage())
                    .isEqualTo("Tenant is already subscribed to this service");
            assertThat(TenantServiceErrorCode.INVALID_TENANT_SERVICE_STATE.getMessage())
                    .isEqualTo("Invalid tenant service state transition");
            assertThat(TenantServiceErrorCode.TENANT_NOT_ACTIVE.getMessage())
                    .isEqualTo("Tenant is not active");
            assertThat(TenantServiceErrorCode.SERVICE_NOT_ACTIVE.getMessage())
                    .isEqualTo("Service is not active");
        }
    }
}
