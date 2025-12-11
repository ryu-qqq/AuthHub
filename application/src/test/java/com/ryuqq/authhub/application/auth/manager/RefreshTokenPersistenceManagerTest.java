package com.ryuqq.authhub.application.auth.manager;

import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.port.out.command.RefreshTokenPersistencePort;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RefreshTokenPersistenceManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenPersistenceManager 단위 테스트")
class RefreshTokenPersistenceManagerTest {

    @Mock private RefreshTokenPersistencePort refreshTokenPersistencePort;

    private RefreshTokenPersistenceManager persistenceManager;

    @BeforeEach
    void setUp() {
        persistenceManager = new RefreshTokenPersistenceManager(refreshTokenPersistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("Refresh Token을 RDB에 저장한다")
        void shouldPersistRefreshTokenToDatabase() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            String refreshToken = "refresh-token-to-persist";

            // when
            persistenceManager.persist(userId, refreshToken);

            // then
            verify(refreshTokenPersistencePort).persist(userId, refreshToken);
        }
    }

    @Nested
    @DisplayName("deleteByUserId 메서드")
    class DeleteByUserIdTest {

        @Test
        @DisplayName("사용자 ID로 Refresh Token을 삭제한다")
        void shouldDeleteRefreshTokenByUserId() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());

            // when
            persistenceManager.deleteByUserId(userId);

            // then
            verify(refreshTokenPersistencePort).deleteByUserId(userId);
        }
    }
}
