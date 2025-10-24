package com.ryuqq.authhub.domain.auth.token.exception;

/**
 * Token이 Blacklist에 등록되어 사용 불가능할 때 발생하는 도메인 예외.
 *
 * <p>로그아웃 또는 보안상의 이유로 강제로 무효화된 Token을 사용하려고 할 때 발생합니다.
 * Blacklist는 Redis에 저장되며, Token의 만료 시각까지 유지됩니다.</p>
 *
 * <p><strong>발생 시나리오:</strong></p>
 * <ul>
 *   <li>로그아웃된 Token을 재사용하려고 시도</li>
 *   <li>보안 침해로 인해 강제로 무효화된 Token 사용</li>
 *   <li>Refresh Token Rotation에서 재사용 감지 시 모든 연관 Token을 Blacklist에 등록</li>
 * </ul>
 *
 * <p><strong>Blacklist 관리:</strong></p>
 * <ul>
 *   <li>Redis SET 자료구조 사용 (O(1) 조회 성능)</li>
 *   <li>TTL을 Token 만료 시각까지 설정 (자동 삭제)</li>
 *   <li>Token ID를 Key로 사용</li>
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
public class BlacklistedTokenException extends TokenDomainException {

    /**
     * 메시지만으로 예외를 생성합니다.
     *
     * @param message 예외 메시지 (null 불가)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public BlacklistedTokenException(final String message) {
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
    public BlacklistedTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
