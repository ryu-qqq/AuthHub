package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * OrganizationErrorCode - Organization 도메인 에러 코드
 *
 * <p>Organization Aggregate 관련 비즈니스 예외 에러 코드입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum OrganizationErrorCode implements ErrorCode {
    ORGANIZATION_NOT_FOUND("ORG-001", 404, "Organization not found"),
    INVALID_ORGANIZATION_STATE("ORG-002", 400, "Invalid organization state transition"),
    DUPLICATE_ORGANIZATION_NAME("ORG-003", 409, "Organization name already exists in this tenant");

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
