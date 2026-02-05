package com.ryuqq.authhub.adapter.out.persistence.tenantservice.adapter;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.mapper.TenantServiceJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository.TenantServiceQueryDslRepository;
import com.ryuqq.authhub.application.tenantservice.port.out.query.TenantServiceQueryPort;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * TenantServiceQueryAdapter - 테넌트-서비스 Query Adapter (조회 전용)
 *
 * <p>TenantServiceQueryPort 구현체로서 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>TenantServiceQueryDslRepository (1개) + TenantServiceJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantServiceQueryAdapter implements TenantServiceQueryPort {

    private final TenantServiceQueryDslRepository repository;
    private final TenantServiceJpaEntityMapper mapper;

    public TenantServiceQueryAdapter(
            TenantServiceQueryDslRepository repository, TenantServiceJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<TenantService> findById(TenantServiceId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(TenantServiceId id) {
        return repository.existsById(id.value());
    }

    @Override
    public boolean existsByTenantIdAndServiceId(TenantId tenantId, ServiceId serviceId) {
        return repository.existsByTenantIdAndServiceId(tenantId.value(), serviceId.value());
    }

    @Override
    public Optional<TenantService> findByTenantIdAndServiceId(
            TenantId tenantId, ServiceId serviceId) {
        return repository
                .findByTenantIdAndServiceId(tenantId.value(), serviceId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<TenantService> findAllByCriteria(TenantServiceSearchCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(TenantServiceSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
