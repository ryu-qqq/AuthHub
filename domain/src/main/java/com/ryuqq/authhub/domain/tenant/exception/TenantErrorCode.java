package com.ryuqq.authhub.domain.tenant.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * TenantErrorCode - Tenant Bounded Context 에러 코드
 *
 * <p>Tenant 도메인에서 발생하는 모든 비즈니스 예외의 에러 코드를 정의합니다.
 *
 * <p><strong>에러 코드 규칙:</strong>
 * <ul>
 *   <li>✅ 형식: TENANT-{3자리 숫자}</li>
 *   <li>✅ HTTP 상태 코드 매핑</li>
 *   <li>✅ 명확한 에러 메시지</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TenantErrorCode implements ErrorCode {

    /**
     * Tenant를 찾을 수 없음
     */
    TENANT_NOT_FOUND("TENANT-001", 404, "Tenant not found"),

    /**
     * 유효하지 않은 Tenant ID
     */
    INVALID_TENANT_ID("TENANT-002", 400, "Invalid tenant ID"),

    /**
     * 유효하지 않은 Tenant 이름
     */
    INVALID_TENANT_NAME("TENANT-003", 400, "Invalid tenant name"),

    /**
     * 유효하지 않은 Tenant 상태
     */
    INVALID_TENANT_STATUS("TENANT-004", 400, "Invalid tenant status");

    private final String code;
    private final int httpStatus;
    private final String message;

    /**
     * Constructor - ErrorCode 생성
     *
     * @param code 에러 코드 (TENANT-XXX)
     * @param httpStatus HTTP 상태 코드
     * @param message 에러 메시지
     * @author development-team
     * @since 1.0.0
     */
    TenantErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    /**
     * 에러 코드 반환
     *
     * @return 에러 코드 문자열 (예: TENANT-001)
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
