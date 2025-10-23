package com.ryuqq.authhub.domain.auth.token.exception;

import com.ryuqq.authhub.domain.auth.token.TokenType;

/**
 * 유효하지 않은 토큰에 대한 예외.
 *
 * <p>다음과 같은 경우 발생합니다:</p>
 * <ul>
 *   <li>JWT 토큰 형식이 올바르지 않은 경우 (서명 검증 실패 등)</li>
 *   <li>토큰의 클레임 값이 예상과 다른 경우</li>
 *   <li>토큰이 블랙리스트에 등록된 경우</li>
 *   <li>토큰의 발급자(issuer)가 올바르지 않은 경우</li>
 *   <li>토큰의 대상(audience)이 올바르지 않은 경우</li>
 *   <li>토큰의 도메인 불변식이 위반된 경우</li>
 * </ul>
 *
 * <p><strong>주의:</strong></p>
 * <p>JWT 생성/검증 로직은 Application Layer의 Port로 분리되어 있으므로,
 * 이 예외는 주로 도메인 규칙 위반 시 사용됩니다.
 * JWT 파싱/검증 실패는 Application Layer에서 처리됩니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class InvalidTokenException extends TokenDomainException {

    private final String tokenType;
    private final String reason;

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public InvalidTokenException(final String message) {
        super(message);
        this.tokenType = null;
        this.reason = null;
    }

    /**
     * 원인 예외를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     * @author AuthHub Team
     * @since 1.0.0
     */
    public InvalidTokenException(final String message, final Throwable cause) {
        super(message, cause);
        this.tokenType = null;
        this.reason = null;
    }

    /**
     * 토큰 타입과 실패 이유를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param tokenType 토큰 타입
     * @param reason 실패 이유
     * @author AuthHub Team
     * @since 1.0.0
     */
    public InvalidTokenException(
            final String message,
            final TokenType tokenType,
            final String reason
    ) {
        super(message);
        this.tokenType = tokenType != null ? tokenType.getCode() : null;
        this.reason = reason;
    }

    /**
     * 토큰 타입을 반환합니다.
     *
     * @return 토큰 타입 코드 (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getTokenType() {
        return this.tokenType;
    }

    /**
     * 실패 이유를 반환합니다.
     *
     * @return 실패 이유 (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * 상세한 예외 정보를 문자열로 반환합니다.
     *
     * @return 예외 정보 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        String baseMessage = super.toString();
        if (tokenType != null && reason != null) {
            return baseMessage + " [type=" + tokenType + ", reason=" + reason + "]";
        }
        return baseMessage;
    }
}
