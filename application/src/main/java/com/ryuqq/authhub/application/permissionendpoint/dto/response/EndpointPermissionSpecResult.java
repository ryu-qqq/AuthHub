package com.ryuqq.authhub.application.permissionendpoint.dto.response;

/**
 * EndpointPermissionSpecResult - Gateway용 엔드포인트-권한 매핑 스펙 DTO
 *
 * <p>Gateway가 URL 기반 권한 검사를 위해 필요한 정보를 제공합니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param permissionEndpointId 엔드포인트 ID
 * @param permissionId 권한 ID
 * @param permissionKey 권한 키 (예: "user:read")
 * @param urlPattern URL 패턴 (예: "/api/v1/users/{id}")
 * @param httpMethod HTTP 메서드 (예: "GET", "POST")
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionSpecResult(
        Long permissionEndpointId,
        Long permissionId,
        String permissionKey,
        String urlPattern,
        String httpMethod) {}
