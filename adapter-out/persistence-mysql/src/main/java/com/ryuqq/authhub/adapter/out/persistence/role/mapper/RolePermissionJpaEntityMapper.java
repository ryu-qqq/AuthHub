package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import org.springframework.stereotype.Component;

/**
 * RolePermissionJpaEntityMapper - 역할 권한 Entity/Domain 변환기
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain ↔ Entity 양방향 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>매핑 테이블도 UUID PK 사용
 *   <li>UuidHolder를 통해 UUIDv7 생성
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionJpaEntityMapper {

    private final UuidHolder uuidHolder;

    public RolePermissionJpaEntityMapper(UuidHolder uuidHolder) {
        this.uuidHolder = uuidHolder;
    }

    /**
     * Domain 객체를 JPA Entity로 변환합니다.
     *
     * @param rolePermission 역할 권한 도메인 객체
     * @return JPA Entity
     */
    public RolePermissionJpaEntity toEntity(RolePermission rolePermission) {
        return RolePermissionJpaEntity.of(
                uuidHolder.random(),
                rolePermission.roleIdValue(),
                rolePermission.permissionIdValue(),
                rolePermission.getGrantedAt());
    }

    /**
     * JPA Entity를 Domain 객체로 변환합니다.
     *
     * @param entity JPA Entity
     * @return 역할 권한 도메인 객체
     */
    public RolePermission toDomain(RolePermissionJpaEntity entity) {
        return RolePermission.reconstitute(
                RoleId.of(entity.getRoleId()),
                PermissionId.of(entity.getPermissionId()),
                entity.getGrantedAt());
    }
}
