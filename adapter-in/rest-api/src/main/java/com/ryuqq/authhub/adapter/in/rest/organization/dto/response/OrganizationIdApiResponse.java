package com.ryuqq.authhub.adapter.in.rest.organization.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OrganizationIdApiResponse - Organization ID 응답 DTO
 *
 * <p>Organization 생성/수정 후 ID만 반환하는 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param organizationId 생성/수정된 Organization ID
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Organization ID 응답")
public record OrganizationIdApiResponse(
        @Schema(description = "Organization ID", example = "550e8400-e29b-41d4-a716-446655440000")
                String organizationId) {

    /**
     * OrganizationIdApiResponse 생성 팩토리 메서드
     *
     * @param organizationId Organization ID
     * @return OrganizationIdApiResponse
     */
    public static OrganizationIdApiResponse of(String organizationId) {
        return new OrganizationIdApiResponse(organizationId);
    }
}
