package com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TenantServiceIdApiResponse - TenantService ID 응답 DTO
 *
 * <p>테넌트-서비스 구독 생성 후 ID만 반환하는 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param tenantServiceId 생성된 TenantService ID
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "TenantService ID 응답")
public record TenantServiceIdApiResponse(
        @Schema(description = "TenantService ID", example = "1") Long tenantServiceId) {

    /**
     * TenantServiceIdApiResponse 생성 팩토리 메서드
     *
     * @param tenantServiceId TenantService ID
     * @return TenantServiceIdApiResponse
     */
    public static TenantServiceIdApiResponse of(Long tenantServiceId) {
        return new TenantServiceIdApiResponse(tenantServiceId);
    }
}
