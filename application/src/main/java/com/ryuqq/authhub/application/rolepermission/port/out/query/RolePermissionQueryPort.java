package com.ryuqq.authhub.application.rolepermission.port.out.query;

import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import java.util.List;
import java.util.Optional;

/**
 * RolePermissionQueryPort - 역할-권한 관계 조회 포트 (Query)
 *
 * <p>RolePermission Aggregate 조회 작업을 담당하는 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공 (findById, existsById, findAll)
 *   <li>저장/삭제 메서드 금지 (PersistencePort로 분리)
 *   <li>Value Object 파라미터 (원시 타입 금지)
 *   <li>Domain 반환 (DTO/Entity 반환 금지)
 *   <li>Criteria 기반 조회 (개별 파라미터 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface RolePermissionQueryPort {

    /**
     * 역할-권한 관계 존재 여부 확인
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 존재 여부
     */
    boolean exists(RoleId roleId, PermissionId permissionId);

    /**
     * 역할-권한 관계 조회
     *
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 역할-권한 관계 (Optional)
     */
    Optional<RolePermission> findByRoleIdAndPermissionId(RoleId roleId, PermissionId permissionId);

    /**
     * 역할의 권한 목록 조회
     *
     * @param roleId 역할 ID
     * @return 역할-권한 관계 목록
     */
    List<RolePermission> findAllByRoleId(RoleId roleId);

    /**
     * 권한이 부여된 역할 목록 조회
     *
     * @param permissionId 권한 ID
     * @return 역할-권한 관계 목록
     */
    List<RolePermission> findAllByPermissionId(PermissionId permissionId);

    /**
     * 권한이 어떤 역할에라도 부여되어 있는지 확인 (Permission 삭제 검증용)
     *
     * @param permissionId 권한 ID
     * @return 사용 중 여부
     */
    boolean existsByPermissionId(PermissionId permissionId);

    /**
     * 조건에 맞는 역할-권한 관계 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return 역할-권한 관계 목록
     */
    List<RolePermission> findAllBySearchCriteria(RolePermissionSearchCriteria criteria);

    /**
     * 조건에 맞는 역할-권한 관계 개수 조회
     *
     * @param criteria 검색 조건
     * @return 총 개수
     */
    long countBySearchCriteria(RolePermissionSearchCriteria criteria);

    /**
     * 역할에 이미 부여된 권한 ID 목록 조회 (요청된 permissionIds 중에서)
     *
     * <p>요청된 permissionIds 중 해당 roleId에 이미 부여된 것만 반환합니다. 전체 RolePermission을 조회하지 않고 필요한 것만 조회하여 성능을
     * 최적화합니다.
     *
     * @param roleId 역할 ID
     * @param permissionIds 확인할 권한 ID 목록
     * @return 이미 부여된 권한 ID 목록
     */
    List<PermissionId> findGrantedPermissionIds(RoleId roleId, List<PermissionId> permissionIds);

    /**
     * 여러 역할의 권한 목록 조회 (IN절)
     *
     * @param roleIds 역할 ID 목록
     * @return 역할-권한 관계 목록
     */
    List<RolePermission> findAllByRoleIds(List<RoleId> roleIds);
}
