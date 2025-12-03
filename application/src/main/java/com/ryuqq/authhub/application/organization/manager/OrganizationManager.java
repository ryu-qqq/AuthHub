package com.ryuqq.authhub.application.organization.manager;

import com.ryuqq.authhub.application.organization.port.out.command.OrganizationPersistencePort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrganizationManager - Organization 영속화 트랜잭션 관리자
 *
 * <p>Organization 엔티티의 영속화를 트랜잭션 범위 내에서 수행합니다.
 *
 * <p><strong>구조:</strong>
 *
 * <ul>
 *   <li>Service → Manager → Port 패턴
 *   <li>트랜잭션 경계 관리
 *   <li>단순 위임 역할 (비즈니스 로직 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationManager {

    private final OrganizationPersistencePort organizationPersistencePort;

    public OrganizationManager(OrganizationPersistencePort organizationPersistencePort) {
        this.organizationPersistencePort = organizationPersistencePort;
    }

    @Transactional
    public OrganizationId persist(Organization organization) {
        return organizationPersistencePort.persist(organization);
    }
}
