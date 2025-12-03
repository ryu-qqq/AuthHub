package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.application.tenant.port.out.command.TenantPersistencePort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Component;

/**
 * TenantCommandAdapter - Tenant Command Adapter
 *
 * <p>TenantPersistencePort 구현체입니다. 신규 저장 및 수정 작업을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지 (UseCase에서 관리)
 *   <li>persist() 메서드만 제공
 *   <li>비즈니스 로직 포함 금지
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class TenantCommandAdapter implements TenantPersistencePort {

    private final TenantJpaRepository tenantJpaRepository;
    private final TenantJpaEntityMapper tenantJpaEntityMapper;

    public TenantCommandAdapter(
            TenantJpaRepository tenantJpaRepository, TenantJpaEntityMapper tenantJpaEntityMapper) {
        this.tenantJpaRepository = tenantJpaRepository;
        this.tenantJpaEntityMapper = tenantJpaEntityMapper;
    }

    /**
     * Tenant 저장 (신규 및 수정)
     *
     * <p>JPA의 save() 메서드는 ID 유무에 따라 INSERT/UPDATE를 자동 결정합니다.
     *
     * @param tenant 저장할 Tenant Domain 객체
     * @return 저장된 Tenant의 ID
     */
    @Override
    public TenantId persist(Tenant tenant) {
        TenantJpaEntity entity = tenantJpaEntityMapper.toEntity(tenant);
        TenantJpaEntity savedEntity = tenantJpaRepository.save(entity);
        return TenantId.of(savedEntity.getId());
    }
}
