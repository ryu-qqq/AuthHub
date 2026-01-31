package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TenantIdApiResponse - Tenant ID 응답 DTO
 *
 * <p>Tenant 생성 후 ID만 반환하는 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param tenantId 생성된 Tenant ID
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Tenant ID 응답")
public record TenantIdApiResponse(
        @Schema(description = "Tenant ID", example = "550e8400-e29b-41d4-a716-446655440000")
                String tenantId) {

    /**
     * TenantIdApiResponse 생성 팩토리 메서드
     *
     * @param tenantId Tenant ID
     * @return TenantIdApiResponse
     */
    public static TenantIdApiResponse of(String tenantId) {
        return new TenantIdApiResponse(tenantId);
    }
}
