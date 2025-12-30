package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleDescription;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

/**
 * RoleJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 Role 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>Role → RoleJpaEntity (저장용)
 *   <li>RoleJpaEntity → Role (조회용)
 * </ul>
 *
 * <p><strong>시간 변환:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: LocalDateTime (UTC 기준)
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong>
 *
 * <ul>
 *   <li>Adapter Layer의 책임
 *   <li>Domain과 Infrastructure 기술 분리
 *   <li>Domain은 JPA 의존성 없음
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleJpaEntityMapper {

    /**
     * Domain → Entity 변환 (신규 생성용)
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 Role 저장 (ID가 null)
     * </ul>
     *
     * @param domain Role 도메인
     * @return RoleJpaEntity (JPA internal ID = null)
     */
    public RoleJpaEntity toEntity(Role domain) {
        return RoleJpaEntity.of(
                domain.roleIdValue(),
                domain.tenantIdValue(),
                domain.nameValue(),
                domain.descriptionValue(),
                domain.getScope(),
                domain.getType(),
                domain.isDeleted(),
                toLocalDateTime(domain.createdAt()),
                toLocalDateTime(domain.updatedAt()));
    }

    /**
     * 기존 Entity 업데이트 (UPDATE용)
     *
     * <p>기존 Entity의 JPA internal ID를 유지하면서 Domain 값으로 업데이트합니다.
     *
     * @param existing 기존 JPA Entity (ID 유지용)
     * @param domain 업데이트할 Role 도메인
     * @return RoleJpaEntity (기존 JPA internal ID 유지)
     */
    public RoleJpaEntity updateEntity(RoleJpaEntity existing, Role domain) {
        return RoleJpaEntity.of(
                domain.roleIdValue(),
                domain.tenantIdValue(),
                domain.nameValue(),
                domain.descriptionValue(),
                domain.getScope(),
                domain.getType(),
                domain.isDeleted(),
                existing.getCreatedAt(),
                toLocalDateTime(domain.updatedAt()));
    }

    /**
     * Entity → Domain 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>데이터베이스에서 조회한 Entity를 Domain으로 변환
     *   <li>Application Layer로 전달
     * </ul>
     *
     * @param entity RoleJpaEntity
     * @return Role 도메인
     */
    public Role toDomain(RoleJpaEntity entity) {
        TenantId tenantId = entity.getTenantId() != null ? TenantId.of(entity.getTenantId()) : null;
        RoleDescription description =
                entity.getDescription() != null
                        ? RoleDescription.of(entity.getDescription())
                        : RoleDescription.empty();

        return Role.reconstitute(
                RoleId.of(entity.getRoleId()),
                tenantId,
                RoleName.of(entity.getName()),
                description,
                entity.getScope(),
                entity.getType(),
                entity.isDeleted(),
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    /** Instant → LocalDateTime 변환 (UTC 기준) */
    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    /** LocalDateTime → Instant 변환 (UTC 기준) */
    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}
