package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.application.role.port.out.command.RoleCommandPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import org.springframework.stereotype.Component;

/**
 * RoleCommandAdapter - 역할 Command Adapter (CUD 전용)
 *
 * <p>RolePersistencePort 구현체로서 역할 저장 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>RoleJpaRepository (1개) + RoleJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 역할 저장 (생성/수정)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Hibernate Dirty Checking 활용 (존재 여부 확인 불필요)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleCommandAdapter implements RoleCommandPort {

    private final RoleJpaRepository repository;
    private final RoleJpaEntityMapper mapper;

    public RoleCommandAdapter(RoleJpaRepository repository, RoleJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 역할 저장 (생성/수정)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Domain → Entity 변환 (Mapper)
     *   <li>Entity 저장 (JpaRepository)
     *   <li>저장된 ID 반환 (Long)
     * </ol>
     *
     * <p><strong>Hibernate Dirty Checking:</strong>
     *
     * <ul>
     *   <li>같은 ID의 Entity가 이미 존재하면 UPDATE
     *   <li>새로운 ID면 INSERT
     *   <li>Hibernate 구현체가 자동으로 판단
     * </ul>
     *
     * @param role 저장할 역할 도메인
     * @return 저장된 역할 ID (Long)
     */
    @Override
    public Long persist(Role role) {
        RoleJpaEntity entity = mapper.toEntity(role);
        RoleJpaEntity savedEntity = repository.save(entity);
        return savedEntity.getRoleId();
    }
}
