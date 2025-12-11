package com.ryuqq.authhub.application.endpointpermission.port.out.command;

import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;

/**
 * EndpointPermissionPersistencePort - 엔드포인트 권한 영속화 Port (Port-Out)
 *
 * <p>엔드포인트 권한 영속화 기능을 정의합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code {Bc}PersistencePort} 네이밍
 *   <li>Domain Aggregate 파라미터/반환
 *   <li>구현은 Adapter 책임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface EndpointPermissionPersistencePort {

    /**
     * EndpointPermission 영속화
     *
     * @param endpointPermission EndpointPermission Aggregate
     * @return 영속화된 EndpointPermission
     */
    EndpointPermission persist(EndpointPermission endpointPermission);
}
