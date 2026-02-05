package com.ryuqq.authhub.adapter.out.persistence.token.repository;

import static com.ryuqq.authhub.adapter.out.persistence.token.entity.QRefreshTokenJpaEntity.refreshTokenJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.token.entity.RefreshTokenJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.token.fixture.RefreshTokenJpaEntityFixture;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * RefreshTokenQueryDslRepository 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>JPAQueryFactory + JPAQuery 체인 Mock
 *   <li>ConditionBuilder 미사용 (단순 where 조건)
 *   <li>Repository 조회 로직 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RefreshTokenQueryDslRepository 단위 테스트")
class RefreshTokenQueryDslRepositoryTest {

    @Mock private JPAQueryFactory queryFactory;

    @Mock private JPAQuery<RefreshTokenJpaEntity> selectFromQuery;

    @Mock private JPAQuery<Integer> selectOneQuery;

    private RefreshTokenQueryDslRepository sut;

    @BeforeEach
    void setUp() {
        sut = new RefreshTokenQueryDslRepository(queryFactory);
    }

    private void stubSelectFromChain(RefreshTokenJpaEntity fetchOneResult) {
        given(queryFactory.selectFrom(refreshTokenJpaEntity)).willReturn(selectFromQuery);
        given(selectFromQuery.where(any(BooleanExpression.class))).willReturn(selectFromQuery);
        given(selectFromQuery.fetchOne()).willReturn(fetchOneResult);
    }

    @Nested
    @DisplayName("findByUserId 메서드")
    class FindByUserId {

        @Test
        @DisplayName("성공: Entity가 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithEntity_WhenExists() {
            // given
            UUID userId = RefreshTokenJpaEntityFixture.defaultUserId();
            RefreshTokenJpaEntity entity = RefreshTokenJpaEntityFixture.createWithUserId(userId);
            stubSelectFromChain(entity);

            // when
            Optional<RefreshTokenJpaEntity> result = sut.findByUserId(userId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(entity);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotFound() {
            // given
            UUID userId = RefreshTokenJpaEntityFixture.defaultUserId();
            stubSelectFromChain(null);

            // when
            Optional<RefreshTokenJpaEntity> result = sut.findByUserId(userId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByUserId 메서드")
    class ExistsByUserId {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            UUID userId = RefreshTokenJpaEntityFixture.defaultUserId();
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(refreshTokenJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result = sut.existsByUserId(userId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            UUID userId = RefreshTokenJpaEntityFixture.defaultUserId();
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(refreshTokenJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result = sut.existsByUserId(userId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByToken 메서드")
    class FindByToken {

        @Test
        @DisplayName("성공: Entity가 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithEntity_WhenExists() {
            // given
            String token = RefreshTokenJpaEntityFixture.defaultToken();
            RefreshTokenJpaEntity entity = RefreshTokenJpaEntityFixture.createWithToken(token);
            stubSelectFromChain(entity);

            // when
            Optional<RefreshTokenJpaEntity> result = sut.findByToken(token);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(entity);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotFound() {
            // given
            String token = "nonexistent-token-value";
            stubSelectFromChain(null);

            // when
            Optional<RefreshTokenJpaEntity> result = sut.findByToken(token);

            // then
            assertThat(result).isEmpty();
        }
    }
}
