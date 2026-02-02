package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * EndpointPermissionSpecApiResponse - Gateway용 엔드포인트-권한 매핑 스펙 API 응답 DTO
 *
 * <p>Gateway가 URL 기반 권한 검사를 위해 필요한 정보를 제공합니다.
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
@Schema(description = "엔드포인트-권한 매핑 스펙")
public record EndpointPermissionSpecApiResponse(
        @Schema(description = "서비스 이름", example = "product-service") String serviceName,
        @Schema(description = "URL 패턴", example = "/api/v1/users/{id}") String pathPattern,
        @Schema(description = "HTTP 메서드", example = "GET") String httpMethod,
        @Schema(description = "필요 권한 목록", example = "[\"user:read\"]")
                List<String> requiredPermissions,
        @Schema(description = "필요 역할 목록", example = "[\"ADMIN\"]") List<String> requiredRoles,
        @Schema(description = "공개 엔드포인트 여부", example = "false") boolean isPublic,
        @Schema(description = "엔드포인트 설명", example = "사용자 조회 API") String description) {}
