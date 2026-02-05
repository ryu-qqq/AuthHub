package com.ryuqq.authhub.adapter.out.persistence.tenant.repository;

import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.tenant.condition.TenantConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantSearchField;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
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
 * TenantQueryDslRepository 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>JPAQueryFactory + JPAQuery 체인 Mock
 *   <li>TenantConditionBuilder는 실제 사용 (순수 로직)
 *   <li>Repository가 conditionBuilder를 올바르게 호출하는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TenantQueryDslRepository 단위 테스트")
class TenantQueryDslRepositoryTest {

    @Mock private JPAQueryFactory queryFactory;

    @Mock private JPAQuery<TenantJpaEntity> selectFromQuery;

    @Mock private JPAQuery<Integer> selectOneQuery;

    @Mock private JPAQuery<Long> countQuery;

    private TenantConditionBuilder conditionBuilder;
    private TenantQueryDslRepository sut;

    @BeforeEach
    void setUp() {
        conditionBuilder = new TenantConditionBuilder();
        sut = new TenantQueryDslRepository(queryFactory, conditionBuilder);
    }

    private void stubSelectFromChain(TenantJpaEntity fetchOneResult) {
        given(queryFactory.selectFrom(tenantJpaEntity)).willReturn(selectFromQuery);
        given(selectFromQuery.where(any(BooleanExpression.class))).willReturn(selectFromQuery);
        given(selectFromQuery.where(any(Predicate.class))).willReturn(selectFromQuery);
        given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
        given(selectFromQuery.offset(anyLong())).willReturn(selectFromQuery);
        given(selectFromQuery.limit(anyLong())).willReturn(selectFromQuery);
        given(selectFromQuery.fetchOne()).willReturn(fetchOneResult);
        given(selectFromQuery.fetch())
                .willReturn(fetchOneResult != null ? List.of(fetchOneResult) : List.of());
    }

    @Nested
    @DisplayName("findByTenantId 메서드")
    class FindByTenantId {

        @Test
        @DisplayName("성공: Entity가 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithEntity_WhenExists() {
            // given
            TenantJpaEntity entity = TenantJpaEntityFixture.create();
            stubSelectFromChain(entity);

            // when
            Optional<TenantJpaEntity> result = sut.findByTenantId(entity.getTenantId());

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
            Optional<TenantJpaEntity> result = sut.findByTenantId("non-existent-id");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByName 메서드")
    class ExistsByName {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(tenantJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result = sut.existsByName("Test Tenant");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(tenantJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result = sut.existsByName("NonExistent Tenant");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantId 메서드")
    class ExistsByTenantId {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(tenantJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result = sut.existsByTenantId(TenantJpaEntityFixture.defaultTenantId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(tenantJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result = sut.existsByTenantId("non-existent-id");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByNameAndIdNot 메서드")
    class ExistsByNameAndIdNot {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given - existsByNameAndIdNot uses where(nameEquals, tenantIdNotEquals)
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(tenantJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result =
                    sut.existsByNameAndIdNot(
                            "Test Tenant", TenantJpaEntityFixture.defaultTenantId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given - existsByNameAndIdNot uses where(nameEquals, tenantIdNotEquals)
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(tenantJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class), any(BooleanExpression.class)))
                    .willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result =
                    sut.existsByNameAndIdNot(
                            "NonExistent Tenant", TenantJpaEntityFixture.defaultTenantId());

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteria {

        @Test
        @DisplayName("성공: 조건에 맞는 Entity 목록 반환")
        void shouldReturnEntityList_WhenCriteriaMatch() {
            // given
            TenantSearchCriteria criteria = createTestCriteria();
            TenantJpaEntity entity = TenantJpaEntityFixture.createWithName("Tenant 1");
            stubSelectFromChain(entity);

            // when
            List<TenantJpaEntity> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(entity);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            TenantSearchCriteria criteria = createTestCriteria();
            given(queryFactory.selectFrom(tenantJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(Predicate.class))).willReturn(selectFromQuery);
            given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
            given(selectFromQuery.offset(anyLong())).willReturn(selectFromQuery);
            given(selectFromQuery.limit(anyLong())).willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of());

            // when
            List<TenantJpaEntity> result = sut.findAllByCriteria(criteria);

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
            TenantSearchCriteria criteria = createTestCriteria();
            given(queryFactory.select(tenantJpaEntity.count())).willReturn(countQuery);
            given(countQuery.from(tenantJpaEntity)).willReturn(countQuery);
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
            TenantSearchCriteria criteria = createTestCriteria();
            given(queryFactory.select(tenantJpaEntity.count())).willReturn(countQuery);
            given(countQuery.from(tenantJpaEntity)).willReturn(countQuery);
            given(countQuery.where(any(Predicate.class))).willReturn(countQuery);
            given(countQuery.fetchOne()).willReturn(null);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    private TenantSearchCriteria createTestCriteria() {
        return TenantSearchCriteria.of(
                null,
                TenantSearchField.NAME,
                null,
                DateRange.of(null, null),
                TenantSortKey.CREATED_AT,
                SortDirection.ASC,
                PageRequest.of(0, 10));
    }
}
