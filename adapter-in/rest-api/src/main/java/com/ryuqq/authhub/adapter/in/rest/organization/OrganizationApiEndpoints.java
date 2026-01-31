package com.ryuqq.authhub.adapter.in.rest.organization;

/**
 * OrganizationApiEndpoints - Organization API Endpoint 상수
 *
 * <p>Organization 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/organizations
 *   ├── GET    /                        # 목록 조회 (복합 조건)
 *   ├── POST   /                        # 생성
 *   ├── PUT    /{organizationId}/name   # 이름 수정
 *   └── PATCH  /{organizationId}/status # 상태 수정
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationApiEndpoints {

    private OrganizationApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Organization 기본 경로 */
    public static final String ORGANIZATIONS = "/api/v1/auth/organizations";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{organizationId}";

    /** 이름 변경 경로 (상대경로) */
    public static final String NAME = "/{organizationId}/name";

    /** 상태 변경 경로 (상대경로) */
    public static final String STATUS = "/{organizationId}/status";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Organization ID 경로 변수명 */
    public static final String PATH_ORGANIZATION_ID = "organizationId";
}
