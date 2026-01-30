package com.ryuqq.authhub.adapter.out.persistence.permission.mapper;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import org.springframework.stereotype.Component;

/**
 * PermissionJpaEntityMapper - Entity ↔ Domain 변환 Mapper (Global Only)
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 Permission 간 변환을 담당합니다.
 *
 * <p><strong>Global Only 설계:</strong>
 *
 * <ul>
 *   <li>모든 Permission은 전체 시스템에서 공유됩니다
 *   <li>테넌트 관련 필드가 제거되었습니다
 * </ul>
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>Permission → PermissionJpaEntity (저장용)
 *   <li>PermissionJpaEntity → Permission (조회용)
 * </ul>
 *
 * <p><strong>시간 처리:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: Instant (UTC) - 변환 없이 직접 전달
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
     * Domain → Entity 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 Permission 저장
     *   <li>기존 Permission 수정 (Hibernate Dirty Checking)
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>permissionId: Domain.permissionIdValue() → Entity.permissionId (Long)
     *   <li>permissionKey: Domain.permissionKeyValue() → Entity.permissionKey
     *   <li>resource: Domain.resourceValue() → Entity.resource
     *   <li>action: Domain.actionValue() → Entity.action
     *   <li>description: Domain.descriptionValue() → Entity.description
     *   <li>type: Domain.getType() → Entity.type
     *   <li>createdAt: Instant → Instant (직접 전달)
     *   <li>updatedAt: Instant → Instant (직접 전달)
     *   <li>deletedAt: DeletionStatus.deletedAt() → Instant (직접 전달)
     * </ul>
     *
     * @param domain Permission 도메인
     * @return PermissionJpaEntity
     */
    public PermissionJpaEntity toEntity(Permission domain) {
        DeletionStatus deletionStatus = domain.getDeletionStatus();
        return PermissionJpaEntity.of(
                domain.permissionIdValue(),
                domain.permissionKeyValue(),
                domain.resourceValue(),
                domain.actionValue(),
                domain.descriptionValue(),
                domain.getType(),
                domain.createdAt(),
                domain.updatedAt(),
                deletionStatus.deletedAt());
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
     *   <li>permissionId: Entity.permissionId (Long) → PermissionId VO
     *   <li>permissionKey: Entity.permissionKey → String
     *   <li>resource: Entity.resource → String
     *   <li>action: Entity.action → String
     *   <li>description: Entity.description → String
     *   <li>type: Entity.type → PermissionType Enum
     *   <li>deletedAt: Entity.getDeletedAt() → DeletionStatus
     *   <li>createdAt: Instant → Instant (직접 전달)
     *   <li>updatedAt: Instant → Instant (직접 전달)
     * </ul>
     *
     * @param entity PermissionJpaEntity
     * @return Permission 도메인
     */
    public Permission toDomain(PermissionJpaEntity entity) {
        return Permission.reconstitute(
                PermissionId.of(entity.getPermissionId()),
                entity.getPermissionKey(),
                entity.getResource(),
                entity.getAction(),
                entity.getDescription(),
                entity.getType(),
                DeletionStatus.reconstitute(entity.isDeleted(), entity.getDeletedAt()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
