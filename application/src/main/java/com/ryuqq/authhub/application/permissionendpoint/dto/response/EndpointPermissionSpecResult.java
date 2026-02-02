package com.ryuqq.authhub.application.permissionendpoint.dto.response;

import java.util.List;

/**
 * EndpointPermissionSpecResult - Gateway용 엔드포인트-권한 매핑 스펙 DTO
 *
 * <p>Gateway가 URL 기반 권한 검사를 위해 필요한 정보를 제공합니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param serviceName 서비스 이름 (예: "product-service")
 * @param pathPattern URL 패턴 (예: "/api/v1/users/{id}")
 * @param httpMethod HTTP 메서드 (예: "GET", "POST")
 * @param requiredPermissions 필요 권한 목록 (예: ["user:read"])
 * @param requiredRoles 필요 역할 목록 (예: ["ADMIN"])
 * @param isPublic 공개 엔드포인트 여부 (인증 불필요)
 * @param description 엔드포인트 설명
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionSpecResult(
        String serviceName,
        String pathPattern,
        String httpMethod,
        List<String> requiredPermissions,
        List<String> requiredRoles,
        boolean isPublic,
        String description) {

    /**
     * QueryDSL Projection용 생성자 - 단일 permissionKey를 List로 래핑
     *
     * <p>QueryDSL Projections.constructor에서 사용됩니다.
     *
     * @param serviceName 서비스 이름
     * @param pathPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @param permissionKey 권한 키 (단일)
     * @param isPublic 공개 여부
     * @param description 설명
     */
    public EndpointPermissionSpecResult(
            String serviceName,
            String pathPattern,
            String httpMethod,
            String permissionKey,
            boolean isPublic,
            String description) {
        this(
                serviceName,
                pathPattern,
                httpMethod,
                List.of(permissionKey),
                List.of(),
                isPublic,
                description);
    }
}
