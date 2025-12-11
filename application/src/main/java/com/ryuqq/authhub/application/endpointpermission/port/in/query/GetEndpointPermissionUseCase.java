package com.ryuqq.authhub.application.endpointpermission.port.in.query;

import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;

/**
 * GetEndpointPermissionUseCase - 엔드포인트 권한 단건 조회 UseCase (Port-In)
 *
 * <p>엔드포인트 권한 단건 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, Response DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetEndpointPermissionUseCase {

    /**
     * 엔드포인트 권한 단건 조회 실행
     *
     * @param query 조회 Query
     * @return 엔드포인트 권한 Response
     */
    EndpointPermissionResponse execute(GetEndpointPermissionQuery query);
}
