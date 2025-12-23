package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PermissionUsageApiResponse - 권한 사용 이력 API 응답 DTO
 *
 * @param usageId 사용 이력 ID
 * @param permissionKey 권한 키
 * @param serviceName 서비스명
 * @param locations 코드 위치 목록
 * @param lastScannedAt 마지막 스캔 시간
 * @param createdAt 생성 시간
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "권한 사용 이력 응답")
public record PermissionUsageApiResponse(
        @Schema(description = "사용 이력 ID") UUID usageId,
        @Schema(description = "권한 키", example = "product:read") String permissionKey,
        @Schema(description = "서비스명", example = "product-service") String serviceName,
        @Schema(description = "코드 위치 목록") List<String> locations,
        @Schema(description = "마지막 스캔 시간") Instant lastScannedAt,
        @Schema(description = "생성 시간") Instant createdAt) {}
