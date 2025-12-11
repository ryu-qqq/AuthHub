package com.ryuqq.authhub.application.role.port.in.query;

import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;

/**
 * GetRoleUseCase - 역할 단건 조회 UseCase (Port-In)
 *
 * <p>역할 단건 조회 기능을 정의합니다.
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
public interface GetRoleUseCase {

    /**
     * 역할 단건 조회 실행
     *
     * @param query 역할 조회 Query
     * @return 역할 Response
     */
    RoleResponse execute(GetRoleQuery query);
}
