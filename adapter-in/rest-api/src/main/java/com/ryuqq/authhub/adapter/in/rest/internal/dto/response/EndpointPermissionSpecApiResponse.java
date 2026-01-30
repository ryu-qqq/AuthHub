package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * EndpointPermissionSpecApiResponse - Gateway용 엔드포인트-권한 매핑 스펙 API 응답 DTO
 *
 * <p>Gateway가 URL 기반 권한 검사를 위해 필요한 정보를 제공합니다.
 *
 * @param permissionEndpointId 엔드포인트 ID
 * @param permissionId 권한 ID
 * @param permissionKey 권한 키 (예: "user:read")
 * @param urlPattern URL 패턴 (예: "/api/v1/users/{id}")
 * @param httpMethod HTTP 메서드 (예: "GET", "POST")
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트-권한 매핑 스펙")
public record EndpointPermissionSpecApiResponse(
        @Schema(description = "엔드포인트 ID", example = "1") Long permissionEndpointId,
        @Schema(description = "권한 ID", example = "10") Long permissionId,
        @Schema(description = "권한 키", example = "user:read") String permissionKey,
        @Schema(description = "URL 패턴", example = "/api/v1/users/{id}") String urlPattern,
        @Schema(description = "HTTP 메서드", example = "GET") String httpMethod) {}
