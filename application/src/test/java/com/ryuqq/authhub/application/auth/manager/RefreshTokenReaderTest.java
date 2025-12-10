package com.ryuqq.authhub.application.auth.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.auth.port.out.cache.RefreshTokenCachePort;
import com.ryuqq.authhub.application.auth.port.out.query.RefreshTokenQueryPort;
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
 * RefreshTokenReader 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenReader 단위 테스트")
class RefreshTokenReaderTest {

    @Mock private RefreshTokenCachePort refreshTokenCachePort;
    @Mock private RefreshTokenQueryPort refreshTokenQueryPort;

    private RefreshTokenReader refreshTokenReader;

    @BeforeEach
    void setUp() {
        refreshTokenReader = new RefreshTokenReader(refreshTokenCachePort, refreshTokenQueryPort);
    }

    @Nested
    @DisplayName("findUserIdByToken 메서드")
    class FindUserIdByTokenTest {

        @Test
        @DisplayName("캐시에서 사용자 ID를 찾으면 RDB를 조회하지 않는다")
        void shouldReturnFromCacheWithoutDbCall() {
            // given
            String refreshToken = "cached-token";
            UserId cachedUserId = UserId.of(UUID.randomUUID());
            given(refreshTokenCachePort.findUserIdByToken(refreshToken))
                    .willReturn(Optional.of(cachedUserId));

            // when
            Optional<UserId> result = refreshTokenReader.findUserIdByToken(refreshToken);

            // then
            assertThat(result).isPresent().contains(cachedUserId);
            verify(refreshTokenQueryPort, never()).findUserIdByToken(anyString());
        }

        @Test
        @DisplayName("캐시 미스 시 RDB에서 조회하고 캐시를 워밍한다")
        void shouldFallbackToDbAndWarmCache() {
            // given
            String refreshToken = "db-token";
            UserId dbUserId = UserId.of(UUID.randomUUID());
            given(refreshTokenCachePort.findUserIdByToken(refreshToken))
                    .willReturn(Optional.empty());
            given(refreshTokenQueryPort.findUserIdByToken(refreshToken))
                    .willReturn(Optional.of(dbUserId));

            // when
            Optional<UserId> result = refreshTokenReader.findUserIdByToken(refreshToken);

            // then
            assertThat(result).isPresent().contains(dbUserId);
            verify(refreshTokenCachePort).save(eq(dbUserId), eq(refreshToken), anyLong());
        }

        @Test
        @DisplayName("캐시와 RDB 모두 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotFound() {
            // given
            String refreshToken = "non-existent-token";
            given(refreshTokenCachePort.findUserIdByToken(refreshToken))
                    .willReturn(Optional.empty());
            given(refreshTokenQueryPort.findUserIdByToken(refreshToken))
                    .willReturn(Optional.empty());

            // when
            Optional<UserId> result = refreshTokenReader.findUserIdByToken(refreshToken);

            // then
            assertThat(result).isEmpty();
            verify(refreshTokenCachePort, never()).save(any(), anyString(), anyLong());
        }
    }

    @Nested
    @DisplayName("findByUserId 메서드")
    class FindByUserIdTest {

        @Test
        @DisplayName("캐시에서 토큰을 찾으면 RDB를 조회하지 않는다")
        void shouldReturnFromCacheWithoutDbCall() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            String cachedToken = "cached-refresh-token";
            given(refreshTokenCachePort.findByUserId(userId)).willReturn(Optional.of(cachedToken));

            // when
            Optional<String> result = refreshTokenReader.findByUserId(userId);

            // then
            assertThat(result).isPresent().contains(cachedToken);
            verify(refreshTokenQueryPort, never()).findByUserId(any());
        }

        @Test
        @DisplayName("캐시 미스 시 RDB에서 조회하고 캐시를 워밍한다")
        void shouldFallbackToDbAndWarmCache() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            String dbToken = "db-refresh-token";
            given(refreshTokenCachePort.findByUserId(userId)).willReturn(Optional.empty());
            given(refreshTokenQueryPort.findByUserId(userId)).willReturn(Optional.of(dbToken));

            // when
            Optional<String> result = refreshTokenReader.findByUserId(userId);

            // then
            assertThat(result).isPresent().contains(dbToken);
            verify(refreshTokenCachePort).save(eq(userId), eq(dbToken), anyLong());
        }

        @Test
        @DisplayName("캐시와 RDB 모두 없으면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenNotFound() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());
            given(refreshTokenCachePort.findByUserId(userId)).willReturn(Optional.empty());
            given(refreshTokenQueryPort.findByUserId(userId)).willReturn(Optional.empty());

            // when
            Optional<String> result = refreshTokenReader.findByUserId(userId);

            // then
            assertThat(result).isEmpty();
        }
    }

    private static UserId any() {
        return org.mockito.ArgumentMatchers.any(UserId.class);
    }
}
