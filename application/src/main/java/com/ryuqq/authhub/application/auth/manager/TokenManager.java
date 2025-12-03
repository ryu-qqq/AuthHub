package com.ryuqq.authhub.application.auth.manager;

import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.application.common.component.TokenProvider;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import com.ryuqq.authhub.application.role.port.in.GetUserRolesUseCase;
import com.ryuqq.authhub.domain.auth.exception.InvalidRefreshTokenException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Component;

/**
 * Token Manager
 *
 * <p>JWT 토큰 발급 및 Refresh Token 저장을 담당
 *
 * <p>주의: 이 클래스의 메서드들은 트랜잭션 외부에서 호출해야 함. JWT 발급/Redis 저장은 외부 시스템 호출이므로 트랜잭션 내에서 수행하면 안됨.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TokenManager {

    private final TokenProvider tokenProvider;
    private final RefreshTokenPersistenceManager refreshTokenPersistenceManager;
    private final RefreshTokenCacheManager refreshTokenCacheManager;
    private final RefreshTokenReader refreshTokenReader;
    private final GetUserRolesUseCase getUserRolesUseCase;

    public TokenManager(
            TokenProvider tokenProvider,
            RefreshTokenPersistenceManager refreshTokenPersistenceManager,
            RefreshTokenCacheManager refreshTokenCacheManager,
            RefreshTokenReader refreshTokenReader,
            GetUserRolesUseCase getUserRolesUseCase) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenPersistenceManager = refreshTokenPersistenceManager;
        this.refreshTokenCacheManager = refreshTokenCacheManager;
        this.refreshTokenReader = refreshTokenReader;
        this.getUserRolesUseCase = getUserRolesUseCase;
    }

    /**
     * 토큰 쌍 발급 및 Refresh Token 저장
     *
     * <p>순서:
     *
     * <ol>
     *   <li>사용자 Role/Permission 조회
     *   <li>JWT Access Token + Refresh Token 생성 (Role/Permission 포함)
     *   <li>Refresh Token → RDB 저장 (트랜잭션)
     *   <li>Refresh Token → Cache(Redis) 저장
     * </ol>
     *
     * @param userId 사용자 ID (Value Object)
     * @return 발급된 토큰 쌍
     */
    public TokenResponse issueTokens(UserId userId) {
        // 1. 사용자 Role/Permission 조회
        UserRolesResponse userRoles = getUserRolesUseCase.execute(userId.value());

        // 2. JWT 토큰 생성 (Role/Permission 포함)
        TokenResponse tokenPair =
                tokenProvider.generateTokenPair(userId, userRoles.roles(), userRoles.permissions());

        saveRefreshToken(userId, tokenPair.refreshToken(), tokenPair.refreshTokenExpiresIn());

        return tokenPair;
    }

    /**
     * Refresh Token 무효화 (로그아웃 시)
     *
     * @param userId 사용자 ID (Value Object)
     */
    public void revokeTokensByUserId(UserId userId) {
        refreshTokenCacheManager.deleteByUserId(userId);
        refreshTokenPersistenceManager.deleteByUserId(userId);
    }

    /**
     * Refresh Token 무효화 (토큰 기준)
     *
     * <p>Cache에서만 삭제합니다. RDB 삭제는 사용자 ID 기준으로만 수행합니다.
     *
     * @param refreshToken Refresh Token 값
     */
    public void revokeToken(String refreshToken) {
        refreshTokenCacheManager.deleteByToken(refreshToken);
    }

    /**
     * Refresh Token으로 토큰 갱신
     *
     * <p>순서:
     *
     * <ol>
     *   <li>Refresh Token으로 사용자 ID 조회 (Cache → RDB fallback)
     *   <li>기존 Refresh Token 무효화
     *   <li>새 토큰 쌍 발급 및 저장
     * </ol>
     *
     * @param refreshToken 기존 Refresh Token
     * @return 새로 발급된 토큰 쌍
     * @throws InvalidRefreshTokenException 유효하지 않은 Refresh Token인 경우
     */
    public TokenResponse refreshTokens(String refreshToken) {
        UserId userId =
                refreshTokenReader
                        .findUserIdByToken(refreshToken)
                        .orElseThrow(InvalidRefreshTokenException::new);

        revokeToken(refreshToken);

        return issueTokens(userId);
    }

    private void saveRefreshToken(UserId userId, String refreshToken, long expiresInSeconds) {
        refreshTokenPersistenceManager.persist(userId, refreshToken);
        refreshTokenCacheManager.save(userId, refreshToken, expiresInSeconds);
    }
}
