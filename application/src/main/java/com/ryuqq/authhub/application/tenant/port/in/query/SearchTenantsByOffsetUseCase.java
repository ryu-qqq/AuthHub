package com.ryuqq.authhub.application.tenant.port.in.query;

import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import com.ryuqq.authhub.application.tenant.dto.response.TenantPageResult;

/**
 * SearchTenantsByOffsetUseCase - 테넌트 목록 조회 UseCase (Port-In, Offset 기반)
 *
 * <p>테넌트 목록을 Offset 기반으로 조회하는 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code Search{Bc}ByOffsetUseCase} 네이밍 (Offset 기반 조회)
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>SearchParams DTO 파라미터, PageResult 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchTenantsByOffsetUseCase {

    /**
     * 테넌트 목록 조회 실행 (Offset 기반)
     *
     * @param params 테넌트 검색 파라미터
     * @return 페이징된 테넌트 목록 결과
     */
    TenantPageResult execute(TenantSearchParams params);
}
