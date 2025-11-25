package com.ryuqq.authhub.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InvalidTenantStateException 테스트")
class InvalidTenantStateExceptionTest {

    @Test
    @DisplayName("tenantId와 reason으로 예외 생성 시 올바른 ErrorCode와 메시지를 가진다")
    void shouldCreateExceptionWithTenantIdAndReason() {
        // Given
        Long tenantId = 123L;
        String reason = "Tenant is already deleted";

        // When
        InvalidTenantStateException exception = new InvalidTenantStateException(tenantId, reason);

        // Then
        assertThat(exception.code()).isEqualTo("TENANT-004");
        assertThat(exception.getMessage()).isEqualTo("Invalid tenant status");
        assertThat(exception.args()).containsEntry("tenantId", tenantId);
        assertThat(exception.args()).containsEntry("reason", reason);
    }

    @Test
    @DisplayName("ErrorCode 정보를 올바르게 반환한다")
    void shouldReturnCorrectErrorCodeInfo() {
        // Given
        Long tenantId = 456L;
        String reason = "Tenant is inactive";
        InvalidTenantStateException exception = new InvalidTenantStateException(tenantId, reason);

        // When
        TenantErrorCode errorCode = TenantErrorCode.INVALID_TENANT_STATUS;

        // Then
        assertThat(exception.code()).isEqualTo(errorCode.getCode());
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
    }
}
