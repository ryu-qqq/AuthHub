package com.ryuqq.authhub.application.role.port.in.query;

import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import java.util.List;

/**
 * SearchRolesUseCase - 역할 검색 UseCase (Port-In)
 *
 * <p>역할 검색 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, List Response 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchRolesUseCase {

    /**
     * 역할 검색 실행
     *
     * @param query 역할 검색 Query
     * @return 역할 Response 목록
     */
    List<RoleResponse> execute(SearchRolesQuery query);
}
