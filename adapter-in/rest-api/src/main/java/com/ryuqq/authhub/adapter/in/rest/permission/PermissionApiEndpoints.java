package com.ryuqq.authhub.adapter.in.rest.permission;

/**
 * PermissionApiEndpoints - Permission API Endpoint 상수
 *
 * <p>Permission 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/permissions
 *   ├── GET    /                    # 목록 조회 (복합 조건)
 *   ├── POST   /                    # 생성
 *   ├── PUT    /{permissionId}      # 수정
 *   └── DELETE /{permissionId}      # 삭제
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionApiEndpoints {

    private PermissionApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Permission 기본 경로 */
    public static final String PERMISSIONS = "/api/v1/auth/permissions";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{permissionId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Permission ID 경로 변수명 */
    public static final String PATH_PERMISSION_ID = "permissionId";
}
