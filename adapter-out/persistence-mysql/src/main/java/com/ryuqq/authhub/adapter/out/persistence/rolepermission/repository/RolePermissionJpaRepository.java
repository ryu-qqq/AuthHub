package com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * RolePermissionJpaRepository - 역할-권한 관계 JPA Repository (Command 전용)
 *
 * <p>Spring Data JPA 인터페이스로 Command(CUD) 작업을 담당합니다.
 *
 * <p><strong>Long PK 전략:</strong>
 *
 * <ul>
 *   <li>rolePermissionId(Long)를 PK로 사용 (Auto Increment)
 *   <li>findById()로 Long ID 직접 조회 가능
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>save() - 관계 저장
 *   <li>delete() - 관계 삭제
 *   <li>deleteByRoleIdAndPermissionId() - roleId + permissionId로 삭제
 *   <li>deleteAllByRoleId() - 역할의 모든 관계 삭제 (Cascade)
 * </ul>
 *
 * <p><strong>CQRS 패턴:</strong>
 *
 * <ul>
 *   <li>Command: RolePermissionJpaRepository (JPA)
 *   <li>Query: RolePermissionQueryDslRepository (QueryDSL)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionJpaEntity, Long> {

    /**
     * roleId + permissionId로 관계 삭제
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     */
    @Modifying
    @Query(
            "DELETE FROM RolePermissionJpaEntity r WHERE r.roleId = :roleId AND r.permissionId ="
                    + " :permissionId")
    void deleteByRoleIdAndPermissionId(
            @Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 역할의 모든 관계 삭제 (Role 삭제 시 Cascade)
     *
     * @param roleId 역할 ID
     */
    @Modifying
    @Query("DELETE FROM RolePermissionJpaEntity r WHERE r.roleId = :roleId")
    void deleteAllByRoleId(@Param("roleId") Long roleId);

    /**
     * 역할의 특정 권한들 삭제
     *
     * @param roleId 역할 ID
     * @param permissionIds 권한 ID 목록
     */
    @Modifying
    @Query(
            "DELETE FROM RolePermissionJpaEntity r WHERE r.roleId = :roleId AND r.permissionId IN"
                    + " :permissionIds")
    void deleteAllByRoleIdAndPermissionIdIn(
            @Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}
