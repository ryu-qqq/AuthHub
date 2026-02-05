package com.ryuqq.authhub.adapter.in.rest.tenantservice;

/**
 * TenantServiceApiEndpoints - TenantService API Endpoint 상수
 *
 * <p>TenantService 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/tenant-services
 *   ├── GET    /                              # 목록 조회 (복합 조건)
 *   ├── POST   /                              # 구독 생성
 *   └── PUT    /{tenantServiceId}/status       # 상태 변경
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantServiceApiEndpoints {

    private TenantServiceApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** TenantService 기본 경로 */
    public static final String TENANT_SERVICES = "/api/v1/auth/tenant-services";

    // ============================================
    // Relative Paths
    // ============================================

    /** ID + 상태 경로 (상대경로) */
    public static final String STATUS = "/{tenantServiceId}/status";

    // ============================================
    // Path Variable Names
    // ============================================

    /** TenantService ID 경로 변수명 */
    public static final String PATH_TENANT_SERVICE_ID = "tenantServiceId";
}
