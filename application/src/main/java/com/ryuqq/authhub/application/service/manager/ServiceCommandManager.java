package com.ryuqq.authhub.application.service.manager;

import com.ryuqq.authhub.application.service.port.out.command.ServiceCommandPort;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceCommandManager - Service Command 관리자
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
public class ServiceCommandManager {

    private final ServiceCommandPort commandPort;

    public ServiceCommandManager(ServiceCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * Service 영속화
     *
     * @param service 영속화할 Service
     * @return 영속화된 Service ID (Long)
     */
    @Transactional
    public Long persist(Service service) {
        return commandPort.persist(service);
    }
}
