package com.ryuqq.authhub.application.user.port.in.query;

import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;

/**
 * GetUserDetailUseCase - 사용자 상세 조회 Port-In (Admin용)
 *
 * <p>어드민 화면용 확장된 사용자 상세 조회 비즈니스 로직의 진입점입니다.
 *
 * <p><strong>일반 GetUserUseCase와의 차이점:</strong>
 *
 * <ul>
 *   <li>UserDetailResponse 반환 (할당된 역할 목록 포함)
 *   <li>관련 엔티티명 포함 (tenantName, organizationName)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*UseCase 네이밍 규칙
 *   <li>단일 메서드 (execute)
 *   <li>Query DTO 입력, DetailResponse 출력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see GetUserUseCase 기본 사용자 조회 UseCase
 * @see UserDetailResponse Admin 상세용 응답 DTO
 */
public interface GetUserDetailUseCase {

    /**
     * 사용자 상세 조회 실행 (Admin용)
     *
     * @param query 조회 Query
     * @return 사용자 상세 응답 (할당된 역할 포함)
     */
    UserDetailResponse execute(GetUserQuery query);
}
