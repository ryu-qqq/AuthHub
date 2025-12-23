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
     * PermissionUsage 영속화 (UPSERT)
     *
     * <p>UPSERT 동작: (permissionKey, serviceName) 조합이 이미 존재하면 업데이트, 없으면 신규 생성합니다.
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>기존 이력 조회 (permissionKey + serviceName)
     *   <li>존재하면 → locations, lastScannedAt 업데이트
     *   <li>존재하지 않으면 → 신규 ID 발급 후 생성
     * </ol>
     *
     * @param usage PermissionUsage Aggregate
     * @return 영속화된 PermissionUsage (ID 할당됨)
     */
    PermissionUsage persist(PermissionUsage usage);
}
