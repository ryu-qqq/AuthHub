package com.ryuqq.authhub.application.token.port.out.client;

import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;

/**
 * TokenProviderClient - Token Provider Client
 *
 * <p>JWT 토큰 발급/검증을 위한 Outbound Client
 *
 * <p>구현체는 Adapter 레이어에서 JWT 라이브러리로 구현
 *
 * <p><strong>하이브리드 JWT 전략:</strong>
 *
 * <ul>
 *   <li>JWT Payload에 사용자 컨텍스트 정보 포함 (tenant, organization, email)
 *   <li>다른 서비스에서 키 없이 Base64 디코딩으로 정보 확인 가능
 *   <li>Gateway에서 서명 검증 후 X-* 헤더로 실시간 정보 추가 전달
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TokenProviderClient {

    /**
     * Access Token + Refresh Token 쌍 생성 (전체 컨텍스트 포함)
     *
     * <p>JWT Payload에 포함되는 정보:
     *
     * <ul>
     *   <li>sub: userId (UUIDv7)
     *   <li>tid: tenantId (UUIDv7)
     *   <li>tenant_name: 테넌트 이름
     *   <li>oid: organizationId (UUIDv7)
     *   <li>org_name: 조직 이름
     *   <li>email: 사용자 이메일
     *   <li>roles: 역할 목록
     *   <li>permissions: 권한 목록
     * </ul>
     *
     * @param context 토큰 생성에 필요한 사용자/조직 Claim 정보
     * @param rolesAndPermissions 역할/권한 정보
     * @return 토큰 쌍
     */
    TokenResponse generateTokenPair(
            TokenClaimsComposite context, RolesAndPermissionsComposite rolesAndPermissions);
}
