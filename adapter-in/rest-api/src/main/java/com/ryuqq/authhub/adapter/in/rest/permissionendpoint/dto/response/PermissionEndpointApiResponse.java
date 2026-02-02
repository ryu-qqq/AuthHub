package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * PermissionEndpointApiResponse - PermissionEndpoint API Response
 *
 * <p>PermissionEndpoint REST API 응답 DTO입니다.
 *
 * @param permissionEndpointId 엔드포인트 ID
 * @param permissionId 연결된 권한 ID
 * @param serviceName 서비스 이름
 * @param urlPattern URL 패턴
 * @param httpMethod HTTP 메서드
 * @param description 설명
 * @param isPublic 공개 엔드포인트 여부
 * @param createdAt 생성 일시 (ISO 8601 형식)
 * @param updatedAt 수정 일시 (ISO 8601 형식)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "PermissionEndpoint 응답 DTO")
public record PermissionEndpointApiResponse(
        @Schema(description = "엔드포인트 ID", example = "1") Long permissionEndpointId,
        @Schema(description = "연결된 권한 ID", example = "1") Long permissionId,
        @Schema(description = "서비스 이름", example = "product-service") String serviceName,
        @Schema(description = "URL 패턴", example = "/api/v1/users/{id}") String urlPattern,
        @Schema(description = "HTTP 메서드", example = "GET") String httpMethod,
        @Schema(description = "설명", example = "사용자 상세 조회 API") String description,
        @Schema(description = "공개 엔드포인트 여부", example = "false") boolean isPublic,
        @Schema(description = "생성 일시", example = "2024-01-15T10:30:00Z") String createdAt,
        @Schema(description = "수정 일시", example = "2024-01-15T10:30:00Z") String updatedAt) {}
