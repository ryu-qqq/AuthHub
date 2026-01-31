package com.ryuqq.authhub.application.userrole.internal;

import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.application.user.validator.UserValidator;
import com.ryuqq.authhub.application.userrole.factory.UserRoleCommandFactory;
import com.ryuqq.authhub.application.userrole.manager.UserRoleReadManager;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * AssignUserRoleCoordinator - 사용자 역할 할당 Coordinator
 *
 * <p>크로스 도메인 검증 및 UserRole 생성을 조율합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>User 존재 검증 (UserValidator)
 *   <li>Role 일괄 존재 검증 (IN절 조회)
 *   <li>이미 할당된 UserRole 필터링 (IN절 최적화)
 *   <li>Factory를 통한 UserRole 목록 생성
 * </ul>
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>User 존재 여부 검증
 *   <li>Role IN절 일괄 조회 및 검증
 *   <li>요청된 roleIds 중 이미 할당된 것만 조회 (IN절 최적화)
 *   <li>이미 할당된 역할 필터링
 *   <li>Factory.createAll()로 UserRole 목록 생성
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class AssignUserRoleCoordinator {

    private final UserValidator userValidator;
    private final RoleValidator roleValidator;
    private final UserRoleReadManager userRoleReadManager;
    private final UserRoleCommandFactory commandFactory;

    public AssignUserRoleCoordinator(
            UserValidator userValidator,
            RoleValidator roleValidator,
            UserRoleReadManager userRoleReadManager,
            UserRoleCommandFactory commandFactory) {
        this.userValidator = userValidator;
        this.roleValidator = roleValidator;
        this.userRoleReadManager = userRoleReadManager;
        this.commandFactory = commandFactory;
    }

    /**
     * 사용자 역할 할당 조율
     *
     * <p>검증 → 필터링 → UserRole 생성을 한 번에 처리합니다.
     *
     * @param userId 사용자 ID (String)
     * @param roleIds 역할 ID 목록 (Long 리스트)
     * @return 생성된 UserRole 목록 (이미 할당된 역할은 제외됨)
     */
    public List<UserRole> coordinate(String userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        UserId userIdVo = UserId.of(userId);
        List<RoleId> roleIdVos = roleIds.stream().map(RoleId::of).toList();

        // 1. User 존재 검증
        userValidator.findExistingOrThrow(userIdVo);

        // 2. Role IN절 일괄 조회 및 검증
        roleValidator.validateAllExist(roleIdVos);

        // 3. 이미 할당된 역할 필터링 (IN절 최적화)
        List<RoleId> newRoleIds = filterNotAssigned(userIdVo, roleIdVos);

        if (newRoleIds.isEmpty()) {
            return List.of();
        }

        // 4. Factory로 UserRole 목록 생성
        return commandFactory.createAll(userIdVo, newRoleIds);
    }

    /**
     * 이미 할당된 역할 필터링 (IN절 최적화)
     *
     * <p>요청된 roleIds 중 이미 할당된 것만 조회하여 필터링합니다. 해당 User의 모든 역할을 조회하지 않고, 요청된 것만 확인하여 성능을 최적화합니다.
     *
     * <p><strong>쿼리 최적화:</strong>
     *
     * <pre>
     * Before: SELECT * FROM user_roles WHERE user_id = ?
     *         → 해당 User의 모든 역할 조회 (수백 개 가능)
     *
     * After:  SELECT role_id FROM user_roles
     *         WHERE user_id = ? AND role_id IN (?,?,?)
     *         → 요청된 것 중 이미 존재하는 것만 조회
     * </pre>
     *
     * @param userId 사용자 ID
     * @param roleIds 요청된 역할 ID 목록
     * @return 아직 할당되지 않은 역할 ID 목록
     */
    private List<RoleId> filterNotAssigned(UserId userId, List<RoleId> roleIds) {
        // 요청된 roleIds 중 이미 할당된 것만 조회 (IN절 최적화)
        List<RoleId> assignedRoleIds = userRoleReadManager.findAssignedRoleIds(userId, roleIds);

        // 이미 할당된 roleId Set
        Set<Long> assignedIds =
                assignedRoleIds.stream().map(RoleId::value).collect(Collectors.toSet());

        // 아직 할당되지 않은 roleId만 필터링
        return roleIds.stream().filter(rid -> !assignedIds.contains(rid.value())).toList();
    }
}
