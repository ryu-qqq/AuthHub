package com.ryuqq.authhub.application.token.port.out.query;

import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;

/**
 * RefreshTokenCacheQueryPort - RefreshToken 캐시 Query 포트
 *
 * <p>RefreshToken을 캐시(Redis)에서 조회하는 Query Port입니다.
 *
 * <p><strong>특징:</strong>
 *
 * <ul>
 *   <li>Cache Query 전용 Port (조회)
 *   <li>Value Object 파라미터 (UserId)
 *   <li>Redis 등 캐시 저장소 대상
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RefreshTokenCacheQueryPort {

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
}
