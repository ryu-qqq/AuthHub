package com.ryuqq.auth.common.exception;

/**
 * SecurityException - 보안 예외 기본 클래스
 *
 * <p>인증/인가 관련 모든 예외의 기본 클래스입니다. 표준 에러 코드를 포함합니다.
 *
 * <p><strong>상속 계층:</strong>
 *
 * <pre>
 * SecurityException (abstract)
 * ├── AuthenticationException (401)
 * └── AuthorizationException (403)
 * </pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public abstract class SecurityException extends RuntimeException {

    private final SecurityErrorCode errorCode;

    /**
     * 에러 코드로 예외 생성
     *
     * @param errorCode 에러 코드
     */
    protected SecurityException(SecurityErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드와 상세 메시지로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param message 상세 메시지
     */
    protected SecurityException(SecurityErrorCode errorCode, String message) {
        super(message != null ? message : errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드와 원인 예외로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    protected SecurityException(SecurityErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드, 상세 메시지, 원인 예외로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param message 상세 메시지
     * @param cause 원인 예외
     */
    protected SecurityException(SecurityErrorCode errorCode, String message, Throwable cause) {
        super(message != null ? message : errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드 반환
     *
     * @return 에러 코드
     */
    public SecurityErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 에러 코드 문자열 반환
     *
     * @return 에러 코드 문자열 (예: AUTH_001)
     */
    public String getCode() {
        return errorCode.getCode();
    }

    /**
     * HTTP 상태 코드 반환
     *
     * @return HTTP 상태 코드
     */
    public int getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    /**
     * 인증 오류인지 확인
     *
     * @return 인증 오류면 true (401)
     */
    public boolean isAuthenticationError() {
        return errorCode.isAuthenticationError();
    }

    /**
     * 인가 오류인지 확인
     *
     * @return 인가 오류면 true (403)
     */
    public boolean isAuthorizationError() {
        return errorCode.isAuthorizationError();
    }
}
