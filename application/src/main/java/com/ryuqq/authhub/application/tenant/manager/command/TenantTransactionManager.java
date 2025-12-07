package com.ryuqq.authhub.application.tenant.manager.command;

import com.ryuqq.authhub.application.tenant.port.out.command.TenantPersistencePort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantTransactionManager - 단일 Port 트랜잭션 관리
 *
 * <p>단일 PersistencePort에 대한 트랜잭션을 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional} 클래스 레벨
 *   <li>{@code persist()} 메서드만 제공 (순수 위임)
 *   <li>단일 PersistencePort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional
public class TenantTransactionManager {

    private final TenantPersistencePort persistencePort;

    public TenantTransactionManager(TenantPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * Tenant 영속화
     *
     * @param tenant Tenant Aggregate
     * @return 영속화된 Tenant (ID 할당됨)
     */
    public Tenant persist(Tenant tenant) {
        return persistencePort.persist(tenant);
    }
}
