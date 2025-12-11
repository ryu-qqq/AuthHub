package com.ryuqq.authhub.adapter.out.persistence.auth.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.auth.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.auth.repository.RefreshTokenJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.auth.repository.RefreshTokenQueryDslRepository;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
 * RefreshTokenCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenCommandAdapter 단위 테스트")
class RefreshTokenCommandAdapterTest {

    @Mock private RefreshTokenJpaRepository jpaRepository;

    @Mock private RefreshTokenQueryDslRepository queryDslRepository;

    @Mock private ClockHolder clockHolder;

    private RefreshTokenCommandAdapter adapter;

    private static final UUID USER_UUID = UserFixture.defaultUUID();
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Clock FIXED_CLOCK = Clock.fixed(FIXED_INSTANT, ZoneId.of("UTC"));
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenCommandAdapter(jpaRepository, queryDslRepository, clockHolder);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("신규 RefreshToken을 저장한다")
        void shouldPersistNewRefreshTokenSuccessfully() {
            // given
            UserId userId = UserId.of(USER_UUID);
            String refreshToken = "new-refresh-token-value";

            given(clockHolder.clock()).willReturn(FIXED_CLOCK);
            given(queryDslRepository.findByUserId(USER_UUID)).willReturn(Optional.empty());

            // when
            adapter.persist(userId, refreshToken);

            // then
            verify(jpaRepository).save(any(RefreshTokenJpaEntity.class));
        }

        @Test
        @DisplayName("기존 RefreshToken을 갱신한다")
        void shouldUpdateExistingRefreshToken() {
            // given
            UserId userId = UserId.of(USER_UUID);
            String newToken = "updated-refresh-token-value";
            RefreshTokenJpaEntity existingEntity =
                    RefreshTokenJpaEntity.of(1L, USER_UUID, "old-token", FIXED_TIME, FIXED_TIME);

            given(clockHolder.clock()).willReturn(FIXED_CLOCK);
            given(queryDslRepository.findByUserId(USER_UUID))
                    .willReturn(Optional.of(existingEntity));

            // when
            adapter.persist(userId, newToken);

            // then
            verify(jpaRepository, never()).save(any(RefreshTokenJpaEntity.class));
        }
    }

    @Nested
    @DisplayName("deleteByUserId 메서드")
    class DeleteByUserIdTest {

        @Test
        @DisplayName("UserId로 RefreshToken을 삭제한다")
        void shouldDeleteRefreshTokenByUserId() {
            // given
            UserId userId = UserId.of(USER_UUID);

            // when
            adapter.deleteByUserId(userId);

            // then
            verify(jpaRepository).deleteByUserId(USER_UUID);
        }
    }
}
