package com.ryuqq.authhub.application.endpointpermission.port.in.query;

import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import java.util.List;

/**
 * SearchEndpointPermissionsUseCase - 엔드포인트 권한 검색 UseCase (Port-In)
 *
 * <p>엔드포인트 권한 검색 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, Response DTO 목록 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface SearchEndpointPermissionsUseCase {

    /**
     * 엔드포인트 권한 검색 실행
     *
     * @param query 검색 Query
     * @return 엔드포인트 권한 Response 목록
     */
    List<EndpointPermissionResponse> execute(SearchEndpointPermissionsQuery query);

    /**
     * 검색 결과 총 개수 조회
     *
     * @param query 검색 Query
     * @return 총 개수
     */
    long count(SearchEndpointPermissionsQuery query);
}
