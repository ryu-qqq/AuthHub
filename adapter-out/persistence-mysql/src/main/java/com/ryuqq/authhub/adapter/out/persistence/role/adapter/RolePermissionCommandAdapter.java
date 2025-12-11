package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RolePermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RolePermissionJpaRepository;
import com.ryuqq.authhub.application.role.port.out.persistence.RolePermissionPersistencePort;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
import org.springframework.stereotype.Component;

/**
 * RolePermissionCommandAdapter - 역할 권한 Command Adapter
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Port 구현체
 *   <li>Mapper를 통한 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionCommandAdapter implements RolePermissionPersistencePort {

    private final RolePermissionJpaRepository repository;
    private final RolePermissionJpaEntityMapper mapper;

    public RolePermissionCommandAdapter(
            RolePermissionJpaRepository repository, RolePermissionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public RolePermission save(RolePermission rolePermission) {
        RolePermissionJpaEntity entity = mapper.toEntity(rolePermission);
        RolePermissionJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(RoleId roleId, PermissionId permissionId) {
        repository.deleteByRoleIdAndPermissionId(roleId.value(), permissionId.value());
    }
}
