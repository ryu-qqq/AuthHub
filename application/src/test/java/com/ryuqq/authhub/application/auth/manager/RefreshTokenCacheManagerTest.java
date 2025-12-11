package com.ryuqq.authhub.application.auth.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.port.out.cache.RefreshTokenCachePort;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.Optional;
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
 * RefreshTokenCacheManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenCacheManager 단위 테스트")
class RefreshTokenCacheManagerTest {

    @Mock private RefreshTokenCachePort refreshTokenCachePort;

    private RefreshTokenCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = new RefreshTokenCacheManager(refreshTokenCachePort);
    }

    @Nested
    @DisplayName("save 메서드")
    class SaveTest {

        @Test
        @DisplayName("Refresh Token을 캐시에 저장한다")
        void shouldSaveRefreshTokenToCache() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            String refreshToken = "refresh-token";
            long expiresInSeconds = 604800L;

            // when
            cacheManager.save(userId, refreshToken, expiresInSeconds);

            // then
            verify(refreshTokenCachePort).save(userId, refreshToken, expiresInSeconds);
        }
    }

    @Nested
    @DisplayName("findByUserId 메서드")
    class FindByUserIdTest {

        @Test
        @DisplayName("사용자 ID로 Refresh Token을 조회한다")
        void shouldFindRefreshTokenByUserId() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            String expectedToken = "cached-refresh-token";
            given(refreshTokenCachePort.findByUserId(userId))
                    .willReturn(Optional.of(expectedToken));

            // when
            Optional<String> result = cacheManager.findByUserId(userId);

            // then
            assertThat(result).isPresent().contains(expectedToken);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenUserIdNotFound() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            given(refreshTokenCachePort.findByUserId(userId)).willReturn(Optional.empty());

            // when
            Optional<String> result = cacheManager.findByUserId(userId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUserIdByToken 메서드")
    class FindUserIdByTokenTest {

        @Test
        @DisplayName("Refresh Token으로 사용자 ID를 조회한다")
        void shouldFindUserIdByToken() {
            // given
            String refreshToken = "refresh-token";
            UserId expectedUserId = UserId.of(UUID.randomUUID());
            given(refreshTokenCachePort.findUserIdByToken(refreshToken))
                    .willReturn(Optional.of(expectedUserId));

            // when
            Optional<UserId> result = cacheManager.findUserIdByToken(refreshToken);

            // then
            assertThat(result).isPresent().contains(expectedUserId);
        }

        @Test
        @DisplayName("존재하지 않는 토큰으로 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenTokenNotFound() {
            // given
            String refreshToken = "non-existent-token";
            given(refreshTokenCachePort.findUserIdByToken(refreshToken))
                    .willReturn(Optional.empty());

            // when
            Optional<UserId> result = cacheManager.findUserIdByToken(refreshToken);

            // then
            assertThat(result).isEmpty();
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
            cacheManager.deleteByUserId(userId);

            // then
            verify(refreshTokenCachePort).deleteByUserId(userId);
        }
    }

    @Nested
    @DisplayName("deleteByToken 메서드")
    class DeleteByTokenTest {

        @Test
        @DisplayName("토큰 값으로 Refresh Token을 삭제한다")
        void shouldDeleteRefreshTokenByTokenValue() {
            // given
            String refreshToken = "token-to-delete";

            // when
            cacheManager.deleteByToken(refreshToken);

            // then
            verify(refreshTokenCachePort).deleteByToken(refreshToken);
        }
    }
}
