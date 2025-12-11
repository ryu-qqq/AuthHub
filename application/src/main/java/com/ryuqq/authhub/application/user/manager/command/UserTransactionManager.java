package com.ryuqq.authhub.application.user.manager.command;

import com.ryuqq.authhub.application.user.port.out.persistence.UserPersistencePort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserTransactionManager - 사용자 트랜잭션 관리자
 *
 * <p>사용자 영속화 작업의 트랜잭션을 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>{@code @Transactional} 클래스 레벨
 *   <li>Port-Out 의존
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional
public class UserTransactionManager {

    private final UserPersistencePort persistencePort;

    public UserTransactionManager(UserPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * 사용자 저장
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    public User persist(User user) {
        return persistencePort.persist(user);
    }

    /**
     * 사용자 삭제 (소프트 삭제)
     *
     * @param user 삭제할 사용자
     */
    public void delete(User user) {
        persistencePort.delete(user);
    }
}
