package com.ryuqq.authhub.adapter.out.persistence.auth.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.auth.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.auth.repository.RefreshTokenQueryDslRepository;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.LocalDateTime;
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
 * RefreshTokenQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenQueryAdapter 단위 테스트")
class RefreshTokenQueryAdapterTest {

    @Mock private RefreshTokenQueryDslRepository queryDslRepository;

    private RefreshTokenQueryAdapter adapter;

    private static final UUID USER_UUID = UserFixture.defaultUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenQueryAdapter(queryDslRepository);
    }

    @Nested
    @DisplayName("findByUserId 메서드")
    class FindByUserIdTest {

        @Test
        @DisplayName("UserId로 RefreshToken을 조회한다")
        void shouldFindRefreshTokenByUserId() {
            // given
            UserId userId = UserId.of(USER_UUID);
            String tokenValue = "stored-refresh-token-value";
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.of(1L, USER_UUID, tokenValue, FIXED_TIME, FIXED_TIME);

            given(queryDslRepository.findByUserId(USER_UUID)).willReturn(Optional.of(entity));

            // when
            Optional<String> result = adapter.findByUserId(userId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(tokenValue);
            verify(queryDslRepository).findByUserId(USER_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 UserId로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenTokenNotFound() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());

            given(queryDslRepository.findByUserId(userId.value())).willReturn(Optional.empty());

            // when
            Optional<String> result = adapter.findByUserId(userId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUserId 메서드")
    class ExistsByUserIdTest {

        @Test
        @DisplayName("RefreshToken이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenTokenExists() {
            // given
            UserId userId = UserId.of(USER_UUID);

            given(queryDslRepository.existsByUserId(USER_UUID)).willReturn(true);

            // when
            boolean result = adapter.existsByUserId(userId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("RefreshToken이 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenTokenNotExists() {
            // given
            UserId userId = UserId.of(UUID.randomUUID());

            given(queryDslRepository.existsByUserId(userId.value())).willReturn(false);

            // when
            boolean result = adapter.existsByUserId(userId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findUserIdByToken 메서드")
    class FindUserIdByTokenTest {

        @Test
        @DisplayName("RefreshToken으로 UserId를 조회한다")
        void shouldFindUserIdByToken() {
            // given
            String tokenValue = "stored-refresh-token-value";
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.of(1L, USER_UUID, tokenValue, FIXED_TIME, FIXED_TIME);

            given(queryDslRepository.findByToken(tokenValue)).willReturn(Optional.of(entity));

            // when
            Optional<UserId> result = adapter.findUserIdByToken(tokenValue);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(USER_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 Token으로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenUserNotFoundByToken() {
            // given
            String invalidToken = "invalid-token";

            given(queryDslRepository.findByToken(invalidToken)).willReturn(Optional.empty());

            // when
            Optional<UserId> result = adapter.findUserIdByToken(invalidToken);

            // then
            assertThat(result).isEmpty();
        }
    }
}
