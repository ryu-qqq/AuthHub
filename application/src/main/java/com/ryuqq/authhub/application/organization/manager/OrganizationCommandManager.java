package com.ryuqq.authhub.application.organization.manager;

import com.ryuqq.authhub.application.organization.port.out.command.OrganizationCommandPort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrganizationCommandManager - Organization Command 관리자
 *
 * <p>CommandPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationCommandManager {

    private final OrganizationCommandPort persistencePort;

    public OrganizationCommandManager(OrganizationCommandPort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * Organization 영속화
     *
     * @param organization 영속화할 Organization
     * @return 영속화된 Organization ID (String)
     */
    @Transactional
    public String persist(Organization organization) {
        return persistencePort.persist(organization);
    }
}
