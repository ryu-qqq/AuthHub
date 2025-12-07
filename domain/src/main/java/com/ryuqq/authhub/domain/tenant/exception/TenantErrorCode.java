package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * TenantErrorCode - Tenant 도메인 에러 코드
 *
 * <p>Tenant Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TenantErrorCode implements ErrorCode {
    TENANT_NOT_FOUND("TENANT-001", 404, "Tenant not found"),
    INVALID_TENANT_STATE("TENANT-002", 400, "Invalid tenant state transition"),
    DUPLICATE_TENANT_NAME("TENANT-003", 409, "Tenant name already exists"),
    TENANT_HAS_ACTIVE_ORGANIZATIONS(
            "TENANT-004", 400, "Cannot deactivate tenant with active organizations");

    private final String code;
    private final int httpStatus;
    private final String message;

    TenantErrorCode(String code, int httpStatus, String message) {
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
