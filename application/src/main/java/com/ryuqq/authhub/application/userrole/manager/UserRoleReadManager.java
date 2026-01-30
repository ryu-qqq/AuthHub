package com.ryuqq.authhub.application.userrole.manager;

import com.ryuqq.authhub.application.userrole.port.out.query.UserRoleQueryPort;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import com.ryuqq.authhub.domain.userrole.query.criteria.UserRoleSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserRoleReadManager - 사용자-역할 관계 Read Manager
 *
 * <p>사용자-역할 관계 조회 작업을 관리합니다.
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
public class UserRoleReadManager {

    private final UserRoleQueryPort queryPort;

    public UserRoleReadManager(UserRoleQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 사용자-역할 관계 존재 여부 확인
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean exists(UserId userId, RoleId roleId) {
        return queryPort.exists(userId, roleId);
    }

    /**
     * 사용자의 역할 목록 조회
     *
     * @param userId 사용자 ID
     * @return 사용자-역할 관계 목록
     */
    @Transactional(readOnly = true)
    public List<UserRole> findAllByUserId(UserId userId) {
        return queryPort.findAllByUserId(userId);
    }

    /**
     * 역할이 어떤 사용자에게라도 할당되어 있는지 확인
     *
     * @param roleId 역할 ID
     * @return 사용 중 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByRoleId(RoleId roleId) {
        return queryPort.existsByRoleId(roleId);
    }

    /**
     * 조건에 맞는 사용자-역할 관계 목록 조회
     *
     * @param criteria 검색 조건
     * @return 사용자-역할 관계 목록
     */
    @Transactional(readOnly = true)
    public List<UserRole> findAllBySearchCriteria(UserRoleSearchCriteria criteria) {
        return queryPort.findAllBySearchCriteria(criteria);
    }

    /**
     * 조건에 맞는 사용자-역할 관계 개수 조회
     *
     * @param criteria 검색 조건
     * @return 총 개수
     */
    @Transactional(readOnly = true)
    public long countBySearchCriteria(UserRoleSearchCriteria criteria) {
        return queryPort.countBySearchCriteria(criteria);
    }

    /**
     * 사용자에게 이미 할당된 역할 ID 목록 조회 (요청된 roleIds 중에서)
     *
     * @param userId 사용자 ID
     * @param roleIds 확인할 역할 ID 목록
     * @return 이미 할당된 역할 ID 목록
     */
    @Transactional(readOnly = true)
    public List<RoleId> findAssignedRoleIds(UserId userId, List<RoleId> roleIds) {
        return queryPort.findAssignedRoleIds(userId, roleIds);
    }
}
