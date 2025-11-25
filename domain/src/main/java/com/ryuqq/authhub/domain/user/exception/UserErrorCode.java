package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.ErrorCode;

/**
 * UserErrorCode - User Bounded Context 에러 코드
 *
 * <p>User 도메인에서 발생하는 모든 비즈니스 예외의 에러 코드를 정의합니다.
 *
 * <p><strong>에러 코드 규칙:</strong>
 *
 * <ul>
 *   <li>✅ 형식: USER-{3자리 숫자}
 *   <li>✅ HTTP 상태 코드 매핑
 *   <li>✅ 명확한 에러 메시지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserErrorCode implements ErrorCode {

    /** User를 찾을 수 없음 */
    USER_NOT_FOUND("USER-001", 404, "User not found"),

    /** 유효하지 않은 User ID */
    INVALID_USER_ID("USER-002", 400, "Invalid user ID"),

    /** 유효하지 않은 User 타입 */
    INVALID_USER_TYPE("USER-003", 400, "Invalid user type"),

    /** 유효하지 않은 User 상태 */
    INVALID_USER_STATUS("USER-004", 400, "Invalid user status");

    private final String code;
    private final int httpStatus;
    private final String message;

    /**
     * Constructor - ErrorCode 생성
     *
     * @param code 에러 코드 (USER-XXX)
     * @param httpStatus HTTP 상태 코드
     * @param message 에러 메시지
     * @author development-team
     * @since 1.0.0
     */
    UserErrorCode(String code, int httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    /**
     * 에러 코드 반환
     *
     * @return 에러 코드 문자열 (예: USER-001)
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
