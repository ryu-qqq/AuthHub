package com.ryuqq.authhub.sdk.exception;

/**
 * AuthenticationException - 인증 실패 예외
 *
 * <p>사용자 인증에 실패한 경우 발생하는 예외입니다. HTTP 401 Unauthorized에 매핑됩니다.
 *
 * <p><strong>발생 상황:</strong>
 *
 * <ul>
 *   <li>토큰이 없거나 만료된 경우
 *   <li>잘못된 인증 정보
 *   <li>계정 비활성화/잠김
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * if (token == null) {
 *     throw new AuthenticationException(SecurityErrorCode.TOKEN_MISSING);
 * }
 *
 * if (isExpired(token)) {
 *     throw new AuthenticationException(SecurityErrorCode.TOKEN_EXPIRED, "Token expired at: " + expiry);
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public class AuthenticationException extends SecurityException {

    /**
     * 에러 코드로 예외 생성
     *
     * @param errorCode 에러 코드
     */
    public AuthenticationException(SecurityErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 에러 코드와 상세 메시지로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param message 상세 메시지
     */
    public AuthenticationException(SecurityErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 에러 코드와 원인 예외로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    public AuthenticationException(SecurityErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    /**
     * 에러 코드, 상세 메시지, 원인 예외로 예외 생성
     *
     * @param errorCode 에러 코드
     * @param message 상세 메시지
     * @param cause 원인 예외
     */
    public AuthenticationException(SecurityErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * 기본 인증 실패 예외 생성 (UNAUTHENTICATED)
     *
     * @return AuthenticationException
     */
    public static AuthenticationException unauthenticated() {
        return new AuthenticationException(SecurityErrorCode.UNAUTHENTICATED);
    }

    /**
     * 토큰 없음 예외 생성
     *
     * @return AuthenticationException
     */
    public static AuthenticationException tokenMissing() {
        return new AuthenticationException(SecurityErrorCode.TOKEN_MISSING);
    }

    /**
     * 토큰 만료 예외 생성
     *
     * @return AuthenticationException
     */
    public static AuthenticationException tokenExpired() {
        return new AuthenticationException(SecurityErrorCode.TOKEN_EXPIRED);
    }

    /**
     * 잘못된 토큰 예외 생성
     *
     * @return AuthenticationException
     */
    public static AuthenticationException tokenInvalid() {
        return new AuthenticationException(SecurityErrorCode.TOKEN_INVALID);
    }

    /**
     * 잘못된 인증 정보 예외 생성
     *
     * @return AuthenticationException
     */
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException(SecurityErrorCode.INVALID_CREDENTIALS);
    }
}
