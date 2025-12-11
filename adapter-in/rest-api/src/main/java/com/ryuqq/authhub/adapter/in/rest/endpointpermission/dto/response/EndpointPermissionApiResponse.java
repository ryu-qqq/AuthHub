package com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Set;

/**
 * EndpointPermissionApiResponse - 엔드포인트 권한 API 응답 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지
 *   <li>Record 사용
 * </ul>
 *
 * @param id 엔드포인트 권한 ID
 * @param serviceName 서비스 이름
 * @param path 경로
 * @param method HTTP 메서드
 * @param description 설명
 * @param isPublic 공개 여부
 * @param requiredPermissions 필요 권한 목록
 * @param requiredRoles 필요 역할 목록
 * @param version 버전
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트 권한 응답")
public record EndpointPermissionApiResponse(
        @Schema(description = "엔드포인트 권한 ID", example = "550e8400-e29b-41d4-a716-446655440000")
                String id,
        @Schema(description = "서비스 이름", example = "auth-hub") String serviceName,
        @Schema(description = "경로", example = "/api/v1/users/{userId}") String path,
        @Schema(description = "HTTP 메서드", example = "GET") String method,
        @Schema(description = "설명", example = "사용자 상세 조회 엔드포인트") String description,
        @Schema(description = "공개 여부", example = "false") boolean isPublic,
        @Schema(description = "필요 권한 목록", example = "[\"user:read\"]")
                Set<String> requiredPermissions,
        @Schema(description = "필요 역할 목록", example = "[\"ADMIN\", \"USER_MANAGER\"]")
                Set<String> requiredRoles,
        @Schema(description = "버전", example = "0") Long version,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
