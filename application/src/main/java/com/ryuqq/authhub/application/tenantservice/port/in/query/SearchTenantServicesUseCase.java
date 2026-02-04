package com.ryuqq.authhub.application.tenantservice.port.in.query;

import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.application.tenantservice.dto.response.TenantServicePageResult;

/**
 * SearchTenantServicesUseCase - 테넌트-서비스 구독 목록 검색 UseCase (Port-In)
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchTenantServicesUseCase {

    /**
     * 테넌트-서비스 구독 목록 검색 실행
     *
     * @param params 검색 파라미터
     * @return 페이지 결과
     */
    TenantServicePageResult execute(TenantServiceSearchParams params);
}
