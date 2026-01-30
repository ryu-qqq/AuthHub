package com.ryuqq.authhub.domain.rolepermission.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * RolePermissionErrorCode - 역할-권한 관계 도메인 에러 코드
 *
 * <p>역할-권한 관계 관련 예외에서 사용하는 에러 코드를 정의합니다.
 *
 * <p><strong>에러 코드 체계:</strong>
 *
 * <ul>
 *   <li>ROLE_PERMISSION-001: 역할-권한 관계를 찾을 수 없음
 *   <li>ROLE_PERMISSION-002: 중복된 역할-권한 관계
 *   <li>ROLE_PERMISSION-003: 권한이 역할에 사용 중 (삭제 불가)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum RolePermissionErrorCode implements ErrorCode {

    /** 역할-권한 관계를 찾을 수 없음 */
    ROLE_PERMISSION_NOT_FOUND("ROLE_PERMISSION-001", 404, "역할-권한 관계를 찾을 수 없습니다"),

    /** 중복된 역할-권한 관계 */
    DUPLICATE_ROLE_PERMISSION("ROLE_PERMISSION-002", 409, "이미 부여된 권한입니다"),

    /** 권한이 역할에 사용 중 (삭제 불가) */
    PERMISSION_IN_USE("ROLE_PERMISSION-003", 409, "권한이 역할에 부여되어 있어 삭제할 수 없습니다");

    private final String code;
    private final int httpStatus;
    private final String message;

    RolePermissionErrorCode(String code, int httpStatus, String message) {
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
