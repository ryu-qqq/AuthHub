package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * RoleErrorCode - Role 도메인 에러 코드
 *
 * <p>Role/Permission Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RoleErrorCode implements ErrorCode {
    ROLE_NOT_FOUND("ROLE-001", 404, "Role not found"),
    PERMISSION_NOT_FOUND("ROLE-002", 404, "Permission not found"),
    DUPLICATE_ROLE_NAME("ROLE-003", 409, "Role name already exists"),
    DUPLICATE_PERMISSION_CODE("ROLE-004", 409, "Permission code already exists"),
    SYSTEM_ROLE_MODIFICATION_NOT_ALLOWED("ROLE-005", 403, "System role cannot be modified"),
    ROLE_ALREADY_ASSIGNED("ROLE-006", 409, "Role already assigned to user"),
    ROLE_NOT_ASSIGNED("ROLE-007", 404, "Role not assigned to user");

    private final String code;
    private final int httpStatus;
    private final String message;

    RoleErrorCode(String code, int httpStatus, String message) {
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
