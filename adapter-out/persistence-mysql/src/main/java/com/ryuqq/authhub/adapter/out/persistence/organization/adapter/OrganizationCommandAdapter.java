package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.application.organization.port.out.command.OrganizationPersistencePort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import org.springframework.stereotype.Component;

/**
 * OrganizationCommandAdapter - Organization Command Adapter
 *
 * <p>OrganizationPersistencePort 구현체입니다. 신규 저장 및 수정 작업을 담당합니다.
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
public class OrganizationCommandAdapter implements OrganizationPersistencePort {

    private final OrganizationJpaRepository organizationJpaRepository;
    private final OrganizationJpaEntityMapper organizationJpaEntityMapper;

    public OrganizationCommandAdapter(
            OrganizationJpaRepository organizationJpaRepository,
            OrganizationJpaEntityMapper organizationJpaEntityMapper) {
        this.organizationJpaRepository = organizationJpaRepository;
        this.organizationJpaEntityMapper = organizationJpaEntityMapper;
    }

    /**
     * Organization 저장 (신규 및 수정)
     *
     * <p>JPA의 save() 메서드는 ID 유무에 따라 INSERT/UPDATE를 자동 결정합니다.
     *
     * @param organization 저장할 Organization Domain 객체
     * @return 저장된 Organization의 ID
     */
    @Override
    public OrganizationId persist(Organization organization) {
        OrganizationJpaEntity entity = organizationJpaEntityMapper.toEntity(organization);
        OrganizationJpaEntity savedEntity = organizationJpaRepository.save(entity);
        return OrganizationId.of(savedEntity.getId());
    }
}
