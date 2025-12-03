package com.ryuqq.authhub.application.tenant.manager;

import com.ryuqq.authhub.application.tenant.port.out.command.TenantPersistencePort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantManager - Tenant 트랜잭션 관리 컴포넌트
 *
 * <p>Tenant 영속화를 위한 트랜잭션 경계를 관리합니다.
 *
 * <p><strong>역할:</strong>
 *
 * <ul>
 *   <li>트랜잭션 경계 관리 (@Transactional)
 *   <li>Domain Aggregate 영속화
 *   <li>Port를 통한 인프라 접근
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>비즈니스 로직 금지 (순수 영속화만)
 *   <li>@Transactional 내 외부 API 호출 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantManager {

    private final TenantPersistencePort tenantPersistencePort;

    public TenantManager(TenantPersistencePort tenantPersistencePort) {
        this.tenantPersistencePort = tenantPersistencePort;
    }

    /**
     * Tenant 영속화 (INSERT 또는 UPDATE)
     *
     * @param tenant Tenant Domain Aggregate
     * @return 영속화된 Tenant의 ID
     */
    @Transactional
    public TenantId persist(Tenant tenant) {
        return tenantPersistencePort.persist(tenant);
    }
}
