package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * TenantErrorCode - Tenant 도메인 에러 코드
 *
 * <p>Tenant 도메인에서 발생할 수 있는 모든 에러 코드를 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TenantErrorCode implements ErrorCode {

    TENANT_NOT_FOUND("TENANT-001", 404, "Tenant not found"),
    INVALID_TENANT_ID("TENANT-002", 400, "Invalid tenant ID"),
    INVALID_TENANT_NAME("TENANT-003", 400, "Invalid tenant name"),
    INVALID_TENANT_STATUS("TENANT-004", 400, "Invalid tenant status");

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
