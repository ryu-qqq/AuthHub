package com.ryuqq.authhub.adapter.in.rest.rolepermission;

import com.ryuqq.authhub.adapter.in.rest.role.RoleApiEndpoints;

/**
 * RolePermissionApiEndpoints - Role Permission API Endpoint 상수
 *
 * <p>Role Permission 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/roles/{roleId}/permissions
 *   ├── POST   # 권한 부여 (Grant)
 *   └── DELETE # 권한 제거 (Revoke)
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RolePermissionApiEndpoints {

    private RolePermissionApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Role Permission 기본 경로 (roles 하위) */
    public static final String BASE = RoleApiEndpoints.ROLES;

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** 권한 관리 경로 (상대경로) */
    public static final String PERMISSIONS = "/{roleId}/permissions";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Role ID 경로 변수명 */
    public static final String PATH_ROLE_ID = "roleId";
}
