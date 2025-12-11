package com.ryuqq.authhub.application.endpointpermission.port.in.query;

import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionSpecQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecResponse;
import java.util.Optional;

/**
 * GetEndpointPermissionSpecUseCase - 엔드포인트 권한 스펙 조회 UseCase (Port-In)
 *
 * <p>인증/인가 처리를 위한 엔드포인트 권한 스펙 조회 기능을 정의합니다.
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
public interface GetEndpointPermissionSpecUseCase {

    /**
     * 엔드포인트 권한 스펙 조회 실행
     *
     * <p>요청 경로와 메서드에 해당하는 엔드포인트 권한 스펙을 조회합니다. 존재하지 않는 경우 Optional.empty() 반환 (공개 엔드포인트로 처리할지는 호출자
     * 판단)
     *
     * @param query 조회 Query
     * @return 엔드포인트 권한 스펙 Response (Optional)
     */
    Optional<EndpointPermissionSpecResponse> execute(GetEndpointPermissionSpecQuery query);
}
