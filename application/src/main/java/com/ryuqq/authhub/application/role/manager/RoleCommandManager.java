package com.ryuqq.authhub.application.role.manager;

import com.ryuqq.authhub.application.role.port.out.command.RoleCommandPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoleCommandManager - Role Command 관리자
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
public class RoleCommandManager {

    private final RoleCommandPort persistencePort;

    public RoleCommandManager(RoleCommandPort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * Role 영속화
     *
     * @param role 영속화할 Role
     * @return 영속화된 Role ID (Long)
     */
    @Transactional
    public Long persist(Role role) {
        return persistencePort.persist(role);
    }
}
