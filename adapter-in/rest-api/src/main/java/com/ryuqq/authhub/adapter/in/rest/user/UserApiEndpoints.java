package com.ryuqq.authhub.adapter.in.rest.user;

/**
 * UserApiEndpoints - User API Endpoint 상수
 *
 * <p>User 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/users
 *   ├── GET    /                  # 목록 조회 (복합 조건)
 *   ├── POST   /                  # 생성
 *   ├── GET    /{userId}          # 단건 조회
 *   ├── PUT    /{userId}          # 정보 수정
 *   ├── PUT    /{userId}/password # 비밀번호 변경
 *   ├── POST   /{userId}/roles    # 역할 할당
 *   └── DELETE /{userId}/roles    # 역할 제거
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserApiEndpoints {

    private UserApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** User 기본 경로 */
    public static final String USERS = "/api/v1/auth/users";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{userId}";

    /** 비밀번호 변경 경로 (상대경로) */
    public static final String PASSWORD = "/{userId}/password";

    /** 역할 관리 경로 (상대경로) */
    public static final String ROLES = "/{userId}/roles";

    // ============================================
    // Path Variable Names
    // ============================================

    /** User ID 경로 변수명 */
    public static final String PATH_USER_ID = "userId";
}
