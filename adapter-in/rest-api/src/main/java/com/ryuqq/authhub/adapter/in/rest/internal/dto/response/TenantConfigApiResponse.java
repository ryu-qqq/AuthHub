package com.ryuqq.authhub.adapter.in.rest.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TenantConfigApiResponse - Gateway용 테넌트 설정 API 응답 DTO
 *
 * <p>Gateway가 테넌트 유효성 검증을 위해 필요한 설정 정보를 제공합니다.
 *
 * @param tenantId 테넌트 ID
 * @param name 테넌트 이름
 * @param status 테넌트 상태 (ACTIVE, INACTIVE)
 * @param active 활성 여부 (Gateway에서 빠른 검증용)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트 설정")
public record TenantConfigApiResponse(
        @Schema(description = "테넌트 ID", example = "01234567-89ab-cdef-0123-456789abcdef")
                String tenantId,
        @Schema(description = "테넌트 이름", example = "Acme Corp") String name,
        @Schema(description = "테넌트 상태", example = "ACTIVE") String status,
        @Schema(description = "활성 여부", example = "true") boolean active) {}
