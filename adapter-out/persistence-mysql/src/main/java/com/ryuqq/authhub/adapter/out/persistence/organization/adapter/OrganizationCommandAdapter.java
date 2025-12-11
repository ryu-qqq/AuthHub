package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.application.organization.port.out.command.OrganizationPersistencePort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import org.springframework.stereotype.Component;

/**
 * OrganizationCommandAdapter - 조직 Command Adapter (CUD 전용)
 *
 * <p>OrganizationPersistencePort 구현체로서 조직 저장 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>OrganizationJpaRepository (1개) + OrganizationJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 조직 저장 (생성/수정)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationCommandAdapter implements OrganizationPersistencePort {

    private final OrganizationJpaRepository repository;
    private final OrganizationJpaEntityMapper mapper;

    public OrganizationCommandAdapter(
            OrganizationJpaRepository repository, OrganizationJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 조직 저장 (생성/수정)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Domain → Entity 변환 (Mapper)
     *   <li>Entity 저장 (JpaRepository)
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param organization 저장할 조직 도메인
     * @return 저장된 조직 도메인 (ID 할당됨)
     */
    @Override
    public Organization persist(Organization organization) {
        OrganizationJpaEntity entity = mapper.toEntity(organization);
        OrganizationJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
