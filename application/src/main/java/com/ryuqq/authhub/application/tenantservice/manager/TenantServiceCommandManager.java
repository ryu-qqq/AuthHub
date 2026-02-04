package com.ryuqq.authhub.application.tenantservice.manager;

import com.ryuqq.authhub.application.tenantservice.port.out.command.TenantServiceCommandPort;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantServiceCommandManager - TenantService Command 관리자
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
public class TenantServiceCommandManager {

    private final TenantServiceCommandPort commandPort;

    public TenantServiceCommandManager(TenantServiceCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * TenantService 영속화
     *
     * @param tenantService 영속화할 TenantService
     * @return 영속화된 TenantService ID (Long)
     */
    @Transactional
    public Long persist(TenantService tenantService) {
        return commandPort.persist(tenantService);
    }
}
