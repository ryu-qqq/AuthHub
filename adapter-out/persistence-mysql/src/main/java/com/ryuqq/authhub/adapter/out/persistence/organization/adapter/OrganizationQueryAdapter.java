package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationQueryDslRepository;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * OrganizationQueryAdapter - Organization Query Adapter
 *
 * <p>OrganizationQueryPort 구현체입니다. 조회 작업을 담당합니다.
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
public class OrganizationQueryAdapter implements OrganizationQueryPort {

    private final OrganizationQueryDslRepository organizationQueryDslRepository;
    private final OrganizationJpaEntityMapper organizationJpaEntityMapper;

    public OrganizationQueryAdapter(
            OrganizationQueryDslRepository organizationQueryDslRepository,
            OrganizationJpaEntityMapper organizationJpaEntityMapper) {
        this.organizationQueryDslRepository = organizationQueryDslRepository;
        this.organizationJpaEntityMapper = organizationJpaEntityMapper;
    }

    /**
     * ID로 Organization 조회
     *
     * @param organizationId 조회할 Organization ID
     * @return Organization Domain 객체 (Optional)
     */
    @Override
    public Optional<Organization> findById(OrganizationId organizationId) {
        return organizationQueryDslRepository
                .findById(organizationId.value())
                .map(organizationJpaEntityMapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param organizationId 확인할 Organization ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(OrganizationId organizationId) {
        return organizationQueryDslRepository.existsById(organizationId.value());
    }

    /**
     * Tenant ID로 Organization 존재 여부 확인
     *
     * @param tenantId Tenant ID
     * @return 존재 여부
     */
    @Override
    public boolean existsByTenantId(TenantId tenantId) {
        return organizationQueryDslRepository.existsByTenantId(tenantId.value());
    }

    /**
     * Tenant ID로 활성 Organization 존재 여부 확인
     *
     * @param tenantId Tenant ID
     * @return 활성 Organization 존재 여부
     */
    @Override
    public boolean existsActiveByTenantId(TenantId tenantId) {
        return organizationQueryDslRepository.existsActiveByTenantId(tenantId.value());
    }
}
