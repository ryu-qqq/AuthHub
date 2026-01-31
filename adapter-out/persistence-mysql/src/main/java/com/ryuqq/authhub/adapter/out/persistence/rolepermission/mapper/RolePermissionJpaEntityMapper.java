package com.ryuqq.authhub.adapter.out.persistence.rolepermission.mapper;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.id.RolePermissionId;
import org.springframework.stereotype.Component;

/**
 * RolePermissionJpaEntityMapper - 역할-권한 관계 Entity ↔ Domain 변환
 *
 * <p>JPA Entity와 Domain 객체 간의 양방향 변환을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>toEntity(): Domain → Entity (저장용)
 *   <li>toDomain(): Entity → Domain (조회용)
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>비즈니스 로직 금지 (단순 변환만)
 *   <li>null 처리는 Domain에서 담당
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionJpaEntityMapper {

    /**
     * Domain → Entity 변환 (저장용)
     *
     * @param domain RolePermission Domain
     * @return RolePermissionJpaEntity
     */
    public RolePermissionJpaEntity toEntity(RolePermission domain) {
        if (domain.isNew()) {
            return RolePermissionJpaEntity.create(
                    domain.roleIdValue(), domain.permissionIdValue(), domain.createdAt());
        }
        return RolePermissionJpaEntity.of(
                domain.rolePermissionIdValue(),
                domain.roleIdValue(),
                domain.permissionIdValue(),
                domain.createdAt());
    }

    /**
     * Entity → Domain 변환 (조회용)
     *
     * @param entity RolePermissionJpaEntity
     * @return RolePermission Domain
     */
    public RolePermission toDomain(RolePermissionJpaEntity entity) {
        return RolePermission.reconstitute(
                RolePermissionId.of(entity.getRolePermissionId()),
                RoleId.of(entity.getRoleId()),
                PermissionId.of(entity.getPermissionId()),
                entity.getCreatedAt());
    }
}
