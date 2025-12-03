package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

import com.ryuqq.authhub.application.tenant.dto.response.CreateTenantResponse;

/**
 * 테넌트 생성 API 응답 DTO
 *
 * <p>REST API로 테넌트 생성 결과를 반환할 때 사용되는 불변 DTO입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record CreateTenantApiResponse(Long tenantId) {

    /**
     * Application Layer Response로부터 API Response 생성
     *
     * @param response UseCase 응답
     * @return API 응답 DTO
     */
    public static CreateTenantApiResponse from(CreateTenantResponse response) {
        return new CreateTenantApiResponse(response.tenantId());
    }
}
