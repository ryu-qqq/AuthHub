package com.ryuqq.authhub.integration.repository.token;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.token.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.RefreshTokenJpaRepository;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RefreshTokenJpaRepository 통합 테스트.
 *
 * <p>Repository 레이어의 CRUD 및 쿼리 기능을 검증합니다.
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.AUTH)
class RefreshTokenRepositoryIntegrationTest extends RepositoryTestBase {

    @Autowired private RefreshTokenJpaRepository refreshTokenJpaRepository;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        refreshTokenJpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("save 테스트")
    class SaveTest {

        @Test
        @DisplayName("리프레시 토큰을 저장할 수 있다")
        void shouldSaveRefreshToken() {
            // given
            UUID refreshTokenId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            String token = "refresh-token-value";
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(refreshTokenId, userId, token, FIXED_TIME);

            // when
            RefreshTokenJpaEntity saved = refreshTokenJpaRepository.save(entity);
            flushAndClear();

            // then
            assertThat(saved.getRefreshTokenId()).isEqualTo(refreshTokenId);

            Optional<RefreshTokenJpaEntity> found =
                    refreshTokenJpaRepository.findById(refreshTokenId);
            assertThat(found).isPresent();
            assertThat(found.get().getUserId()).isEqualTo(userId);
            assertThat(found.get().getToken()).isEqualTo(token);
        }
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 리프레시 토큰을 ID로 조회할 수 있다")
        void shouldFindExistingRefreshToken() {
            // given
            UUID refreshTokenId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(refreshTokenId, userId, "token-value", FIXED_TIME);
            refreshTokenJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<RefreshTokenJpaEntity> found =
                    refreshTokenJpaRepository.findById(refreshTokenId);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getToken()).isEqualTo("token-value");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // given
            UUID nonExistentId = UUID.randomUUID();

            // when
            Optional<RefreshTokenJpaEntity> found =
                    refreshTokenJpaRepository.findById(nonExistentId);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUserId 테스트")
    class FindByUserIdTest {

        @Test
        @DisplayName("사용자 ID로 리프레시 토큰을 조회할 수 있다")
        void shouldFindByUserId() {
            // given
            UUID refreshTokenId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(refreshTokenId, userId, "user-token", FIXED_TIME);
            refreshTokenJpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<RefreshTokenJpaEntity> found = refreshTokenJpaRepository.findByUserId(userId);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getRefreshTokenId()).isEqualTo(refreshTokenId);
        }
    }

    @Nested
    @DisplayName("delete 테스트")
    class DeleteTest {

        @Test
        @DisplayName("리프레시 토큰을 삭제할 수 있다")
        void shouldDeleteRefreshToken() {
            // given
            UUID refreshTokenId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(
                            refreshTokenId, userId, "token-to-delete", FIXED_TIME);
            refreshTokenJpaRepository.save(entity);
            flushAndClear();

            // when
            refreshTokenJpaRepository.deleteById(refreshTokenId);
            flushAndClear();

            // then
            Optional<RefreshTokenJpaEntity> found =
                    refreshTokenJpaRepository.findById(refreshTokenId);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("사용자 ID로 리프레시 토큰을 삭제할 수 있다")
        void shouldDeleteByUserId() {
            // given
            UUID refreshTokenId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(
                            refreshTokenId, userId, "token-to-delete", FIXED_TIME);
            refreshTokenJpaRepository.save(entity);
            flushAndClear();

            // when
            refreshTokenJpaRepository.deleteByUserId(userId);
            flushAndClear();

            // then
            Optional<RefreshTokenJpaEntity> found = refreshTokenJpaRepository.findByUserId(userId);
            assertThat(found).isEmpty();
        }
    }
}
