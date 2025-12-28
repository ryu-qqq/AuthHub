package com.ryuqq.authhub.adapter.out.persistence.permission.mapper;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.permission.vo.Action;
import com.ryuqq.authhub.domain.permission.vo.PermissionDescription;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

/**
 * PermissionJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 Permission 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>Permission → PermissionJpaEntity (저장용)
 *   <li>PermissionJpaEntity → Permission (조회용)
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
public class PermissionJpaEntityMapper {

    /**
     * Domain → Entity 변환 (신규 생성용)
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 Permission 저장 (ID가 null)
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>permissionId: Domain.permissionIdValue() → Entity.permissionId (UUID)
     *   <li>permissionKey: Domain.keyValue() → Entity.permissionKey
     *   <li>resource: Domain.resourceValue() → Entity.resource
     *   <li>action: Domain.actionValue() → Entity.action
     *   <li>description: Domain.descriptionValue() → Entity.description
     *   <li>type: Domain.getType() → Entity.type
     *   <li>deleted: Domain.isDeleted() → Entity.deleted
     *   <li>createdAt: Instant → LocalDateTime (UTC)
     *   <li>updatedAt: Instant → LocalDateTime (UTC)
     * </ul>
     *
     * @param domain Permission 도메인
     * @return PermissionJpaEntity (JPA internal ID = null)
     */
    public PermissionJpaEntity toEntity(Permission domain) {
        return PermissionJpaEntity.of(
                null,
                domain.permissionIdValue(),
                domain.keyValue(),
                domain.resourceValue(),
                domain.actionValue(),
                domain.descriptionValue(),
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
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>기존 Permission 수정 (ID가 있음)
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>id: 기존 Entity ID 유지 (JPA internal ID)
     *   <li>permissionId: Domain.permissionIdValue() → Entity.permissionId (UUID)
     *   <li>permissionKey: Domain.keyValue() → Entity.permissionKey
     *   <li>resource: Domain.resourceValue() → Entity.resource
     *   <li>action: Domain.actionValue() → Entity.action
     *   <li>description: Domain.descriptionValue() → Entity.description
     *   <li>type: Domain.getType() → Entity.type
     *   <li>deleted: Domain.isDeleted() → Entity.deleted
     *   <li>createdAt: Instant → LocalDateTime (UTC)
     *   <li>updatedAt: Instant → LocalDateTime (UTC)
     * </ul>
     *
     * @param existing 기존 JPA Entity (ID 유지용)
     * @param domain 업데이트할 Permission 도메인
     * @return PermissionJpaEntity (기존 JPA internal ID 유지)
     */
    public PermissionJpaEntity updateEntity(PermissionJpaEntity existing, Permission domain) {
        return PermissionJpaEntity.of(
                existing.getId(),
                domain.permissionIdValue(),
                domain.keyValue(),
                domain.resourceValue(),
                domain.actionValue(),
                domain.descriptionValue(),
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
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>permissionId: Entity.permissionId → PermissionId VO
     *   <li>key: Entity.resource + Entity.action → PermissionKey VO
     *   <li>description: Entity.description → PermissionDescription VO
     *   <li>type: Entity.type → PermissionType Enum
     *   <li>deleted: Entity.deleted → boolean
     *   <li>createdAt: LocalDateTime → Instant (UTC)
     *   <li>updatedAt: LocalDateTime → Instant (UTC)
     * </ul>
     *
     * @param entity PermissionJpaEntity
     * @return Permission 도메인
     */
    public Permission toDomain(PermissionJpaEntity entity) {
        Resource resource = Resource.of(entity.getResource());
        Action action = Action.of(entity.getAction());
        PermissionKey key = PermissionKey.of(resource, action);
        PermissionDescription description =
                entity.getDescription() != null
                        ? PermissionDescription.of(entity.getDescription())
                        : PermissionDescription.empty();

        return Permission.reconstitute(
                PermissionId.of(entity.getPermissionId()),
                key,
                description,
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
