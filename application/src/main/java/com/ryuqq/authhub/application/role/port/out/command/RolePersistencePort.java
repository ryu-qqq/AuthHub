package com.ryuqq.authhub.application.role.port.out.command;

import com.ryuqq.authhub.domain.role.aggregate.Role;

/**
 * RolePersistencePort - 역할 저장 Port (Port-Out)
 *
 * <p>역할 저장 기능을 정의합니다.
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
public interface RolePersistencePort {

    /**
     * 역할 저장 (생성/수정)
     *
     * @param role 저장할 역할 도메인
     * @return 저장된 역할 도메인 (ID 할당됨)
     */
    Role persist(Role role);
}
