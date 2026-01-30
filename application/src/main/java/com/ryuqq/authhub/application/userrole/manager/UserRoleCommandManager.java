package com.ryuqq.authhub.application.userrole.manager;

import com.ryuqq.authhub.application.userrole.port.out.command.UserRoleCommandPort;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.userrole.aggregate.UserRole;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserRoleCommandManager - 사용자-역할 관계 Command Manager
 *
 * <p>사용자-역할 관계 Command 작업을 관리합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Command 트랜잭션 관리 (@Transactional)
 *   <li>PersistencePort 위임
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleCommandManager {

    private final UserRoleCommandPort persistencePort;

    public UserRoleCommandManager(UserRoleCommandPort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * 사용자-역할 관계 저장
     *
     * @param userRole 저장할 사용자-역할 관계
     * @return 저장된 사용자-역할 관계
     */
    @Transactional
    public UserRole persist(UserRole userRole) {
        return persistencePort.persist(userRole);
    }

    /**
     * 사용자-역할 관계 다건 저장
     *
     * @param userRoles 저장할 사용자-역할 관계 목록
     * @return 저장된 사용자-역할 관계 목록
     */
    @Transactional
    public List<UserRole> persistAll(List<UserRole> userRoles) {
        return persistencePort.persistAll(userRoles);
    }

    /**
     * 사용자-역할 관계 삭제
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     */
    @Transactional
    public void delete(UserId userId, RoleId roleId) {
        persistencePort.delete(userId, roleId);
    }

    /**
     * 사용자의 모든 역할 관계 삭제 (User 삭제 시 Cascade)
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteAllByUserId(UserId userId) {
        persistencePort.deleteAllByUserId(userId);
    }

    /**
     * 사용자-역할 관계 다건 삭제
     *
     * @param userId 사용자 ID
     * @param roleIds 삭제할 역할 ID 목록
     */
    @Transactional
    public void deleteAll(UserId userId, List<RoleId> roleIds) {
        persistencePort.deleteAll(userId, roleIds);
    }
}
