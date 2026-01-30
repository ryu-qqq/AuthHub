package com.ryuqq.authhub.integration.repository.token;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.token.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.RefreshTokenJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.RefreshTokenQueryDslRepository;
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
 * RefreshToken QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 RefreshToken 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>findByUserId - 사용자 ID로 단건 조회
 *   <li>existsByUserId - 사용자 ID 존재 여부
 *   <li>findByToken - 토큰 문자열로 단건 조회
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.AUTH)
@DisplayName("리프레시 토큰 QueryDSL Repository 테스트")
class RefreshTokenQueryDslRepositoryTest extends RepositoryTestBase {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");

    @Autowired private RefreshTokenJpaRepository jpaRepository;
    @Autowired private RefreshTokenQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("findByUserId 테스트")
    class FindByUserIdTest {

        @Test
        @DisplayName("사용자 ID로 리프레시 토큰 조회 성공")
        void shouldFindByUserId() {
            // given
            UUID refreshTokenId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            RefreshTokenJpaEntity entity =
                    RefreshTokenJpaEntity.forNew(
                            refreshTokenId, userId, "refresh-token-value", FIXED_TIME);
            jpaRepository.save(entity);
            flushAndClear();

            // when
            Optional<RefreshTokenJpaEntity> found = queryDslRepository.findByUserId(userId);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getRefreshTokenId()).isEqualTo(refreshTokenId);
            assertThat(found.get().getUserId()).isEqualTo(userId);
            assertThat(found.get().getToken()).isEqualTo("refresh-token-value");
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<RefreshTokenJpaEntity> found =
                    queryDslRepository.findByUserId(UUID.randomUUID());

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUserId 테스트")
    class ExistsByUserIdTest {

        @Test
        @DisplayName("리프레시 토큰 존재 확인 - true")
        void shouldReturnTrueWhenExists() {
            // given
            UUID userId = UUID.randomUUID();
            jpaRepository.save(
                    RefreshTokenJpaEntity.forNew(
                            UUID.randomUUID(), userId, "token-exists", FIXED_TIME));
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByUserId(userId);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("리프레시 토큰 미존재 - false")
        void shouldReturnFalseWhenNotExists() {
            // when
            boolean exists = queryDslRepository.existsByUserId(UUID.randomUUID());

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByToken 테스트")
    class FindByTokenTest {

        @Test
        @DisplayName("토큰 문자열로 리프레시 토큰 조회 성공")
        void shouldFindByToken() {
            // given
            UUID refreshTokenId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            String tokenValue = "jwt-refresh-token-abc123";
            jpaRepository.save(
                    RefreshTokenJpaEntity.forNew(refreshTokenId, userId, tokenValue, FIXED_TIME));
            flushAndClear();

            // when
            Optional<RefreshTokenJpaEntity> found = queryDslRepository.findByToken(tokenValue);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getRefreshTokenId()).isEqualTo(refreshTokenId);
            assertThat(found.get().getToken()).isEqualTo(tokenValue);
        }

        @Test
        @DisplayName("존재하지 않는 토큰으로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenTokenNotFound() {
            // when
            Optional<RefreshTokenJpaEntity> found =
                    queryDslRepository.findByToken("nonexistent-token");

            // then
            assertThat(found).isEmpty();
        }
    }
}
