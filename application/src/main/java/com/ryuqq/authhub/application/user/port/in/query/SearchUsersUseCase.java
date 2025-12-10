package com.ryuqq.authhub.application.user.port.in.query;

import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import java.util.List;

/**
 * SearchUsersUseCase - 사용자 목록 검색 Port-In
 *
 * <p>사용자 목록 검색 비즈니스 로직의 진입점입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*UseCase 네이밍 규칙
 *   <li>단일 메서드 (execute)
 *   <li>Query DTO 입력, Response DTO 목록 출력
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchUsersUseCase {

    /**
     * 사용자 목록 검색 실행
     *
     * @param query 검색 Query
     * @return 사용자 응답 목록
     */
    List<UserResponse> execute(SearchUsersQuery query);
}
