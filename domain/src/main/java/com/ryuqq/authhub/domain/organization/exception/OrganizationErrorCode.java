package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * OrganizationErrorCode - Organization 도메인 에러 코드
 *
 * <p>Organization Bounded Context의 모든 에러 코드를 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum OrganizationErrorCode implements ErrorCode {
    ORGANIZATION_NOT_FOUND("ORGANIZATION-001", 404, "Organization not found"),
    INVALID_ORGANIZATION_ID("ORGANIZATION-002", 400, "Invalid organization ID"),
    INVALID_ORGANIZATION_NAME("ORGANIZATION-003", 400, "Invalid organization name"),
    INVALID_ORGANIZATION_STATUS("ORGANIZATION-004", 400, "Invalid organization status"),
    ACTIVE_USERS_EXIST("ORGANIZATION-005", 409, "Cannot deactivate organization with active users");

    private final String code;
    private final int httpStatus;
    private final String message;

    OrganizationErrorCode(String code, int httpStatus, String message) {
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
