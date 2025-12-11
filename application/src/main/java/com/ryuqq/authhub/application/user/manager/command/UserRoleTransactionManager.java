package com.ryuqq.authhub.application.user.manager.command;

import com.ryuqq.authhub.application.user.port.out.persistence.UserRolePersistencePort;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserRoleTransactionManager - 사용자 역할 트랜잭션 관리자
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>{@code @Transactional} 책임
 *   <li>Port를 통한 영속화
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleTransactionManager {

    private final UserRolePersistencePort persistencePort;

    public UserRoleTransactionManager(UserRolePersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * 사용자 역할을 저장합니다.
     *
     * @param userRole 저장할 사용자 역할
     * @return 저장된 사용자 역할
     */
    @Transactional
    public UserRole persist(UserRole userRole) {
        return persistencePort.save(userRole);
    }

    /**
     * 사용자 역할을 삭제합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     */
    @Transactional
    public void delete(UserId userId, RoleId roleId) {
        persistencePort.delete(userId, roleId);
    }
}
