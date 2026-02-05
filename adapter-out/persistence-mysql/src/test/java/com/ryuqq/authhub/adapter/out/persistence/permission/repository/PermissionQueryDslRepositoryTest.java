package com.ryuqq.authhub.adapter.out.persistence.permission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.permission.condition.PermissionConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.fixture.PermissionJpaEntityFixture;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import java.util.List;
import java.util.Optional;
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
 * PermissionQueryDslRepository 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>JPAQueryFactory + JPAQuery 체인 Mock
 *   <li>PermissionConditionBuilder는 실제 사용 (순수 로직)
 *   <li>Repository가 conditionBuilder를 올바르게 호출하는지 검증
 *   <li>notDeleted 조건 포함 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PermissionQueryDslRepository 단위 테스트")
class PermissionQueryDslRepositoryTest {

    @Mock private JPAQueryFactory queryFactory;

    @Mock private JPAQuery<PermissionJpaEntity> selectFromQuery;

    @Mock private JPAQuery<Integer> selectOneQuery;

    @Mock private JPAQuery<Long> countQuery;

    private PermissionConditionBuilder conditionBuilder;
    private PermissionQueryDslRepository sut;

    @BeforeEach
    void setUp() {
        conditionBuilder = new PermissionConditionBuilder();
        sut = new PermissionQueryDslRepository(queryFactory, conditionBuilder);
    }

    private void stubSelectFromChain(PermissionJpaEntity fetchOneResult) {
        given(queryFactory.selectFrom(permissionJpaEntity)).willReturn(selectFromQuery);
        given(selectFromQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                .willReturn(selectFromQuery);
        given(selectFromQuery.where(any(Predicate.class))).willReturn(selectFromQuery);
        given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
        given(selectFromQuery.offset(anyLong())).willReturn(selectFromQuery);
        given(selectFromQuery.limit(anyLong())).willReturn(selectFromQuery);
        given(selectFromQuery.fetchOne()).willReturn(fetchOneResult);
        given(selectFromQuery.fetch())
                .willReturn(fetchOneResult != null ? List.of(fetchOneResult) : List.of());
    }

    @Nested
    @DisplayName("findByPermissionId 메서드")
    class FindByPermissionId {

        @Test
        @DisplayName("성공: Entity가 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithEntity_WhenExists() {
            // given
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();
            stubSelectFromChain(entity);

            // when
            Optional<PermissionJpaEntity> result = sut.findByPermissionId(1L);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(entity);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotFound() {
            // given
            stubSelectFromChain(null);

            // when
            Optional<PermissionJpaEntity> result = sut.findByPermissionId(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByPermissionId 메서드")
    class ExistsByPermissionId {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(permissionJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result = sut.existsByPermissionId(1L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(permissionJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result = sut.existsByPermissionId(999L);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByServiceIdAndPermissionKey 메서드")
    class ExistsByServiceIdAndPermissionKey {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(permissionJpaEntity)).willReturn(selectOneQuery);
            given(
                            selectOneQuery.where(
                                    any(BooleanExpression.class),
                                    any(BooleanExpression.class),
                                    any(BooleanExpression.class)))
                    .willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result = sut.existsByServiceIdAndPermissionKey(1L, "user:read");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(permissionJpaEntity)).willReturn(selectOneQuery);
            given(
                            selectOneQuery.where(
                                    any(BooleanExpression.class),
                                    any(BooleanExpression.class),
                                    any(BooleanExpression.class)))
                    .willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result = sut.existsByServiceIdAndPermissionKey(1L, "nonexistent:key");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByServiceIdAndPermissionKey 메서드")
    class FindByServiceIdAndPermissionKey {

        @Test
        @DisplayName("성공: Entity가 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithEntity_WhenExists() {
            // given
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();
            given(queryFactory.selectFrom(permissionJpaEntity)).willReturn(selectFromQuery);
            given(
                            selectFromQuery.where(
                                    any(BooleanExpression.class),
                                    any(BooleanExpression.class),
                                    any(BooleanExpression.class)))
                    .willReturn(selectFromQuery);
            given(selectFromQuery.fetchOne()).willReturn(entity);

            // when
            Optional<PermissionJpaEntity> result =
                    sut.findByServiceIdAndPermissionKey(1L, "user:read");

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(entity);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotFound() {
            // given
            given(queryFactory.selectFrom(permissionJpaEntity)).willReturn(selectFromQuery);
            given(
                            selectFromQuery.where(
                                    any(BooleanExpression.class),
                                    any(BooleanExpression.class),
                                    any(BooleanExpression.class)))
                    .willReturn(selectFromQuery);
            given(selectFromQuery.fetchOne()).willReturn(null);

            // when
            Optional<PermissionJpaEntity> result =
                    sut.findByServiceIdAndPermissionKey(1L, "nonexistent:key");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteria {

        @Test
        @DisplayName("성공: 조건에 맞는 Entity 목록 반환")
        void shouldReturnEntityList_WhenCriteriaMatch() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 10);
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();
            stubSelectFromChain(entity);

            // when
            List<PermissionJpaEntity> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(entity);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 10);
            given(queryFactory.selectFrom(permissionJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(Predicate.class))).willReturn(selectFromQuery);
            given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
            given(selectFromQuery.offset(anyLong())).willReturn(selectFromQuery);
            given(selectFromQuery.limit(anyLong())).willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of());

            // when
            List<PermissionJpaEntity> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria 메서드")
    class CountByCriteria {

        @Test
        @DisplayName("성공: 조건에 맞는 개수 반환")
        void shouldReturnCount_WhenCriteriaMatch() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 10);
            given(queryFactory.select(permissionJpaEntity.count())).willReturn(countQuery);
            given(countQuery.from(permissionJpaEntity)).willReturn(countQuery);
            given(countQuery.where(any(Predicate.class))).willReturn(countQuery);
            given(countQuery.fetchOne()).willReturn(5L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("결과가 null이면 0 반환")
        void shouldReturnZero_WhenFetchOneReturnsNull() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 10);
            given(queryFactory.select(permissionJpaEntity.count())).willReturn(countQuery);
            given(countQuery.from(permissionJpaEntity)).willReturn(countQuery);
            given(countQuery.where(any(Predicate.class))).willReturn(countQuery);
            given(countQuery.fetchOne()).willReturn(null);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("findAllByIds 메서드")
    class FindAllByIds {

        @Test
        @DisplayName("성공: ID 목록으로 Entity 목록 반환")
        void shouldReturnEntityList_ByIds() {
            // given
            List<Long> ids = List.of(1L, 2L);
            PermissionJpaEntity entity1 = PermissionJpaEntityFixture.createWithId(1L);
            PermissionJpaEntity entity2 = PermissionJpaEntityFixture.createWithId(2L);
            given(queryFactory.selectFrom(permissionJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of(entity1, entity2));

            // when
            List<PermissionJpaEntity> result = sut.findAllByIds(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            given(queryFactory.selectFrom(permissionJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of());

            // when
            List<PermissionJpaEntity> result = sut.findAllByIds(List.of(999L));

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByPermissionKeys 메서드")
    class FindAllByPermissionKeys {

        @Test
        @DisplayName("성공: 권한 키 목록으로 Entity 목록 반환")
        void shouldReturnEntityList_ByPermissionKeys() {
            // given
            List<String> keys = List.of("user:read", "user:write");
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();
            given(queryFactory.selectFrom(permissionJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of(entity));

            // when
            List<PermissionJpaEntity> result = sut.findAllByPermissionKeys(keys);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            given(queryFactory.selectFrom(permissionJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of());

            // when
            List<PermissionJpaEntity> result =
                    sut.findAllByPermissionKeys(List.of("nonexistent:key"));

            // then
            assertThat(result).isEmpty();
        }
    }
}
