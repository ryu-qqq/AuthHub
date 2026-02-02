package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.mapper;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointJpaEntityMapper - Domain ⇄ Entity 변환 Mapper
 *
 * <p>Domain Aggregate와 JPA Entity 간 양방향 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>toEntity() - Domain → Entity
 *   <li>toDomain() - Entity → Domain (reconstitute)
 *   <li>비즈니스 로직 금지
 *   <li>null 체크 필수
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointJpaEntityMapper {

    /**
     * Domain → Entity 변환
     *
     * @param domain PermissionEndpoint Domain
     * @return PermissionEndpointJpaEntity
     */
    public PermissionEndpointJpaEntity toEntity(PermissionEndpoint domain) {
        return PermissionEndpointJpaEntity.of(
                domain.permissionEndpointIdValue(),
                domain.permissionIdValue(),
                domain.serviceNameValue(),
                domain.urlPatternValue(),
                domain.getHttpMethod(),
                domain.getDescription(),
                domain.isPublicEndpoint(),
                domain.createdAt(),
                domain.updatedAt(),
                extractDeletedAt(domain.getDeletionStatus()));
    }

    /**
     * Entity → Domain 변환 (reconstitute)
     *
     * @param entity PermissionEndpointJpaEntity
     * @return PermissionEndpoint Domain
     */
    public PermissionEndpoint toDomain(PermissionEndpointJpaEntity entity) {
        return PermissionEndpoint.reconstitute(
                entity.getPermissionEndpointId(),
                entity.getPermissionId(),
                entity.getServiceName(),
                entity.getUrlPattern(),
                entity.getHttpMethod(),
                entity.getDescription(),
                entity.isPublic(),
                parseDeletionStatus(entity.getDeletedAt()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    // ===== Private Helper Methods =====

    private DeletionStatus parseDeletionStatus(java.time.Instant deletedAt) {
        return deletedAt != null ? DeletionStatus.deletedAt(deletedAt) : DeletionStatus.active();
    }

    private java.time.Instant extractDeletedAt(DeletionStatus deletionStatus) {
        return deletionStatus != null && deletionStatus.isDeleted()
                ? deletionStatus.deletedAt()
                : null;
    }
}
