package com.ryuqq.authhub.application.rolepermission.port.out.command;

import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import java.util.List;

/**
 * RolePermissionPersistencePort - 역할-권한 관계 영속화 포트 (Command)
 *
 * <p>RolePermission Aggregate의 영속화 작업을 담당하는 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>저장/삭제 메서드만 제공 (persist, delete)
 *   <li>조회 메서드 금지 (QueryPort로 분리)
 *   <li>Value Object 파라미터 (원시 타입 금지)
 *   <li>Domain 파라미터 (Entity 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RolePermissionCommandPort {

    /**
     * 역할-권한 관계 저장
     *
     * @param rolePermission 저장할 역할-권한 관계
     * @return 저장된 역할-권한 관계
     */
    RolePermission persist(RolePermission rolePermission);

    /**
     * 역할-권한 관계 다건 저장
     *
     * @param rolePermissions 저장할 역할-권한 관계 목록
     * @return 저장된 역할-권한 관계 목록
     */
    List<RolePermission> persistAll(List<RolePermission> rolePermissions);

    /**
     * 역할-권한 관계 삭제
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     */
    void delete(RoleId roleId, PermissionId permissionId);

    /**
     * 역할의 모든 권한 관계 삭제 (Role 삭제 시 Cascade)
     *
     * @param roleId 역할 ID
     */
    void deleteAllByRoleId(RoleId roleId);

    /**
     * 역할-권한 관계 다건 삭제
     *
     * @param roleId 역할 ID
     * @param permissionIds 삭제할 권한 ID 목록
     */
    void deleteAll(RoleId roleId, List<PermissionId> permissionIds);
}
