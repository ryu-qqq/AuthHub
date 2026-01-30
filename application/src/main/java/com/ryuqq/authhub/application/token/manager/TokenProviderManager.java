package com.ryuqq.authhub.application.token.manager;

import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.application.token.port.out.client.TokenProviderClient;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import org.springframework.stereotype.Component;

/**
 * TokenProviderManager - 토큰 Provider 관리자
 *
 * <p>JWT 토큰 생성을 담당하는 Manager입니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>TokenProviderClient를 래핑하여 토큰 생성
 *   <li>외부 시스템(JWT 라이브러리) 호출 캡슐화
 * </ul>
 *
 * <p><strong>주의:</strong> 트랜잭션 외부에서 호출해야 합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TokenProviderManager {

    private final TokenProviderClient tokenProviderClient;

    public TokenProviderManager(TokenProviderClient tokenProviderClient) {
        this.tokenProviderClient = tokenProviderClient;
    }

    /**
     * Access Token + Refresh Token 쌍 생성
     *
     * @param context 토큰 생성에 필요한 사용자/조직 Claim 정보
     * @param rolesAndPermissions 역할/권한 정보
     * @return 토큰 쌍 (TokenResponse)
     */
    public TokenResponse generateTokenPair(
            TokenClaimsComposite context, RolesAndPermissionsComposite rolesAndPermissions) {
        return tokenProviderClient.generateTokenPair(context, rolesAndPermissions);
    }
}
