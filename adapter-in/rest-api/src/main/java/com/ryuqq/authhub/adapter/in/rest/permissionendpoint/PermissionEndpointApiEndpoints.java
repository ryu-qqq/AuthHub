package com.ryuqq.authhub.adapter.in.rest.permissionendpoint;

/**
 * PermissionEndpointApiEndpoints - PermissionEndpoint API Endpoint 상수
 *
 * <p>PermissionEndpoint 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/permission-endpoints
 *   ├── GET    /                              # 목록 조회 (복합 조건)
 *   ├── POST   /                              # 생성
 *   ├── PUT    /{permissionEndpointId}        # 수정
 *   └── DELETE /{permissionEndpointId}        # 삭제
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionEndpointApiEndpoints {

    private PermissionEndpointApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** PermissionEndpoint 기본 경로 */
    public static final String PERMISSION_ENDPOINTS = "/api/v1/permission-endpoints";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{permissionEndpointId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** PermissionEndpoint ID 경로 변수명 */
    public static final String PATH_PERMISSION_ENDPOINT_ID = "permissionEndpointId";
}
