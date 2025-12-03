package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.PermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.PermissionQueryDslRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RolePermissionQueryDslRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleQueryDslRepository;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.UserRoleQueryDslRepository;
import com.ryuqq.authhub.application.role.port.out.query.RoleQueryPort;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.PermissionCode;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * RoleQueryAdapter - Role Query Adapter
 *
 * <p>RoleQueryPort 구현체입니다. 조회 작업을 담당합니다.
 *
 * <p><strong>Long FK 전략:</strong>
 *
 * <ul>
 *   <li>Role → Permission 관계는 JOIN 없이 별도 쿼리로 분리
 *   <li>User → Role 관계도 별도 쿼리로 분리
 *   <li>N+1 방지를 위해 ID 목록 기반 조회 사용
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지
 *   <li>QueryDslRepository에 조회 로직 위임
 *   <li>Entity → Domain 변환은 Mapper 사용
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class RoleQueryAdapter implements RoleQueryPort {

    private final RoleQueryDslRepository roleQueryDslRepository;
    private final UserRoleQueryDslRepository userRoleQueryDslRepository;
    private final RolePermissionQueryDslRepository rolePermissionQueryDslRepository;
    private final PermissionQueryDslRepository permissionQueryDslRepository;
    private final RoleJpaEntityMapper roleJpaEntityMapper;
    private final PermissionJpaEntityMapper permissionJpaEntityMapper;

    public RoleQueryAdapter(
            RoleQueryDslRepository roleQueryDslRepository,
            UserRoleQueryDslRepository userRoleQueryDslRepository,
            RolePermissionQueryDslRepository rolePermissionQueryDslRepository,
            PermissionQueryDslRepository permissionQueryDslRepository,
            RoleJpaEntityMapper roleJpaEntityMapper,
            PermissionJpaEntityMapper permissionJpaEntityMapper) {
        this.roleQueryDslRepository = roleQueryDslRepository;
        this.userRoleQueryDslRepository = userRoleQueryDslRepository;
        this.rolePermissionQueryDslRepository = rolePermissionQueryDslRepository;
        this.permissionQueryDslRepository = permissionQueryDslRepository;
        this.roleJpaEntityMapper = roleJpaEntityMapper;
        this.permissionJpaEntityMapper = permissionJpaEntityMapper;
    }

    /**
     * RoleId로 Role 조회
     *
     * <p>Long FK 전략에 따라 Permission은 별도 쿼리로 조회:
     *
     * <ol>
     *   <li>Role 조회
     *   <li>role_permissions 테이블에서 permission_id 목록 조회
     *   <li>permissions 테이블에서 Permission 정보 조회
     *   <li>PermissionCode Set 구성 후 Role Domain 변환
     * </ol>
     *
     * @param roleId 역할 ID
     * @return Role (Optional)
     */
    @Override
    public Optional<Role> findById(RoleId roleId) {
        return roleQueryDslRepository.findById(roleId.value())
            .map(this::toRoleWithPermissions);
    }

    /**
     * UserId로 해당 사용자의 모든 Role 조회
     *
     * <p>Long FK 전략에 따라 별도 쿼리로 분리:
     *
     * <ol>
     *   <li>user_roles 테이블에서 role_id 목록 조회
     *   <li>roles 테이블에서 Role 정보 조회
     *   <li>role_permissions 테이블에서 permission_id 목록 조회
     *   <li>permissions 테이블에서 Permission 정보 조회
     *   <li>각 Role에 해당 PermissionCode Set 매핑
     * </ol>
     *
     * @param userId 사용자 ID
     * @return 사용자에게 할당된 Role 목록
     */
    @Override
    public List<Role> findByUserId(UserId userId) {
        List<Long> roleIds = userRoleQueryDslRepository.findRoleIdsByUserId(userId.value());
        if (roleIds.isEmpty()) {
            return List.of();
        }

        return findRolesByIds(roleIds);
    }

    /**
     * Role 이름으로 조회
     *
     * @param roleName 역할 이름 (예: ROLE_USER)
     * @return Role (Optional)
     */
    @Override
    public Optional<Role> findByName(String roleName) {
        return roleQueryDslRepository.findByName(roleName).map(this::toRoleWithPermissions);
    }

    /**
     * RoleJpaEntity를 Permission과 함께 Role Domain으로 변환
     *
     * @param roleEntity Role Entity
     * @return Role Domain
     */
    private Role toRoleWithPermissions(RoleJpaEntity roleEntity) {
        Set<PermissionCode> permissions = findPermissionsByRoleId(roleEntity.getId());
        return roleJpaEntityMapper.toDomain(roleEntity, permissions);
    }

    /**
     * Role ID로 해당 Role의 PermissionCode Set 조회
     *
     * @param roleId Role ID
     * @return PermissionCode Set
     */
    private Set<PermissionCode> findPermissionsByRoleId(Long roleId) {
        List<Long> permissionIds =
                rolePermissionQueryDslRepository.findPermissionIdsByRoleId(roleId);
        if (permissionIds.isEmpty()) {
            return Set.of();
        }

        return permissionQueryDslRepository.findByIds(permissionIds).stream()
            .map(permissionJpaEntityMapper::toPermissionCode)
            .collect(Collectors.toSet());
    }

    /**
     * Role ID 목록으로 Role 목록 조회 (N+1 방지)
     *
     * @param roleIds Role ID 목록
     * @return Role 목록
     */
    private List<Role> findRolesByIds(List<Long> roleIds) {
        return roleQueryDslRepository.findByIds(roleIds).stream()
            .map(this::toRoleWithPermissions)
            .toList();
    }
}
