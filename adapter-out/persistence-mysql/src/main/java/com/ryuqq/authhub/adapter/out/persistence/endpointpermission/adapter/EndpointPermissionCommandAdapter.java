package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity.EndpointPermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.mapper.EndpointPermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.repository.EndpointPermissionJpaRepository;
import com.ryuqq.authhub.application.endpointpermission.port.out.command.EndpointPermissionPersistencePort;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import org.springframework.stereotype.Component;

/**
 * EndpointPermissionCommandAdapter - 엔드포인트 권한 Command Adapter (CUD 전용)
 *
 * <p>EndpointPermissionPersistencePort 구현체로서 엔드포인트 권한 저장 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>EndpointPermissionJpaRepository (1개) + EndpointPermissionJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 엔드포인트 권한 저장 (생성/수정)
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
public class EndpointPermissionCommandAdapter implements EndpointPermissionPersistencePort {

    private final EndpointPermissionJpaRepository repository;
    private final EndpointPermissionJpaEntityMapper mapper;

    public EndpointPermissionCommandAdapter(
            EndpointPermissionJpaRepository repository, EndpointPermissionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 엔드포인트 권한 저장 (생성/수정)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Domain -> Entity 변환 (Mapper)
     *   <li>Entity 저장 (JpaRepository)
     *   <li>Entity -> Domain 변환 (Mapper)
     * </ol>
     *
     * @param endpointPermission 저장할 엔드포인트 권한 도메인
     * @return 저장된 엔드포인트 권한 도메인 (ID 할당됨)
     */
    @Override
    public EndpointPermission persist(EndpointPermission endpointPermission) {
        EndpointPermissionJpaEntity entity = mapper.toEntity(endpointPermission);
        EndpointPermissionJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
