package com.ryuqq.authhub.application.user.manager;

import com.ryuqq.authhub.application.user.port.out.command.UserCommandPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserCommandManager - User Command 관리자
 *
 * <p>CommandPort를 래핑하여 트랜잭션 일관성을 보장합니다.
 *
 * <p>C-004: @Transactional은 Manager에서만 메서드 단위로 사용합니다.
 *
 * <p>C-005: Port를 직접 노출하지 않고 Manager로 래핑합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserCommandManager {

    private final UserCommandPort persistencePort;

    public UserCommandManager(UserCommandPort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * User 영속화
     *
     * @param user 영속화할 User
     * @return 영속화된 User ID (String - UUIDv7)
     */
    @Transactional
    public String persist(User user) {
        return persistencePort.persist(user);
    }
}
