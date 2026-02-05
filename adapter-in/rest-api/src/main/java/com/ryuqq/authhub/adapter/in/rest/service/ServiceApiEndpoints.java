package com.ryuqq.authhub.adapter.in.rest.service;

/**
 * ServiceApiEndpoints - Service API Endpoint 상수
 *
 * <p>Service 도메인 전용 API Endpoint 경로를 중앙 관리합니다.
 *
 * <p><strong>API 구조:</strong>
 *
 * <pre>{@code
 * /api/v1/auth/services
 *   ├── GET    /                         # 목록 조회 (복합 조건)
 *   ├── POST   /                         # 생성
 *   └── PUT    /{serviceId}              # 수정
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ServiceApiEndpoints {

    private ServiceApiEndpoints() {
        // Utility class - prevent instantiation
    }

    // ============================================
    // Base Paths
    // ============================================

    /** Service 기본 경로 */
    public static final String SERVICES = "/api/v1/auth/services";

    // ============================================
    // Relative Paths (for @GetMapping, @PutMapping, etc.)
    // ============================================

    /** ID 경로 (상대경로) */
    public static final String ID = "/{serviceId}";

    // ============================================
    // Path Variable Names
    // ============================================

    /** Service ID 경로 변수명 */
    public static final String PATH_SERVICE_ID = "serviceId";
}
