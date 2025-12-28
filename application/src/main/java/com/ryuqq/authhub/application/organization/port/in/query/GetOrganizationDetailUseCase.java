package com.ryuqq.authhub.application.organization.port.in.query;

import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;

/**
 * GetOrganizationDetailUseCase - Admin 조직 상세 조회 UseCase (Query)
 *
 * <p>어드민 친화적 조직 상세 조회를 위한 Port-In 인터페이스입니다. tenantName, users, userCount를 포함한 Detail 정보를 반환합니다.
 *
 * <p><strong>GetOrganizationUseCase와의 차이점:</strong>
 *
 * <ul>
 *   <li>OrganizationDetailResponse 반환 (연관 데이터 포함)
 *   <li>소속 사용자 목록 포함
 *   <li>테넌트명 포함
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>단일 execute 메서드만 제공
 *   <li>Query DTO 입력, Response DTO 출력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see GetOrganizationUseCase 기본 조회 UseCase
 */
public interface GetOrganizationDetailUseCase {

    /**
     * Admin 조직 상세 조회 실행
     *
     * @param query 조회 조건 (조직 ID)
     * @return 조직 상세 정보 (연관 데이터 포함)
     * @throws com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException 조직을 찾을
     *     수 없는 경우
     */
    OrganizationDetailResponse execute(GetOrganizationQuery query);
}
