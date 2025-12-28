package com.ryuqq.authhub.application.user.port.in.query;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;

/**
 * SearchUsersAdminUseCase - 사용자 목록 검색 Port-In (Admin용)
 *
 * <p>어드민 화면용 확장된 사용자 검색 비즈니스 로직의 진입점입니다.
 *
 * <p><strong>일반 SearchUsersUseCase와의 차이점:</strong>
 *
 * <ul>
 *   <li>PageResponse 반환 (페이징 정보 포함)
 *   <li>UserSummaryResponse 반환 (관련 엔티티명, roleCount 포함)
 *   <li>확장 필터 지원 (날짜 범위, 정렬, 역할 필터)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*UseCase 네이밍 규칙
 *   <li>단일 메서드 (execute)
 *   <li>Query DTO 입력, PageResponse 출력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see SearchUsersUseCase 기본 사용자 검색 UseCase
 * @see UserSummaryResponse Admin 목록용 응답 DTO
 */
public interface SearchUsersAdminUseCase {

    /**
     * 사용자 목록 검색 실행 (Admin용)
     *
     * @param query 검색 Query (확장 필터 포함)
     * @return 페이징된 사용자 Summary 목록
     */
    PageResponse<UserSummaryResponse> execute(SearchUsersQuery query);
}
