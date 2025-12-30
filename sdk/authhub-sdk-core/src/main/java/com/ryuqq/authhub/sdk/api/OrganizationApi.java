package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.organization.CreateOrganizationRequest;
import com.ryuqq.authhub.sdk.model.organization.CreateOrganizationResponse;
import com.ryuqq.authhub.sdk.model.organization.OrganizationResponse;
import com.ryuqq.authhub.sdk.model.organization.OrganizationSummaryResponse;
import com.ryuqq.authhub.sdk.model.organization.UpdateOrganizationRequest;
import com.ryuqq.authhub.sdk.model.organization.UpdateOrganizationStatusRequest;
import java.util.Map;

/** Organization 관련 API 인터페이스. 조직 생성, 조회, 수정, 상태 변경 등의 기능을 제공합니다. */
public interface OrganizationApi {

    /**
     * 새로운 조직을 생성합니다.
     *
     * @param request 조직 생성 요청
     * @return 생성된 조직 ID를 포함한 응답
     */
    ApiResponse<CreateOrganizationResponse> create(CreateOrganizationRequest request);

    /**
     * 조직 ID로 조직을 조회합니다.
     *
     * @param organizationId 조직 ID
     * @return 조직 정보
     */
    ApiResponse<OrganizationResponse> getById(String organizationId);

    /**
     * 조직 목록을 검색합니다.
     *
     * @param queryParams 검색 조건 (tenantId, name, status, page, size, sortKey, sortDirection)
     * @return 조직 목록
     */
    ApiResponse<PageResponse<OrganizationResponse>> search(Map<String, Object> queryParams);

    /**
     * Admin용 조직 목록을 검색합니다.
     *
     * @param queryParams 검색 조건
     * @return Admin용 조직 목록 (Summary 포함)
     */
    ApiResponse<PageResponse<OrganizationSummaryResponse>> searchAdmin(
            Map<String, Object> queryParams);

    /**
     * 조직 정보를 수정합니다.
     *
     * @param organizationId 조직 ID
     * @param request 수정 요청
     * @return 수정된 조직 정보
     */
    ApiResponse<OrganizationResponse> update(
            String organizationId, UpdateOrganizationRequest request);

    /**
     * 조직 상태를 변경합니다.
     *
     * @param organizationId 조직 ID
     * @param request 상태 변경 요청
     * @return 변경된 조직 정보
     */
    ApiResponse<OrganizationResponse> updateStatus(
            String organizationId, UpdateOrganizationStatusRequest request);

    /**
     * 조직을 삭제(비활성화)합니다.
     *
     * @param organizationId 조직 ID
     */
    void delete(String organizationId);
}
