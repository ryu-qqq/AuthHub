package com.ryuqq.authhub.application.tenant.port.in.query;

import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;

/**
 * GetTenantDetailUseCase - Admin 테넌트 상세 조회 UseCase
 *
 * <p>어드민 친화적 테넌트 상세 조회를 위한 Port-In입니다.
 *
 * <p><strong>GetTenantUseCase와의 차이점:</strong>
 *
 * <ul>
 *   <li>TenantDetailResponse 반환 (organizations, organizationCount 포함)
 *   <li>추가 API 호출 없이 상세 화면 구성 가능
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
 * @see GetTenantUseCase 기본 조회 UseCase
 */
public interface GetTenantDetailUseCase {

    /**
     * Admin 테넌트 상세 조회 실행
     *
     * @param query 조회 조건
     * @return 테넌트 상세 정보 (연관 데이터 포함)
     * @throws com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException 테넌트가 존재하지 않는 경우
     */
    TenantDetailResponse execute(GetTenantQuery query);
}
