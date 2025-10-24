package com.ryuqq.authhub.domain.auth.token.exception;

/**
 * Token을 찾을 수 없을 때 발생하는 도메인 예외.
 *
 * <p>Redis에서 Refresh Token을 조회했지만 존재하지 않는 경우 발생합니다.
 * Refresh Token Rotation 전략에서 이미 사용된 Token이거나, 만료되어 삭제된 경우입니다.</p>
 *
 * <p><strong>발생 시나리오:</strong></p>
 * <ul>
 *   <li>Refresh Token이 Redis에 존재하지 않음 (이미 사용되었거나 TTL 만료)</li>
 *   <li>Refresh Token Rotation으로 새 Token 발급 후 기존 Token 삭제됨</li>
 *   <li>로그아웃 후 Refresh Token이 삭제됨</li>
 * </ul>
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
public class TokenNotFoundException extends TokenDomainException {

    /**
     * 메시지만으로 예외를 생성합니다.
     *
     * @param message 예외 메시지 (null 불가)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public TokenNotFoundException(final String message) {
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
    public TokenNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
