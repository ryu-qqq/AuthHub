package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.application.tenant.port.out.command.TenantPersistencePort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import org.springframework.stereotype.Component;

/**
 * TenantCommandAdapter - 테넌트 Command Adapter (CUD 전용)
 *
 * <p>TenantPersistencePort 구현체로서 테넌트 저장 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>TenantJpaRepository (1개) + TenantJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 테넌트 저장 (생성/수정)
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
public class TenantCommandAdapter implements TenantPersistencePort {

    private final TenantJpaRepository repository;
    private final TenantJpaEntityMapper mapper;

    public TenantCommandAdapter(TenantJpaRepository repository, TenantJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 테넌트 저장 (생성/수정)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Domain → Entity 변환 (Mapper)
     *   <li>Entity 저장 (JpaRepository)
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param tenant 저장할 테넌트 도메인
     * @return 저장된 테넌트 도메인 (ID 할당됨)
     */
    @Override
    public Tenant persist(Tenant tenant) {
        TenantJpaEntity entity = mapper.toEntity(tenant);
        TenantJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
