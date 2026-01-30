package com.ryuqq.authhub.application.tenant.manager;

import com.ryuqq.authhub.application.tenant.port.out.command.TenantCommandPort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantCommandManager - Tenant Command 관리자
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
public class TenantCommandManager {

    private final TenantCommandPort persistencePort;

    public TenantCommandManager(TenantCommandPort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * Tenant 영속화
     *
     * @param tenant 영속화할 Tenant
     * @return 영속화된 Tenant ID (String)
     */
    @Transactional
    public String persist(Tenant tenant) {
        return persistencePort.persist(tenant);
    }
}
