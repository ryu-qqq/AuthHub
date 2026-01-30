package com.ryuqq.authhub.adapter.in.rest.tenant;

/**
 * TenantApiEndpoints - Tenant API Endpoint 상수
 *
 * <p>Tenant 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/tenants
 *   ├── GET    /                    # 목록 조회 (복합 조건)
 *   ├── POST   /                    # 생성
 *   ├── PUT    /{tenantId}/name     # 이름 수정
 *   └── PATCH  /{tenantId}/status   # 상태 수정
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantApiEndpoints {

    private TenantApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Tenant 기본 경로 */
    public static final String TENANTS = "/api/v1/auth/tenants";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{tenantId}";

    /** 이름 변경 경로 (상대경로) */
    public static final String NAME = "/{tenantId}/name";

    /** 상태 변경 경로 (상대경로) */
    public static final String STATUS = "/{tenantId}/status";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Tenant ID 경로 변수명 */
    public static final String PATH_TENANT_ID = "tenantId";
}
