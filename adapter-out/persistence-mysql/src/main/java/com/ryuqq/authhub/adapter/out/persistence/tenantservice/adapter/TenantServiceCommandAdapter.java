package com.ryuqq.authhub.adapter.out.persistence.tenantservice.adapter;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.mapper.TenantServiceJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository.TenantServiceJpaRepository;
import com.ryuqq.authhub.application.tenantservice.port.out.command.TenantServiceCommandPort;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import org.springframework.stereotype.Component;

/**
 * TenantServiceCommandAdapter - 테넌트-서비스 Command Adapter (CUD 전용)
 *
 * <p>TenantServiceCommandPort 구현체로서 저장 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>TenantServiceJpaRepository (1개) + TenantServiceJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceCommandAdapter implements TenantServiceCommandPort {

    private final TenantServiceJpaRepository repository;
    private final TenantServiceJpaEntityMapper mapper;

    public TenantServiceCommandAdapter(
            TenantServiceJpaRepository repository, TenantServiceJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(TenantService tenantService) {
        TenantServiceJpaEntity entity = mapper.toEntity(tenantService);
        TenantServiceJpaEntity savedEntity = repository.save(entity);
        return savedEntity.getId();
    }
}
