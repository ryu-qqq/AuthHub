package com.ryuqq.authhub.domain.token.vo;

import com.ryuqq.authhub.domain.user.id.UserId;

/**
 * RefreshToken - Refresh Token Value Object
 *
 * <p>Refresh Token 정보를 캡슐화하는 Value Object입니다.
 *
 * <p><strong>포함 정보:</strong>
 *
 * <ul>
 *   <li>userId: 사용자 ID
 *   <li>token: 토큰 문자열
 *   <li>expiresInSeconds: 만료 시간 (초)
 * </ul>
 *
 * @param userId 사용자 ID (Value Object)
 * @param token 토큰 문자열
 * @param expiresInSeconds 만료 시간 (초)
 * @author development-team
 * @since 1.0.0
 */
public record RefreshToken(UserId userId, String token, long expiresInSeconds) {

    /** Compact Constructor - 유효성 검증 */
    public RefreshToken {
        if (userId == null) {
            throw new IllegalArgumentException("UserId는 null일 수 없습니다");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("RefreshToken은 null이거나 빈 값일 수 없습니다");
        }
        if (expiresInSeconds <= 0) {
            throw new IllegalArgumentException("만료 시간은 0보다 커야 합니다: " + expiresInSeconds);
        }
    }

    /**
     * RefreshToken 생성
     *
     * @param userId 사용자 ID
     * @param token 토큰 문자열
     * @param expiresInSeconds 만료 시간 (초)
     * @return RefreshToken 인스턴스
     */
    public static RefreshToken of(UserId userId, String token, long expiresInSeconds) {
        return new RefreshToken(userId, token, expiresInSeconds);
    }
}
