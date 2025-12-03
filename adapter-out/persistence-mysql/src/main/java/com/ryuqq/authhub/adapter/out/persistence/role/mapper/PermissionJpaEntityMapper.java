package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.PermissionJpaEntity;
import com.ryuqq.authhub.domain.role.aggregate.Permission;
import com.ryuqq.authhub.domain.role.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import org.springframework.stereotype.Component;

/**
 * PermissionJpaEntityMapper - Permission Domain ↔ Entity 변환
 *
 * <p>Domain 객체와 JPA Entity 간의 양방향 변환을 담당합니다.
 *
 * <p><strong>Audit 정보:</strong> Permission 도메인은 createdAt/updatedAt을 가지지 않으므로 시간 변환이 필요하지 않습니다.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class PermissionJpaEntityMapper {

    /**
     * Domain → Entity 변환
     *
     * @param permission Permission Domain 객체
     * @return PermissionJpaEntity
     */
    public PermissionJpaEntity toEntity(Permission permission) {
        return PermissionJpaEntity.of(
                permission.permissionIdValue(),
                permission.codeValue(),
                permission.getDescription());
    }

    /**
     * Entity → Domain 변환
     *
     * <p>Domain.reconstitute() 메서드를 사용하여 영속화된 데이터를 복원합니다.
     *
     * @param entity PermissionJpaEntity
     * @return Permission Domain 객체
     */
    public Permission toDomain(PermissionJpaEntity entity) {
        return Permission.reconstitute(
                PermissionId.of(entity.getId()),
                PermissionCode.of(entity.getCode()),
                entity.getDescription());
    }

    /**
     * Entity → PermissionCode 변환
     *
     * <p>Role의 permissions Set을 구성할 때 사용
     *
     * @param entity PermissionJpaEntity
     * @return PermissionCode
     */
    public PermissionCode toPermissionCode(PermissionJpaEntity entity) {
        return PermissionCode.of(entity.getCode());
    }
}
