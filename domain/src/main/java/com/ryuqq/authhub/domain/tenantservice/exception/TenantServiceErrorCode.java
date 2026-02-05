package com.ryuqq.authhub.domain.tenantservice.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * TenantServiceErrorCode - TenantService 도메인 에러 코드
 *
 * <p>TenantService Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TenantServiceErrorCode implements ErrorCode {
    TENANT_SERVICE_NOT_FOUND("TENANT_SERVICE-001", 404, "Tenant service subscription not found"),
    DUPLICATE_TENANT_SERVICE(
            "TENANT_SERVICE-002", 409, "Tenant is already subscribed to this service"),
    INVALID_TENANT_SERVICE_STATE(
            "TENANT_SERVICE-003", 400, "Invalid tenant service state transition"),
    TENANT_NOT_ACTIVE("TENANT_SERVICE-004", 400, "Tenant is not active"),
    SERVICE_NOT_ACTIVE("TENANT_SERVICE-005", 400, "Service is not active");

    private final String code;
    private final int httpStatus;
    private final String message;

    TenantServiceErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
