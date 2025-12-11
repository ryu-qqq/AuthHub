package com.ryuqq.authhub.application.endpointpermission.port.in.query;

import com.ryuqq.authhub.application.endpointpermission.dto.query.GetServiceEndpointPermissionSpecQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecListResponse;

/**
 * GetAllEndpointPermissionSpecUseCase - 서비스별 엔드포인트 권한 스펙 조회 UseCase (Port-In)
 *
 * <p>Gateway에서 캐싱할 서비스별 엔드포인트 권한 스펙 목록을 조회합니다.
 *
 * <p><strong>용도:</strong>
 *
 * <ul>
 *   <li>Gateway 서버 시작 시 서비스별 스펙 로드
 *   <li>Webhook 수신 후 스펙 갱신
 *   <li>주기적인 동기화
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Action}{Bc}UseCase} 네이밍
 *   <li>{@code execute(Query)} 메서드 시그니처
 *   <li>Response DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetAllEndpointPermissionSpecUseCase {

    /**
     * 서비스별 엔드포인트 권한 스펙 조회 실행
     *
     * <p>해당 서비스의 엔드포인트 권한 스펙을 조회하여 Gateway 캐싱용 응답을 반환합니다.
     *
     * @param query 조회 조건 (서비스명)
     * @return 엔드포인트 권한 스펙 목록 Response
     */
    EndpointPermissionSpecListResponse execute(GetServiceEndpointPermissionSpecQuery query);
}
