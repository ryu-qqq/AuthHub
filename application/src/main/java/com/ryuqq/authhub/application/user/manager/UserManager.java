package com.ryuqq.authhub.application.user.manager;

import com.ryuqq.authhub.application.user.port.out.command.UserPersistencePort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserManager {

    private final UserPersistencePort userPersistencePort;

    public UserManager(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }

    @Transactional
    public UserId persist(User user) {
        return userPersistencePort.persist(user);
    }
}
