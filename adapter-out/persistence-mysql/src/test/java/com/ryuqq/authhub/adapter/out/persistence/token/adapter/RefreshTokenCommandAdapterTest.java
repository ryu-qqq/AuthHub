package com.ryuqq.authhub.adapter.out.persistence.token.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.adapter.out.persistence.token.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.token.fixture.RefreshTokenJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.RefreshTokenJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.RefreshTokenQueryDslRepository;
import com.ryuqq.authhub.domain.common.util.ClockHolder;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 담당
 *   <li>Repository/ClockHolder/UuidHolder를 Mock으로 대체
 *   <li>persist는 기존 토큰 존재 여부에 따라 다른 흐름
 *   <li>deleteByUserId는 JpaRepository에 위임
 * </ul>
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

    @Mock private UuidHolder uuidHolder;

    private RefreshTokenCommandAdapter sut;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        sut =
                new RefreshTokenCommandAdapter(
                        jpaRepository, queryDslRepository, clockHolder, uuidHolder);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: 기존 토큰이 없으면 신규 저장")
        void shouldCreateNew_WhenNoExistingToken() {
            // given
            UserId userId = UserFixture.defaultId();
            String refreshToken = "new_refresh_token";
            UUID newRefreshTokenId = UUID.randomUUID();
            Clock clock = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);

            given(clockHolder.clock()).willReturn(clock);
            given(queryDslRepository.findByUserId(UUID.fromString(userId.value())))
                    .willReturn(Optional.empty());
            given(uuidHolder.random()).willReturn(newRefreshTokenId);

            // when
            sut.persist(userId, refreshToken);

            // then
            then(jpaRepository).should().save(any(RefreshTokenJpaEntity.class));
        }

        @Test
        @DisplayName("성공: 기존 토큰이 있으면 업데이트")
        void shouldUpdate_WhenExistingTokenFound() {
            // given
            UserId userId = UserFixture.defaultId();
            String newRefreshToken = "updated_refresh_token";
            RefreshTokenJpaEntity existingEntity = RefreshTokenJpaEntityFixture.create();
            Clock clock = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);

            given(clockHolder.clock()).willReturn(clock);
            given(queryDslRepository.findByUserId(UUID.fromString(userId.value())))
                    .willReturn(Optional.of(existingEntity));

            // when
            sut.persist(userId, newRefreshToken);

            // then
            then(jpaRepository).should(never()).save(any(RefreshTokenJpaEntity.class));
        }

        @Test
        @DisplayName("ClockHolder에서 현재 시각 획득")
        void shouldGetCurrentTime_FromClockHolder() {
            // given
            UserId userId = UserFixture.defaultId();
            String refreshToken = "test_token";
            UUID newRefreshTokenId = UUID.randomUUID();
            Clock clock = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);

            given(clockHolder.clock()).willReturn(clock);
            given(queryDslRepository.findByUserId(UUID.fromString(userId.value())))
                    .willReturn(Optional.empty());
            given(uuidHolder.random()).willReturn(newRefreshTokenId);

            // when
            sut.persist(userId, refreshToken);

            // then
            then(clockHolder).should().clock();
        }

        @Test
        @DisplayName("신규 저장 시 UuidHolder에서 UUID 생성")
        void shouldGenerateUuid_FromUuidHolder_WhenCreatingNew() {
            // given
            UserId userId = UserFixture.defaultId();
            String refreshToken = "test_token";
            UUID newRefreshTokenId = UUID.randomUUID();
            Clock clock = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);

            given(clockHolder.clock()).willReturn(clock);
            given(queryDslRepository.findByUserId(UUID.fromString(userId.value())))
                    .willReturn(Optional.empty());
            given(uuidHolder.random()).willReturn(newRefreshTokenId);

            // when
            sut.persist(userId, refreshToken);

            // then
            then(uuidHolder).should().random();
        }

        @Test
        @DisplayName("업데이트 시 UuidHolder 호출하지 않음")
        void shouldNotCallUuidHolder_WhenUpdating() {
            // given
            UserId userId = UserFixture.defaultId();
            String newRefreshToken = "updated_refresh_token";
            RefreshTokenJpaEntity existingEntity = RefreshTokenJpaEntityFixture.create();
            Clock clock = Clock.fixed(FIXED_TIME, ZoneOffset.UTC);

            given(clockHolder.clock()).willReturn(clock);
            given(queryDslRepository.findByUserId(UUID.fromString(userId.value())))
                    .willReturn(Optional.of(existingEntity));

            // when
            sut.persist(userId, newRefreshToken);

            // then
            then(uuidHolder).should(never()).random();
        }
    }

    @Nested
    @DisplayName("deleteByUserId 메서드")
    class DeleteByUserId {

        @Test
        @DisplayName("성공: JpaRepository에 삭제 위임")
        void shouldDelegateDelete_ToJpaRepository() {
            // given
            UserId userId = UserFixture.defaultId();

            // when
            sut.deleteByUserId(userId);

            // then
            then(jpaRepository).should().deleteByUserId(UUID.fromString(userId.value()));
        }

        @Test
        @DisplayName("UserId를 UUID로 변환하여 전달")
        void shouldConvertUserIdToUuid() {
            // given
            String userIdString = "01941234-5678-7000-8000-123456789abc";
            UserId userId = UserId.of(userIdString);

            // when
            sut.deleteByUserId(userId);

            // then
            then(jpaRepository).should().deleteByUserId(UUID.fromString(userIdString));
        }
    }
}
