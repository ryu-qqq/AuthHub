package com.ryuqq.authhub.domain.permissionendpoint.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * PermissionEndpointErrorCode - PermissionEndpoint 도메인 에러 코드
 *
 * <p>PermissionEndpoint Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum PermissionEndpointErrorCode implements ErrorCode {
    PERMISSION_ENDPOINT_NOT_FOUND("PERM-EP-001", 404, "Permission endpoint not found"),
    DUPLICATE_PERMISSION_ENDPOINT(
            "PERM-EP-002",
            409,
            "Permission endpoint with same URL pattern and HTTP method already exists"),
    INVALID_URL_PATTERN("PERM-EP-003", 400, "Invalid URL pattern format"),
    PERMISSION_NOT_FOUND_FOR_ENDPOINT("PERM-EP-004", 404, "Permission not found for the endpoint");

    private final String code;
    private final int httpStatus;
    private final String message;

    PermissionEndpointErrorCode(String code, int httpStatus, String message) {
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
