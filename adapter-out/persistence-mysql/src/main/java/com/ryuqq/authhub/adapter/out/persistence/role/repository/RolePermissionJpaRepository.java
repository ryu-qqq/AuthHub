package com.ryuqq.authhub.adapter.out.persistence.role.repository;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RolePermissionJpaRepository - 역할 권한 JPA Repository
 *
 * <p><strong>UUIDv7 PK 전략:</strong>
 *
 * <ul>
 *   <li>rolePermissionId(UUID)를 PK로 사용
 *   <li>findById()로 UUID 직접 조회 가능
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JpaRepository 상속 필수
 *   <li>UUID 타입 ID 사용
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionJpaEntity, UUID> {

    /**
     * 역할 ID와 권한 ID로 역할 권한을 조회합니다.
     *
     * @param roleId 역할 UUID
     * @param permissionId 권한 UUID
     * @return 역할 권한 (없으면 Optional.empty())
     */
    Optional<RolePermissionJpaEntity> findByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    /**
     * 역할 ID로 모든 권한을 조회합니다.
     *
     * @param roleId 역할 UUID
     * @return 역할 권한 목록
     */
    List<RolePermissionJpaEntity> findAllByRoleId(UUID roleId);

    /**
     * 역할에 특정 권한이 부여되어 있는지 확인합니다.
     *
     * @param roleId 역할 UUID
     * @param permissionId 권한 UUID
     * @return 부여 여부
     */
    boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    /**
     * 역할 권한을 삭제합니다.
     *
     * @param roleId 역할 UUID
     * @param permissionId 권한 UUID
     */
    void deleteByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
}
