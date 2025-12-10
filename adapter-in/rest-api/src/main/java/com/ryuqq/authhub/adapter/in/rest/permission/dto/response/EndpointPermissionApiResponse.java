package com.ryuqq.authhub.adapter.in.rest.permission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * EndpointPermissionApiResponse - 엔드포인트별 권한 API 응답 DTO
 *
 * <p>개별 엔드포인트의 권한 정보를 표현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트별 권한 응답")
public record EndpointPermissionApiResponse(
        @Schema(description = "서비스명") String serviceName,
        @Schema(description = "엔드포인트 경로") String path,
        @Schema(description = "HTTP 메서드") String method,
        @Schema(description = "필요 권한 목록") List<String> requiredPermissions,
        @Schema(description = "필요 역할 목록") List<String> requiredRoles,
        @Schema(description = "공개 엔드포인트 여부") boolean isPublic) {}
