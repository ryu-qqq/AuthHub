package com.ryuqq.authhub.application.tenant.port.in.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;

/**
 * SearchTenantsUseCase - 테넌트 목록 조회 UseCase (Port-In)
 *
 * <p>Admin용 테넌트 목록 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code Search{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, PageResponse 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchTenantsUseCase {

    /**
     * 테넌트 목록 조회 실행
     *
     * @param query 테넌트 검색 Query
     * @return 페이징된 테넌트 목록 Response
     */
    PageResponse<TenantResponse> execute(SearchTenantsQuery query);
}
