package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * EndpointSyncResultApiResponse - 엔드포인트 동기화 결과 API 응답 DTO
 *
 * @param serviceName 서비스 이름
 * @param totalEndpoints 전체 엔드포인트 수
 * @param createdPermissions 생성된 권한 수
 * @param createdEndpoints 생성된 엔드포인트 수
 * @param skippedEndpoints 스킵된 엔드포인트 수 (이미 존재)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "엔드포인트 동기화 결과")
public record EndpointSyncResultApiResponse(
        @Schema(description = "서비스 이름", example = "marketplace") String serviceName,
        @Schema(description = "전체 엔드포인트 수", example = "10") int totalEndpoints,
        @Schema(description = "생성된 권한 수", example = "3") int createdPermissions,
        @Schema(description = "생성된 엔드포인트 수", example = "5") int createdEndpoints,
        @Schema(description = "스킵된 엔드포인트 수", example = "3") int skippedEndpoints,
        @Schema(description = "자동 매핑된 Role-Permission 수", example = "6")
                int mappedRolePermissions) {}
