package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.tenant.CreateTenantRequest;
import com.ryuqq.authhub.sdk.model.tenant.CreateTenantResponse;
import com.ryuqq.authhub.sdk.model.tenant.TenantResponse;
import com.ryuqq.authhub.sdk.model.tenant.TenantSummaryResponse;
import com.ryuqq.authhub.sdk.model.tenant.UpdateTenantNameRequest;
import com.ryuqq.authhub.sdk.model.tenant.UpdateTenantStatusRequest;
import java.util.Map;

/** Tenant 관련 API 인터페이스. 테넌트 생성, 조회, 수정, 상태 변경 등의 기능을 제공합니다. */
public interface TenantApi {

    /**
     * 새로운 테넌트를 생성합니다.
     *
     * @param request 테넌트 생성 요청
     * @return 생성된 테넌트 ID를 포함한 응답
     */
    ApiResponse<CreateTenantResponse> create(CreateTenantRequest request);

    /**
     * 테넌트 ID로 테넌트를 조회합니다.
     *
     * @param tenantId 테넌트 ID
     * @return 테넌트 정보
     */
    ApiResponse<TenantResponse> getById(String tenantId);

    /**
     * 테넌트 목록을 검색합니다.
     *
     * @param queryParams 검색 조건 (name, status, page, size, sortKey, sortDirection)
     * @return 테넌트 목록
     */
    ApiResponse<PageResponse<TenantResponse>> search(Map<String, Object> queryParams);

    /**
     * Admin용 테넌트 목록을 검색합니다.
     *
     * @param queryParams 검색 조건
     * @return Admin용 테넌트 목록 (Summary 포함)
     */
    ApiResponse<PageResponse<TenantSummaryResponse>> searchAdmin(Map<String, Object> queryParams);

    /**
     * 테넌트 이름을 수정합니다.
     *
     * @param tenantId 테넌트 ID
     * @param request 수정 요청
     * @return 수정된 테넌트 정보
     */
    ApiResponse<TenantResponse> updateName(String tenantId, UpdateTenantNameRequest request);

    /**
     * 테넌트 상태를 변경합니다.
     *
     * @param tenantId 테넌트 ID
     * @param request 상태 변경 요청
     * @return 변경된 테넌트 정보
     */
    ApiResponse<TenantResponse> updateStatus(String tenantId, UpdateTenantStatusRequest request);

    /**
     * 테넌트를 삭제(비활성화)합니다.
     *
     * @param tenantId 테넌트 ID
     */
    void delete(String tenantId);
}
