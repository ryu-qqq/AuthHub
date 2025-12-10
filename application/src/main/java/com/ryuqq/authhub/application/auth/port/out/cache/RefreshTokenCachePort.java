package com.ryuqq.authhub.application.auth.port.out.cache;

import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Optional;

/**
 * RefreshTokenCachePort - RefreshToken 캐시 포트
 *
 * <p>RefreshToken을 캐시(Redis)에 저장/조회/삭제하는 Port입니다.
 *
 * <p><strong>특징:</strong>
 *
 * <ul>
 *   <li>Cache 전용 Port (Redis 등 캐시 저장소)
 *   <li>Value Object 파라미터 (UserId)
 *   <li>조회/저장/삭제 모두 지원 (Cache-Aside 패턴)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RefreshTokenCachePort {

    /**
     * RefreshToken 캐시 저장
     *
     * @param userId 사용자 ID (Value Object)
     * @param refreshToken RefreshToken 문자열
     * @param expiresInSeconds 만료 시간 (초)
     */
    void save(UserId userId, String refreshToken, long expiresInSeconds);

    /**
     * UserId로 RefreshToken 조회
     *
     * @param userId 사용자 ID (Value Object)
     * @return RefreshToken 문자열 (Optional)
     */
    Optional<String> findByUserId(UserId userId);

    /**
     * RefreshToken으로 UserId 조회
     *
     * @param refreshToken RefreshToken 값
     * @return UserId (Optional)
     */
    Optional<UserId> findUserIdByToken(String refreshToken);

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
