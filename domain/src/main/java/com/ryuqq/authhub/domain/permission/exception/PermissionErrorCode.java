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
    PERMISSION_NOT_FOUND("PERM-001", 404, "Permission not found"),
    DUPLICATE_PERMISSION_KEY("PERM-002", 409, "Permission key already exists"),
    SYSTEM_PERMISSION_NOT_MODIFIABLE("PERM-003", 403, "System permission cannot be modified"),
    SYSTEM_PERMISSION_NOT_DELETABLE("PERM-004", 403, "System permission cannot be deleted"),
    PERMISSION_IN_USE("PERM-005", 409, "Permission is currently in use and cannot be deleted");

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
