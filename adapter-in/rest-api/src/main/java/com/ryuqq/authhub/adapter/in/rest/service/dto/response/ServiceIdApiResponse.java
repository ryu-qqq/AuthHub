package com.ryuqq.authhub.adapter.in.rest.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ServiceIdApiResponse - Service ID 응답 DTO
 *
 * <p>Service 생성 후 ID만 반환하는 REST API 응답 DTO입니다.
 *
 * <p>ADTO-001: API Response DTO는 Record로 정의.
 *
 * <p>ADTO-005: *ApiResponse 네이밍.
 *
 * @param serviceId 생성된 Service ID
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "Service ID 응답")
public record ServiceIdApiResponse(
        @Schema(description = "Service ID", example = "1") Long serviceId) {

    /**
     * ServiceIdApiResponse 생성 팩토리 메서드
     *
     * @param serviceId Service ID
     * @return ServiceIdApiResponse
     */
    public static ServiceIdApiResponse of(Long serviceId) {
        return new ServiceIdApiResponse(serviceId);
    }
}
