package com.ryuqq.authhub.domain.auth.credential.exception;

/**
 * UserCredential 도메인의 최상위 예외 클래스.
 *
 * <p>모든 UserCredential 관련 도메인 예외는 이 클래스를 상속합니다.
 * 도메인 규칙 위반이나 비즈니스 로직 오류를 나타냅니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Framework 의존성 금지 - Pure Java Exception만 사용</li>
 *   <li>✅ Checked Exception이 아닌 RuntimeException 계열 사용</li>
 *   <li>✅ 명확한 예외 메시지 제공</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class CredentialDomainException extends RuntimeException {

    /**
     * 메시지만으로 예외를 생성합니다.
     *
     * @param message 예외 메시지 (null 불가)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialDomainException(final String message) {
        super(message);
    }

    /**
     * 메시지와 원인 예외를 포함하여 예외를 생성합니다.
     *
     * @param message 예외 메시지 (null 불가)
     * @param cause 원인 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialDomainException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
