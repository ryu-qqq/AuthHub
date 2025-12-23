package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionUsageJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionUsageJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionUsageJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionUsageQueryDslRepository;
import com.ryuqq.authhub.application.permission.port.out.command.PermissionUsagePersistencePort;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * PermissionUsageCommandAdapter - 권한 사용 이력 Command Adapter (CUD 전용)
 *
 * <p>PermissionUsagePersistencePort 구현체로서 사용 이력 저장 작업을 담당합니다.
 *
 * <p><strong>UPSERT 지원:</strong>
 *
 * <ul>
 *   <li>동일 permissionKey + serviceName 조합이 존재하면 업데이트
 *   <li>존재하지 않으면 신규 생성
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
public class PermissionUsageCommandAdapter implements PermissionUsagePersistencePort {

    private final PermissionUsageJpaRepository repository;
    private final PermissionUsageQueryDslRepository queryRepository;
    private final PermissionUsageJpaEntityMapper mapper;
    private final UuidHolder uuidHolder;

    public PermissionUsageCommandAdapter(
            PermissionUsageJpaRepository repository,
            PermissionUsageQueryDslRepository queryRepository,
            PermissionUsageJpaEntityMapper mapper,
            UuidHolder uuidHolder) {
        this.repository = repository;
        this.queryRepository = queryRepository;
        this.mapper = mapper;
        this.uuidHolder = uuidHolder;
    }

    /**
     * 사용 이력 저장 (UPSERT)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>기존 이력 조회 (permissionKey + serviceName)
     *   <li>존재하면 업데이트, 없으면 신규 생성
     *   <li>Entity 저장
     *   <li>Entity → Domain 변환
     * </ol>
     *
     * @param usage 저장할 사용 이력 도메인
     * @return 저장된 사용 이력 도메인 (ID 할당됨)
     */
    @Override
    public PermissionUsage persist(PermissionUsage usage) {
        PermissionUsageJpaEntity entity;

        if (usage.isNew()) {
            // 신규 생성 또는 UPSERT - 기존 이력 확인
            Optional<PermissionUsageJpaEntity> existing =
                    queryRepository.findByKeyAndService(
                            usage.permissionKeyValue(), usage.serviceNameValue());

            if (existing.isPresent()) {
                // 기존 이력 업데이트
                entity = mapper.toEntityForUpdate(usage, existing.get().getId());
            } else {
                // 신규 생성 - UUID 발급
                PermissionUsage usageWithId =
                        PermissionUsage.reconstitute(
                                com.ryuqq.authhub.domain.permission.identifier.PermissionUsageId.of(
                                        uuidHolder.random()),
                                usage.getPermissionKey(),
                                usage.getServiceName(),
                                usage.getLocations(),
                                usage.getLastScannedAt(),
                                usage.createdAt(),
                                usage.updatedAt());
                entity = mapper.toEntity(usageWithId);
            }
        } else {
            // ID가 있는 업데이트 케이스
            Optional<PermissionUsageJpaEntity> existing =
                    queryRepository.findByKeyAndService(
                            usage.permissionKeyValue(), usage.serviceNameValue());

            entity =
                    existing.map(e -> mapper.toEntityForUpdate(usage, e.getId()))
                            .orElseGet(() -> mapper.toEntity(usage));
        }

        PermissionUsageJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
