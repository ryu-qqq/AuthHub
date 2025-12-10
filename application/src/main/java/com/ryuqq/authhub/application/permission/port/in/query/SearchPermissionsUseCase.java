package com.ryuqq.authhub.application.permission.port.in.query;

import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import java.util.List;

/**
 * SearchPermissionsUseCase - 권한 검색 UseCase (Port-In)
 *
 * <p>권한 검색 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, Page Response 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchPermissionsUseCase {

    /**
     * 권한 검색 실행
     *
     * @param query 권한 검색 Query
     * @return 권한 Response 목록
     */
    List<PermissionResponse> execute(SearchPermissionsQuery query);
}
