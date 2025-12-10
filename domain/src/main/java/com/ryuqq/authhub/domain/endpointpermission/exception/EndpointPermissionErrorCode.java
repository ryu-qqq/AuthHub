package com.ryuqq.authhub.domain.endpointpermission.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * EndpointPermissionErrorCode - EndpointPermission 도메인 에러 코드
 *
 * <p>EndpointPermission Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum EndpointPermissionErrorCode implements ErrorCode {
    ENDPOINT_PERMISSION_NOT_FOUND("ENDPOINT-PERMISSION-001", 404, "Endpoint permission not found"),
    DUPLICATE_ENDPOINT_PERMISSION(
            "ENDPOINT-PERMISSION-002",
            409,
            "Endpoint permission already exists for this service, path, and method"),
    INVALID_ENDPOINT_PATH("ENDPOINT-PERMISSION-003", 400, "Invalid endpoint path format");

    private final String code;
    private final int httpStatus;
    private final String message;

    EndpointPermissionErrorCode(String code, int httpStatus, String message) {
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
