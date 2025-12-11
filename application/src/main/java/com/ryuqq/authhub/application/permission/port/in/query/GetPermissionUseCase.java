package com.ryuqq.authhub.application.permission.port.in.query;

import com.ryuqq.authhub.application.permission.dto.query.GetPermissionQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;

/**
 * GetPermissionUseCase - 권한 단건 조회 UseCase (Port-In)
 *
 * <p>권한 단건 조회 기능을 정의합니다.
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
public interface GetPermissionUseCase {

    /**
     * 권한 단건 조회 실행
     *
     * @param query 권한 조회 Query
     * @return 조회된 권한 Response
     */
    PermissionResponse execute(GetPermissionQuery query);
}
