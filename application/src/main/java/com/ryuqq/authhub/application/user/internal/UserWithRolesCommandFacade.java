package com.ryuqq.authhub.application.user.internal;

import com.ryuqq.authhub.application.user.manager.UserCommandManager;
import com.ryuqq.authhub.application.userrole.manager.UserRoleCommandManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserWithRolesCommandFacade - User + UserRole 한방 영속화 Facade
 *
 * <p>User와 UserRole을 하나의 트랜잭션으로 영속화합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
class UserWithRolesCommandFacade {

    private final UserCommandManager userCommandManager;
    private final UserRoleCommandManager userRoleCommandManager;

    UserWithRolesCommandFacade(
            UserCommandManager userCommandManager, UserRoleCommandManager userRoleCommandManager) {
        this.userCommandManager = userCommandManager;
        this.userRoleCommandManager = userRoleCommandManager;
    }

    /**
     * User + UserRole 한방 영속화
     *
     * @param bundle 영속화할 도메인 객체 번들
     * @return 생성된 사용자 ID (UUIDv7)
     */
    @Transactional
    String persistAll(CreateUserWithRolesBundle bundle) {
        String userId = userCommandManager.persist(bundle.user());
        if (!bundle.userRoles().isEmpty()) {
            userRoleCommandManager.persistAll(bundle.userRoles());
        }
        return userId;
    }
}
