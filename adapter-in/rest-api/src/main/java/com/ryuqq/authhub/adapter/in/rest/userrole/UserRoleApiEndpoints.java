package com.ryuqq.authhub.adapter.in.rest.userrole;

import com.ryuqq.authhub.adapter.in.rest.user.UserApiEndpoints;

/**
 * UserRoleApiEndpoints - UserRole API Endpoint 상수
 *
 * <p>UserRole 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/users/{userId}/roles
 *   ├── POST   # 역할 할당 (Assign)
 *   └── DELETE # 역할 철회 (Revoke)
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserRoleApiEndpoints {

    private UserRoleApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** UserRole 기본 경로 (users 하위) */
    public static final String BASE = UserApiEndpoints.USERS;

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** 역할 관리 경로 (상대경로) */
    public static final String ROLES = "/{userId}/roles";

    // ============================================
    // Path Variable Names
    // ============================================

    /** User ID 경로 변수명 */
    public static final String PATH_USER_ID = "userId";
}
