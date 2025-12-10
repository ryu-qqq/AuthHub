package com.ryuqq.authhub.application.auth.manager;

import com.ryuqq.authhub.application.auth.port.out.command.RefreshTokenPersistencePort;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Refresh Token Persistence Manager
 *
 * <p>Refresh Token RDB 저장/삭제를 담당하는 Manager
 *
 * <p>이 클래스만 트랜잭션을 관리 (MySQL 저장은 트랜잭션 필요)
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RefreshTokenPersistenceManager {

    private final RefreshTokenPersistencePort refreshTokenPersistencePort;

    public RefreshTokenPersistenceManager(RefreshTokenPersistencePort refreshTokenPersistencePort) {
        this.refreshTokenPersistencePort = refreshTokenPersistencePort;
    }

    /**
     * Refresh Token RDB 저장
     *
     * @param userId 사용자 ID (Value Object)
     * @param refreshToken Refresh Token 값
     */
    @Transactional
    public void persist(UserId userId, String refreshToken) {
        refreshTokenPersistencePort.persist(userId, refreshToken);
    }

    /**
     * Refresh Token 삭제 (사용자 ID 기준)
     *
     * @param userId 사용자 ID (Value Object)
     */
    @Transactional
    public void deleteByUserId(UserId userId) {
        refreshTokenPersistencePort.deleteByUserId(userId);
    }
}
