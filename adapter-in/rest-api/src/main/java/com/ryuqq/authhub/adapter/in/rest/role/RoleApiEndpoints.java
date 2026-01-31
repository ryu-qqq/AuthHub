package com.ryuqq.authhub.adapter.in.rest.role;

/**
 * RoleApiEndpoints - Role API Endpoint 상수
 *
 * <p>Role 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/roles
 *   ├── GET    /                         # 목록 조회 (복합 조건)
 *   ├── POST   /                         # 생성
 *   ├── PUT    /{roleId}                 # 수정
 *   ├── DELETE /{roleId}                 # 삭제
 *   ├── POST   /{roleId}/permissions     # 권한 부여
 *   └── DELETE /{roleId}/permissions     # 권한 제거
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleApiEndpoints {

    private RoleApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Role 기본 경로 */
    public static final String ROLES = "/api/v1/auth/roles";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{roleId}";

    /** 권한 관리 경로 (상대경로) */
    public static final String PERMISSIONS = "/{roleId}/permissions";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Role ID 경로 변수명 */
    public static final String PATH_ROLE_ID = "roleId";
}
