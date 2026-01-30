package com.ryuqq.authhub.adapter.out.persistence.rolepermission.adapter;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.mapper.RolePermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionJpaRepository;
import com.ryuqq.authhub.application.rolepermission.port.out.command.RolePermissionCommandPort;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RolePermissionCommandAdapter - 역할-권한 관계 Command Adapter
 *
 * <p>RolePermissionPersistencePort 구현체로서 영속화 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>RolePermissionJpaRepository (1개) + RolePermissionJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 관계 저장
 *   <li>persistAll() - 관계 다건 저장
 *   <li>delete() - 관계 삭제
 *   <li>deleteAllByRoleId() - 역할의 모든 관계 삭제 (Cascade)
 *   <li>deleteAll() - 관계 다건 삭제
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionCommandAdapter implements RolePermissionCommandPort {

    private final RolePermissionJpaRepository repository;
    private final RolePermissionJpaEntityMapper mapper;

    public RolePermissionCommandAdapter(
            RolePermissionJpaRepository repository, RolePermissionJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 역할-권한 관계 저장
     *
     * @param rolePermission 저장할 역할-권한 관계
     * @return 저장된 역할-권한 관계
     */
    @Override
    public RolePermission persist(RolePermission rolePermission) {
        RolePermissionJpaEntity entity = mapper.toEntity(rolePermission);
        RolePermissionJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * 역할-권한 관계 다건 저장
     *
     * @param rolePermissions 저장할 역할-권한 관계 목록
     * @return 저장된 역할-권한 관계 목록
     */
    @Override
    public List<RolePermission> persistAll(List<RolePermission> rolePermissions) {
        List<RolePermissionJpaEntity> entities =
                rolePermissions.stream().map(mapper::toEntity).toList();
        List<RolePermissionJpaEntity> savedEntities = repository.saveAll(entities);
        return savedEntities.stream().map(mapper::toDomain).toList();
    }

    /**
     * 역할-권한 관계 삭제
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     */
    @Override
    public void delete(RoleId roleId, PermissionId permissionId) {
        repository.deleteByRoleIdAndPermissionId(roleId.value(), permissionId.value());
    }

    /**
     * 역할의 모든 권한 관계 삭제 (Role 삭제 시 Cascade)
     *
     * @param roleId 역할 ID
     */
    @Override
    public void deleteAllByRoleId(RoleId roleId) {
        repository.deleteAllByRoleId(roleId.value());
    }

    /**
     * 역할-권한 관계 다건 삭제
     *
     * @param roleId 역할 ID
     * @param permissionIds 삭제할 권한 ID 목록
     */
    @Override
    public void deleteAll(RoleId roleId, List<PermissionId> permissionIds) {
        List<Long> ids = permissionIds.stream().map(PermissionId::value).toList();
        repository.deleteAllByRoleIdAndPermissionIdIn(roleId.value(), ids);
    }
}
