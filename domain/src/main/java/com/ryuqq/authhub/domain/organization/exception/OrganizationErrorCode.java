package com.ryuqq.authhub.domain.organization.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * OrganizationErrorCode - Organization Bounded Context 에러 코드
 *
 * <p>Organization 도메인에서 발생하는 모든 비즈니스 예외의 에러 코드를 정의합니다.
 *
 * <p><strong>에러 코드 규칙:</strong>
 * <ul>
 *   <li>✅ 형식: ORGANIZATION-{3자리 숫자}</li>
 *   <li>✅ HTTP 상태 코드 매핑</li>
 *   <li>✅ 명확한 에러 메시지</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum OrganizationErrorCode implements ErrorCode {

    /**
     * Organization을 찾을 수 없음
     */
    ORGANIZATION_NOT_FOUND("ORGANIZATION-001", 404, "Organization not found"),

    /**
     * 유효하지 않은 Organization ID
     */
    INVALID_ORGANIZATION_ID("ORGANIZATION-002", 400, "Invalid organization ID"),

    /**
     * 유효하지 않은 Organization 이름
     */
    INVALID_ORGANIZATION_NAME("ORGANIZATION-003", 400, "Invalid organization name"),

    /**
     * 유효하지 않은 Organization 상태
     */
    INVALID_ORGANIZATION_STATUS("ORGANIZATION-004", 400, "Invalid organization status");

    private final String code;
    private final int httpStatus;
    private final String message;

    /**
     * Constructor - ErrorCode 생성
     *
     * @param code 에러 코드 (ORGANIZATION-XXX)
     * @param httpStatus HTTP 상태 코드
     * @param message 에러 메시지
     * @author development-team
     * @since 1.0.0
     */
    OrganizationErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    /**
     * 에러 코드 반환
     *
     * @return 에러 코드 문자열 (예: ORGANIZATION-001)
     * @author development-team
     * @since 1.0.0
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * HTTP 상태 코드 반환
     *
     * @return HTTP 상태 코드 (예: 404, 400)
     * @author development-team
     * @since 1.0.0
     */
    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * 에러 메시지 반환
     *
     * @return 에러 메시지 문자열
     * @author development-team
     * @since 1.0.0
     */
    @Override
    public String getMessage() {
        return message;
    }
}
