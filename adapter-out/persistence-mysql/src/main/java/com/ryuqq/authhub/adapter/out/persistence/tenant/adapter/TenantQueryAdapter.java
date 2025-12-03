package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantQueryDslRepository;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * TenantQueryAdapter - Tenant Query Adapter
 *
 * <p>TenantQueryPort 구현체입니다. 조회 작업을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지
 *   <li>QueryDslRepository에 조회 로직 위임
 *   <li>Entity → Domain 변환은 Mapper 사용
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class TenantQueryAdapter implements TenantQueryPort {

    private final TenantQueryDslRepository tenantQueryDslRepository;
    private final TenantJpaEntityMapper tenantJpaEntityMapper;

    public TenantQueryAdapter(
            TenantQueryDslRepository tenantQueryDslRepository,
            TenantJpaEntityMapper tenantJpaEntityMapper) {
        this.tenantQueryDslRepository = tenantQueryDslRepository;
        this.tenantJpaEntityMapper = tenantJpaEntityMapper;
    }

    /**
     * ID로 Tenant 조회
     *
     * @param tenantId 조회할 Tenant ID
     * @return Tenant Domain 객체 (Optional)
     */
    @Override
    public Optional<Tenant> findById(TenantId tenantId) {
        return tenantQueryDslRepository
                .findById(tenantId.value())
                .map(tenantJpaEntityMapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param tenantId 확인할 Tenant ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(TenantId tenantId) {
        return tenantQueryDslRepository.existsById(tenantId.value());
    }
}
