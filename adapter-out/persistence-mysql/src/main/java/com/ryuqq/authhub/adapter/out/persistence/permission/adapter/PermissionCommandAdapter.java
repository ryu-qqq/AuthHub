package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.application.permission.port.out.command.PermissionPersistencePort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * PermissionCommandAdapter - 권한 Command Adapter (CUD 전용)
 *
 * <p>PermissionPersistencePort 구현체로서 권한 저장 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>PermissionJpaRepository (1개) + PermissionJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 권한 저장 (생성/수정)
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
public class PermissionCommandAdapter implements PermissionPersistencePort {

    private final PermissionJpaRepository repository;
    private final PermissionJpaEntityMapper mapper;

    public PermissionCommandAdapter(
            PermissionJpaRepository repository, PermissionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 권한 저장 (생성/수정)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>기존 Entity 조회 (UUID로 조회)
     *   <li>기존 Entity 존재 시: 기존 ID 유지하며 업데이트
     *   <li>기존 Entity 없음 시: 신규 Entity 생성
     *   <li>Entity 저장 (JpaRepository)
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * <p><strong>UPDATE vs INSERT 판단 기준:</strong>
     *
     * <ul>
     *   <li>permissionId(UUID)로 기존 Entity 조회
     *   <li>기존 Entity 존재 → UPDATE (JPA internal ID 유지)
     *   <li>기존 Entity 없음 → INSERT (신규 Entity 생성)
     * </ul>
     *
     * @param permission 저장할 권한 도메인
     * @return 저장된 권한 도메인 (ID 할당됨)
     */
    @Override
    public Permission persist(Permission permission) {
        UUID permissionIdValue = permission.permissionIdValue();
        Optional<PermissionJpaEntity> existing = repository.findByPermissionId(permissionIdValue);

        PermissionJpaEntity entity;
        if (existing.isPresent()) {
            // UPDATE: 기존 Entity의 JPA internal ID 유지
            entity = mapper.updateEntity(existing.get(), permission);
        } else {
            // INSERT: 신규 Entity 생성
            entity = mapper.toEntity(permission);
        }

        PermissionJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
