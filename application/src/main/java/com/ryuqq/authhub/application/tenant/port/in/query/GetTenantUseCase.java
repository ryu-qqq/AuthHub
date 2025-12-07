package com.ryuqq.authhub.application.tenant.port.in.query;

import com.ryuqq.authhub.application.tenant.dto.query.GetTenantQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;

/**
 * GetTenantUseCase - 테넌트 단건 조회 UseCase (Port-In)
 *
 * <p>테넌트 단건 조회 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code Get{Bc}UseCase} 네이밍
 *   <li>{@code execute()} 메서드 시그니처
 *   <li>Query DTO 파라미터, Response DTO 반환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface GetTenantUseCase {

    /**
     * 테넌트 단건 조회 실행
     *
     * @param query 테넌트 조회 Query
     * @return 조회된 테넌트 Response
     */
    TenantResponse execute(GetTenantQuery query);
}
