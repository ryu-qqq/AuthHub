package com.ryuqq.authhub.application.permission.manager.command;

import com.ryuqq.authhub.application.permission.port.out.command.PermissionPersistencePort;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * PermissionTransactionManager - 단일 Port 트랜잭션 관리
 *
 * <p>단일 PersistencePort에 대한 트랜잭션을 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional} 클래스 레벨
 *   <li>{@code persist()} 메서드만 제공 (순수 위임)
 *   <li>단일 PersistencePort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional
public class PermissionTransactionManager {

    private final PermissionPersistencePort persistencePort;

    public PermissionTransactionManager(PermissionPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * Permission 영속화
     *
     * @param permission Permission Aggregate
     * @return 영속화된 Permission (ID 할당됨)
     */
    public Permission persist(Permission permission) {
        return persistencePort.persist(permission);
    }
}
