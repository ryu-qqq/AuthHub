package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * RoleJpaEntityMapper - Role Domain ↔ Entity 변환
 *
 * <p>Domain 객체와 JPA Entity 간의 양방향 변환을 담당합니다.
 *
 * <p><strong>Permissions 처리:</strong>
 *
 * <ul>
 *   <li>Entity → Domain: permissions는 외부에서 별도 조회하여 전달
 *   <li>Domain → Entity: permissions는 별도 테이블에 저장 (RolePermissionJpaEntity)
 * </ul>
 *
 * <p><strong>Audit 정보:</strong> Role 도메인은 createdAt/updatedAt을 가지지 않으므로 시간 변환이 필요하지 않습니다.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class RoleJpaEntityMapper {

    /**
     * Domain → Entity 변환
     *
     * <p>permissions는 별도 테이블로 저장되므로 Entity에 포함되지 않습니다.
     *
     * @param role Role Domain 객체
     * @return RoleJpaEntity
     */
    public RoleJpaEntity toEntity(Role role) {
        return RoleJpaEntity.of(
                role.roleIdValue(),
                role.tenantIdValue(),
                role.nameValue(),
                role.getDescription(),
                role.isSystem());
    }

    /**
     * Entity → Domain 변환 (permissions 포함)
     *
     * <p>Domain.reconstitute() 메서드를 사용하여 영속화된 데이터를 복원합니다.
     *
     * @param entity RoleJpaEntity
     * @param permissions Permission 코드 Set (별도 조회 결과)
     * @return Role Domain 객체
     */
    public Role toDomain(RoleJpaEntity entity, Set<PermissionCode> permissions) {
        return Role.reconstitute(
                RoleId.of(entity.getId()),
                entity.getTenantId() != null ? TenantId.of(entity.getTenantId()) : null,
                RoleName.of(entity.getName()),
                entity.getDescription(),
                entity.isSystem(),
                permissions);
    }
}
