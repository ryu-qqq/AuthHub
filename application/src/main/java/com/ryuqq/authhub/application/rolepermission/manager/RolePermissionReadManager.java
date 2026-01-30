package com.ryuqq.authhub.application.rolepermission.manager;

import com.ryuqq.authhub.application.rolepermission.port.out.query.RolePermissionQueryPort;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RolePermissionReadManager - 역할-권한 관계 Read Manager
 *
 * <p>역할-권한 관계 조회 작업을 관리합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>조회 트랜잭션 관리 (@Transactional readOnly)
 *   <li>QueryPort 위임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RolePermissionReadManager {

    private final RolePermissionQueryPort queryPort;

    public RolePermissionReadManager(RolePermissionQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 역할-권한 관계 존재 여부 확인
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean exists(RoleId roleId, PermissionId permissionId) {
        return queryPort.exists(roleId, permissionId);
    }

    /**
     * 역할의 권한 목록 조회
     *
     * @param roleId 역할 ID
     * @return 역할-권한 관계 목록
     */
    @Transactional(readOnly = true)
    public List<RolePermission> findAllByRoleId(RoleId roleId) {
        return queryPort.findAllByRoleId(roleId);
    }

    /**
     * 권한이 어떤 역할에라도 부여되어 있는지 확인
     *
     * @param permissionId 권한 ID
     * @return 사용 중 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByPermissionId(PermissionId permissionId) {
        return queryPort.existsByPermissionId(permissionId);
    }

    /**
     * 조건에 맞는 역할-권한 관계 목록 조회
     *
     * @param criteria 검색 조건
     * @return 역할-권한 관계 목록
     */
    @Transactional(readOnly = true)
    public List<RolePermission> findAllBySearchCriteria(RolePermissionSearchCriteria criteria) {
        return queryPort.findAllBySearchCriteria(criteria);
    }

    /**
     * 조건에 맞는 역할-권한 관계 개수 조회
     *
     * @param criteria 검색 조건
     * @return 총 개수
     */
    @Transactional(readOnly = true)
    public long countBySearchCriteria(RolePermissionSearchCriteria criteria) {
        return queryPort.countBySearchCriteria(criteria);
    }

    /**
     * 역할에 이미 부여된 권한 ID 목록 조회 (요청된 permissionIds 중에서)
     *
     * @param roleId 역할 ID
     * @param permissionIds 확인할 권한 ID 목록
     * @return 이미 부여된 권한 ID 목록
     */
    @Transactional(readOnly = true)
    public List<PermissionId> findGrantedPermissionIds(
            RoleId roleId, List<PermissionId> permissionIds) {
        return queryPort.findGrantedPermissionIds(roleId, permissionIds);
    }

    /**
     * 여러 역할의 권한 목록 조회 (IN절)
     *
     * @param roleIds 역할 ID 목록
     * @return 역할-권한 관계 목록
     */
    @Transactional(readOnly = true)
    public List<RolePermission> findAllByRoleIds(List<RoleId> roleIds) {
        return queryPort.findAllByRoleIds(roleIds);
    }
}
