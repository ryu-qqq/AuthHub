package com.ryuqq.authhub.adapter.in.rest.auth.component;

import java.util.Optional;
import java.util.Set;

/**
 * JWT Claims 추출기 인터페이스
 *
 * <p>Authorization 헤더의 JWT 토큰을 검증하고 Claims를 추출합니다.
 *
 * <p>사용 시나리오:
 *
 * <ul>
 *   <li>Gateway 없이 AuthHub 직접 호출 시 JWT 인증
 *   <li>로컬 개발 환경에서의 테스트
 *   <li>프론트엔드 직접 연동
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface JwtClaimsExtractor {

    /**
     * JWT 토큰 검증 및 Claims 추출
     *
     * @param token JWT 토큰 (Bearer prefix 없이)
     * @return 유효한 토큰이면 JwtClaims, 유효하지 않으면 empty
     */
    Optional<JwtClaims> extractClaims(String token);

    /**
     * JWT Claims 데이터
     *
     * @param userId 사용자 ID (sub claim)
     * @param tenantId 테넌트 ID (tid claim)
     * @param organizationId 조직 ID (oid claim)
     * @param roles 역할 목록
     * @param permissions 권한 목록
     */
    record JwtClaims(
            String userId,
            String tenantId,
            String organizationId,
            Set<String> roles,
            Set<String> permissions) {}
}
