package com.ryuqq.authhub.application.tenant.port.in.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;

/**
 * SearchTenantsAdminUseCase - Admin 테넌트 목록 검색 UseCase
 *
 * <p>어드민 친화적 테넌트 목록 검색을 위한 Port-In입니다.
 *
 * <p><strong>SearchTenantsUseCase와의 차이점:</strong>
 *
 * <ul>
 *   <li>확장 필터 지원 (날짜 범위, 정렬)
 *   <li>TenantSummaryResponse 반환 (organizationCount 포함)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>단일 execute 메서드만 정의
 *   <li>Query DTO 파라미터
 *   <li>{@code @Transactional} 금지 (Manager/Facade에서 관리)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SearchTenantsUseCase 기본 검색 UseCase
 */
public interface SearchTenantsAdminUseCase {

    /**
     * Admin 테넌트 목록 검색 실행
     *
     * @param query 검색 조건 (확장 필터 포함)
     * @return 페이징된 테넌트 Summary 목록
     */
    PageResponse<TenantSummaryResponse> execute(SearchTenantsQuery query);
}
