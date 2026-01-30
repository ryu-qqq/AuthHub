package com.ryuqq.authhub.application.token.port.out.command;

import com.ryuqq.authhub.domain.user.id.UserId;

/**
 * RefreshTokenCacheCommandPort - RefreshToken 캐시 Command 포트
 *
 * <p>RefreshToken을 캐시(Redis)에 저장/삭제하는 Command Port입니다.
 *
 * <p><strong>특징:</strong>
 *
 * <ul>
 *   <li>Cache Command 전용 Port (저장/삭제)
 *   <li>Value Object 파라미터 (UserId)
 *   <li>Redis 등 캐시 저장소 대상
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RefreshTokenCacheCommandPort {

    /**
     * RefreshToken 캐시 저장
     *
     * @param userId 사용자 ID (Value Object)
     * @param refreshToken RefreshToken 문자열
     * @param expiresInSeconds 만료 시간 (초)
     */
    void save(UserId userId, String refreshToken, long expiresInSeconds);

    /**
     * UserId로 RefreshToken 삭제
     *
     * @param userId 사용자 ID (Value Object)
     */
    void deleteByUserId(UserId userId);

    /**
     * RefreshToken 값으로 삭제
     *
     * @param refreshToken RefreshToken 값
     */
    void deleteByToken(String refreshToken);
}
