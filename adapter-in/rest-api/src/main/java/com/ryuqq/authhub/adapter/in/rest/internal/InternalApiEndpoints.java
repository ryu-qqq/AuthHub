package com.ryuqq.authhub.adapter.in.rest.internal;

import com.ryuqq.authhub.adapter.in.rest.common.ApiVersionPaths;

/**
 * InternalApiEndpoints - Internal API 경로 상수
 *
 * <p>Gateway 및 내부 서비스 간 통신을 위한 Internal API 경로를 정의합니다.
 *
 * <p><strong>보안 참고:</strong>
 *
 * <ul>
 *   <li>Internal API는 서비스 토큰 인증으로 보호됩니다
 *   <li>외부 접근이 차단된 내부 네트워크에서만 접근 가능해야 합니다
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class InternalApiEndpoints {

    private InternalApiEndpoints() {}

    /** Internal API 기본 경로 */
    public static final String BASE = ApiVersionPaths.API_VERSION + "/internal";

    /** 엔드포인트-권한 스펙 API 기본 경로 */
    public static final String ENDPOINT_PERMISSIONS = BASE + "/endpoint-permissions";

    /** 엔드포인트-권한 스펙 전체 조회 */
    public static final String ENDPOINT_PERMISSIONS_SPEC = "/spec";

    /** 테넌트 설정 API 기본 경로 */
    public static final String TENANTS = BASE + "/tenants";

    /** 테넌트 설정 조회 */
    public static final String TENANT_CONFIG = "/{tenantId}/config";

    /** 엔드포인트 동기화 API 기본 경로 */
    public static final String ENDPOINTS = BASE + "/endpoints";

    /** 엔드포인트 동기화 */
    public static final String ENDPOINTS_SYNC = "/sync";

    /** 온보딩 API 기본 경로 */
    public static final String ONBOARDING = BASE + "/onboarding";
}
