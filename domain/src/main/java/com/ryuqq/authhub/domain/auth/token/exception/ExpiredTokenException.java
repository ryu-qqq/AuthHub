package com.ryuqq.authhub.domain.auth.token.exception;

import com.ryuqq.authhub.domain.auth.token.TokenId;
import com.ryuqq.authhub.domain.auth.token.TokenType;

import java.time.Instant;

/**
 * 만료된 토큰에 대한 예외.
 *
 * <p>토큰의 만료 시각(expiresAt)이 현재 시각보다 이전인 경우 발생합니다.
 * 이 예외는 비즈니스 규칙 위반을 나타내며, 클라이언트는 토큰을 재발급받아야 합니다.</p>
 *
 * <p><strong>발생 상황:</strong></p>
 * <ul>
 *   <li>만료된 액세스 토큰으로 API 요청 시</li>
 *   <li>만료된 리프레시 토큰으로 토큰 갱신 요청 시</li>
 *   <li>토큰 검증 과정에서 만료 확인 시</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class ExpiredTokenException extends TokenDomainException {

    private final String tokenId;
    private final String tokenType;
    private final Instant expiresAt;

    /**
     * 기본 생성자.
     *
     * @param message 예외 메시지
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ExpiredTokenException(final String message) {
        super(message);
        this.tokenId = null;
        this.tokenType = null;
        this.expiresAt = null;
    }

    /**
     * 토큰 정보를 포함한 생성자.
     *
     * @param message 예외 메시지
     * @param tokenId 토큰 식별자
     * @param tokenType 토큰 타입
     * @param expiresAt 만료 시각
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ExpiredTokenException(
            final String message,
            final TokenId tokenId,
            final TokenType tokenType,
            final Instant expiresAt
    ) {
        super(message);
        this.tokenId = tokenId != null ? tokenId.asString() : null;
        this.tokenType = tokenType != null ? tokenType.getCode() : null;
        this.expiresAt = expiresAt;
    }

    /**
     * 토큰 식별자를 반환합니다.
     *
     * @return 토큰 식별자 문자열 (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getTokenId() {
        return this.tokenId;
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
     * 만료 시각을 반환합니다.
     *
     * @return 만료 시각 Instant (null 가능)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getExpiresAt() {
        return this.expiresAt;
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
        if (tokenId != null && tokenType != null && expiresAt != null) {
            return baseMessage + " [tokenId=" + tokenId + ", type=" + tokenType + ", expiresAt=" + expiresAt + "]";
        }
        return baseMessage;
    }
}
