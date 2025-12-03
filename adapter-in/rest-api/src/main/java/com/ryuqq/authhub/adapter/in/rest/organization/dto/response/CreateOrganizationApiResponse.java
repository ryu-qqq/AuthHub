package com.ryuqq.authhub.adapter.in.rest.organization.dto.response;

import com.ryuqq.authhub.application.organization.dto.response.CreateOrganizationResponse;

/**
 * 조직 생성 API 응답 DTO
 *
 * <p>REST API로 조직 생성 결과를 반환할 때 사용되는 불변 DTO입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record CreateOrganizationApiResponse(Long organizationId) {

    /**
     * Application Layer Response로부터 API Response 생성
     *
     * @param response UseCase 응답
     * @return API 응답 DTO
     */
    public static CreateOrganizationApiResponse from(CreateOrganizationResponse response) {
        return new CreateOrganizationApiResponse(response.organizationId());
    }
}
