package com.ryuqq.authhub.application.permission.port.out.command;

import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;

/**
 * PermissionUsagePersistencePort - 권한 사용 이력 영속화 Port (Port-Out)
 *
 * <p>권한 사용 이력 영속화 기능을 정의합니다.
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
public interface PermissionUsagePersistencePort {

    /**
     * PermissionUsage 영속화
     *
     * @param usage PermissionUsage Aggregate
     * @return 영속화된 PermissionUsage (ID 할당됨)
     */
    PermissionUsage persist(PermissionUsage usage);
}
