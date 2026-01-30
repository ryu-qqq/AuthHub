package com.ryuqq.authhub.application.userrole.port.out.query;

import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.query.criteria.UserRoleSearchCriteria;
import java.util.List;
import java.util.Optional;

/**
 * UserRoleQueryPort - 사용자-역할 관계 조회 포트 (Query)
 *
 * <p>UserRole Aggregate 조회 작업을 담당하는 Port입니다.
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
public interface UserRoleQueryPort {

    /**
     * 사용자-역할 관계 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 존재 여부
     */
    boolean exists(UserId userId, RoleId roleId);

    /**
     * 사용자-역할 관계 조회
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 사용자-역할 관계 (Optional)
     */
    Optional<UserRole> findByUserIdAndRoleId(UserId userId, RoleId roleId);

    /**
     * 사용자의 역할 목록 조회
     *
     * @param userId 사용자 ID
     * @return 사용자-역할 관계 목록
     */
    List<UserRole> findAllByUserId(UserId userId);

    /**
     * 역할이 할당된 사용자 목록 조회
     *
     * @param roleId 역할 ID
     * @return 사용자-역할 관계 목록
     */
    List<UserRole> findAllByRoleId(RoleId roleId);

    /**
     * 역할이 어떤 사용자에게라도 할당되어 있는지 확인 (Role 삭제 검증용)
     *
     * @param roleId 역할 ID
     * @return 사용 중 여부
     */
    boolean existsByRoleId(RoleId roleId);

    /**
     * 조건에 맞는 사용자-역할 관계 목록 조회 (페이징)
     *
     * @param criteria 검색 조건
     * @return 사용자-역할 관계 목록
     */
    List<UserRole> findAllBySearchCriteria(UserRoleSearchCriteria criteria);

    /**
     * 조건에 맞는 사용자-역할 관계 개수 조회
     *
     * @param criteria 검색 조건
     * @return 총 개수
     */
    long countBySearchCriteria(UserRoleSearchCriteria criteria);

    /**
     * 사용자에게 이미 할당된 역할 ID 목록 조회 (요청된 roleIds 중에서)
     *
     * <p>요청된 roleIds 중 해당 userId에 이미 할당된 것만 반환합니다. 전체 UserRole을 조회하지 않고 필요한 것만 조회하여 성능을 최적화합니다.
     *
     * @param userId 사용자 ID
     * @param roleIds 확인할 역할 ID 목록
     * @return 이미 할당된 역할 ID 목록
     */
    List<RoleId> findAssignedRoleIds(UserId userId, List<RoleId> roleIds);
}
