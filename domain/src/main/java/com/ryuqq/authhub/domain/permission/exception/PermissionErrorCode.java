package com.ryuqq.authhub.domain.permission.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * PermissionErrorCode - Permission 도메인 에러 코드
 *
 * <p>Permission Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum PermissionErrorCode implements ErrorCode {

    PERMISSION_NOT_FOUND("PERMISSION-001", 404, "Permission not found"),
    DUPLICATE_PERMISSION_KEY("PERMISSION-002", 409, "Permission key already exists"),
    SYSTEM_PERMISSION_NOT_MODIFIABLE("PERMISSION-003", 400, "System permission cannot be modified"),
    SYSTEM_PERMISSION_NOT_DELETABLE("PERMISSION-004", 400, "System permission cannot be deleted"),
    INVALID_PERMISSION_KEY("PERMISSION-005", 400, "Invalid permission key format");

    private final String code;
    private final int httpStatus;
    private final String message;

    PermissionErrorCode(String code, int httpStatus, String message) {
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
