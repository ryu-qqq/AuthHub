package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * RoleErrorCode - Role 도메인 에러 코드
 *
 * <p>Role Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RoleErrorCode implements ErrorCode {

    ROLE_NOT_FOUND("ROLE-001", 404, "Role not found"),
    DUPLICATE_ROLE_NAME("ROLE-002", 409, "Role name already exists"),
    SYSTEM_ROLE_NOT_MODIFIABLE("ROLE-003", 400, "System role cannot be modified"),
    SYSTEM_ROLE_NOT_DELETABLE("ROLE-004", 400, "System role cannot be deleted"),
    INVALID_ROLE_SCOPE("ROLE-005", 400, "Invalid role scope for this operation");

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
