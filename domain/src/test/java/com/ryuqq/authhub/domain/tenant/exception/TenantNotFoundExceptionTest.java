package com.ryuqq.authhub.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TenantNotFoundException 테스트")
class TenantNotFoundExceptionTest {

    @Test
    @DisplayName("tenantId로 예외 생성 시 올바른 ErrorCode와 메시지를 가진다")
    void shouldCreateExceptionWithTenantId() {
        // Given
        Long tenantId = 123L;

        // When
        TenantNotFoundException exception = new TenantNotFoundException(tenantId);

        // Then
        assertThat(exception.code()).isEqualTo("TENANT-001");
        assertThat(exception.getMessage()).isEqualTo("Tenant not found");
        assertThat(exception.args()).containsEntry("tenantId", tenantId);
    }

    @Test
    @DisplayName("ErrorCode 정보를 올바르게 반환한다")
    void shouldReturnCorrectErrorCodeInfo() {
        // Given
        Long tenantId = 456L;
        TenantNotFoundException exception = new TenantNotFoundException(tenantId);

        // When
        TenantErrorCode errorCode = TenantErrorCode.TENANT_NOT_FOUND;

        // Then
        assertThat(exception.code()).isEqualTo(errorCode.getCode());
        assertThat(errorCode.getHttpStatus()).isEqualTo(404);
    }
}
