package com.ryuqq.authhub.domain.tenant.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TenantErrorCode 테스트")
class TenantErrorCodeTest {

    @Test
    @DisplayName("TENANT_NOT_FOUND 에러 코드 검증")
    void shouldHaveTenantNotFoundErrorCode() {
        // When
        TenantErrorCode errorCode = TenantErrorCode.TENANT_NOT_FOUND;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("TENANT-001");
        assertThat(errorCode.getHttpStatus()).isEqualTo(404);
        assertThat(errorCode.getMessage()).isEqualTo("Tenant not found");
    }

    @Test
    @DisplayName("INVALID_TENANT_ID 에러 코드 검증")
    void shouldHaveInvalidTenantIdErrorCode() {
        // When
        TenantErrorCode errorCode = TenantErrorCode.INVALID_TENANT_ID;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("TENANT-002");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid tenant ID");
    }

    @Test
    @DisplayName("INVALID_TENANT_NAME 에러 코드 검증")
    void shouldHaveInvalidTenantNameErrorCode() {
        // When
        TenantErrorCode errorCode = TenantErrorCode.INVALID_TENANT_NAME;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("TENANT-003");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid tenant name");
    }

    @Test
    @DisplayName("INVALID_TENANT_STATUS 에러 코드 검증")
    void shouldHaveInvalidTenantStatusErrorCode() {
        // When
        TenantErrorCode errorCode = TenantErrorCode.INVALID_TENANT_STATUS;

        // Then
        assertThat(errorCode.getCode()).isEqualTo("TENANT-004");
        assertThat(errorCode.getHttpStatus()).isEqualTo(400);
        assertThat(errorCode.getMessage()).isEqualTo("Invalid tenant status");
    }
}
