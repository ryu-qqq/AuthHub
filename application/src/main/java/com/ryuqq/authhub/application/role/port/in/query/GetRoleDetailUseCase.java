package com.ryuqq.authhub.application.role.port.in.query;

import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;

/**
 * GetRoleDetailUseCase - 역할 상세 조회 UseCase (Admin용)
 *
 * <p>Admin 화면을 위한 역할 상세 조회 인터페이스입니다. 역할에 할당된 권한 목록을 포함한 상세 정보를 반환합니다.
 *
 * <p><strong>일반 GetRoleUseCase와의 차이점:</strong>
 *
 * <ul>
 *   <li>RoleDetailResponse 반환 (권한 목록, userCount 포함)
 *   <li>tenantName 포함
 *   <li>Admin 화면 상세 조회에 최적화
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>UseCase 인터페이스 (Port-In)
 *   <li>단일 execute() 메서드
 *   <li>Application Layer에서 정의
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see GetRoleUseCase 기본 역할 조회 UseCase
 * @see RoleDetailResponse 상세 조회용 응답 DTO
 */
public interface GetRoleDetailUseCase {

    /**
     * 역할 상세 조회 (Admin용)
     *
     * @param query 역할 조회 쿼리
     * @return 역할 상세 정보 (권한 목록 포함)
     * @throws com.ryuqq.authhub.domain.role.exception.RoleNotFoundException 역할을 찾을 수 없는 경우
     */
    RoleDetailResponse execute(GetRoleQuery query);
}
