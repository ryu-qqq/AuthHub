package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.internal.EndpointPermissionSpecList;
import com.ryuqq.authhub.sdk.model.internal.PublicKeys;
import com.ryuqq.authhub.sdk.model.internal.TenantConfig;
import com.ryuqq.authhub.sdk.model.internal.UserPermissions;

/**
 * Internal API 인터페이스.
 *
 * <p>Gateway 및 내부 서비스 간 통신을 위한 API를 제공합니다. 서비스 토큰 인증을 사용합니다.
 */
public interface InternalApi {

    /**
     * 엔드포인트-권한 스펙 전체 조회.
     *
     * <p>Gateway가 시작 시 또는 갱신 시 호출하여 전체 스펙을 캐싱합니다.
     *
     * @return 엔드포인트-권한 스펙 목록
     */
    ApiResponse<EndpointPermissionSpecList> getPermissionSpec();

    /**
     * JWKS 공개키 조회 (JWT 서명 검증용).
     *
     * <p>Gateway가 JWT 서명 검증을 위해 공개키를 조회합니다.
     *
     * @return 공개키 목록 (RFC 7517 JWKS 형식)
     */
    PublicKeys getJwks();

    /**
     * 테넌트 설정 조회 (테넌트 유효성 검증용).
     *
     * <p>Gateway가 요청 처리 시 테넌트 활성 여부를 검증하기 위해 호출합니다.
     *
     * @param tenantId 테넌트 ID
     * @return 테넌트 설정
     */
    ApiResponse<TenantConfig> getTenantConfig(String tenantId);

    /**
     * 사용자 권한 조회 (인가 검증용).
     *
     * <p>Gateway가 사용자 인가 검증을 위해 역할/권한 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 역할/권한 정보
     */
    ApiResponse<UserPermissions> getUserPermissions(String userId);
}
