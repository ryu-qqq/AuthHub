package com.ryuqq.authhub.application.common.component;

import com.ryuqq.authhub.application.auth.dto.response.TokenResponse;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Set;

/**
 * Token Provider Port
 *
 * <p>JWT 토큰 발급/검증을 위한 Outbound Port
 *
 * <p>구현체는 Adapter 레이어에서 JWT 라이브러리로 구현
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TokenProvider {

    /**
     * Access Token + Refresh Token 쌍 생성 (Role 및 Permission 포함)
     *
     * @param userId 회원 ID
     * @param roles 역할 목록 (예: ["ROLE_USER", "ROLE_ADMIN"])
     * @param permissions 권한 목록 (예: ["user:read", "user:write"])
     * @return 토큰 쌍
     */
    TokenResponse generateTokenPair(UserId userId, Set<String> roles, Set<String> permissions);
}
