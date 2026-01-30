package com.ryuqq.authhub.adapter.out.persistence.token.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.token.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.token.fixture.RefreshTokenJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.RefreshTokenQueryDslRepository;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.id.UserId;
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
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 담당
 *   <li>QueryDslRepository를 Mock으로 대체
 *   <li>Entity → String/UserId 변환 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenQueryAdapter 단위 테스트")
class RefreshTokenQueryAdapterTest {

    @Mock private RefreshTokenQueryDslRepository repository;

    private RefreshTokenQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new RefreshTokenQueryAdapter(repository);
    }

    @Nested
    @DisplayName("findByUserId 메서드")
    class FindByUserId {

        @Test
        @DisplayName("성공: Entity 조회 후 토큰 문자열 반환")
        void shouldFindAndReturnToken_WhenEntityExists() {
            // given
            UserId userId = UserFixture.defaultId();
            RefreshTokenJpaEntity entity = RefreshTokenJpaEntityFixture.create();
            String expectedToken = entity.getToken();

            given(repository.findByUserId(UUID.fromString(userId.value())))
                    .willReturn(Optional.of(entity));

            // when
            Optional<String> result = sut.findByUserId(userId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedToken);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            UserId userId = UserFixture.defaultId();

            given(repository.findByUserId(UUID.fromString(userId.value())))
                    .willReturn(Optional.empty());

            // when
            Optional<String> result = sut.findByUserId(userId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("UserId를 UUID로 변환하여 Repository 호출")
        void shouldConvertUserIdToUuid_AndCallRepository() {
            // given
            UserId userId = UserFixture.defaultId();

            given(repository.findByUserId(UUID.fromString(userId.value())))
                    .willReturn(Optional.empty());

            // when
            sut.findByUserId(userId);

            // then
            then(repository).should().findByUserId(UUID.fromString(userId.value()));
        }
    }

    @Nested
    @DisplayName("existsByUserId 메서드")
    class ExistsByUserId {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            UserId userId = UserFixture.defaultId();

            given(repository.existsByUserId(UUID.fromString(userId.value()))).willReturn(true);

            // when
            boolean result = sut.existsByUserId(userId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            UserId userId = UserFixture.defaultId();

            given(repository.existsByUserId(UUID.fromString(userId.value()))).willReturn(false);

            // when
            boolean result = sut.existsByUserId(userId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findUserIdByToken 메서드")
    class FindUserIdByToken {

        @Test
        @DisplayName("성공: Entity 조회 후 UserId 반환")
        void shouldFindAndReturnUserId_WhenEntityExists() {
            // given
            String refreshToken = RefreshTokenJpaEntityFixture.defaultToken();
            RefreshTokenJpaEntity entity = RefreshTokenJpaEntityFixture.create();
            String expectedUserId = entity.getUserId().toString();

            given(repository.findByToken(refreshToken)).willReturn(Optional.of(entity));

            // when
            Optional<UserId> result = sut.findUserIdByToken(refreshToken);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().value()).isEqualTo(expectedUserId);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            String refreshToken = "nonexistent_token";

            given(repository.findByToken(refreshToken)).willReturn(Optional.empty());

            // when
            Optional<UserId> result = sut.findUserIdByToken(refreshToken);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Repository에 토큰 문자열 전달")
        void shouldPassToken_ToRepository() {
            // given
            String refreshToken = "test_token";

            given(repository.findByToken(refreshToken)).willReturn(Optional.empty());

            // when
            sut.findUserIdByToken(refreshToken);

            // then
            then(repository).should().findByToken(refreshToken);
        }
    }
}
