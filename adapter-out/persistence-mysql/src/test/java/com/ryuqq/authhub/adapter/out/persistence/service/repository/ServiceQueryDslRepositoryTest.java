package com.ryuqq.authhub.adapter.out.persistence.service.repository;

import static com.ryuqq.authhub.adapter.out.persistence.service.entity.QServiceJpaEntity.serviceJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.service.condition.ServiceConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.service.fixture.ServiceJpaEntityFixture;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceSearchField;
import com.ryuqq.authhub.domain.service.vo.ServiceSortKey;
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
 * ServiceQueryDslRepository 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>JPAQueryFactory + JPAQuery 체인 Mock
 *   <li>ServiceConditionBuilder는 실제 사용 (순수 로직)
 *   <li>Repository가 conditionBuilder를 올바르게 호출하는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ServiceQueryDslRepository 단위 테스트")
class ServiceQueryDslRepositoryTest {

    @Mock private JPAQueryFactory queryFactory;

    @Mock private JPAQuery<ServiceJpaEntity> selectFromQuery;

    @Mock private JPAQuery<Integer> selectOneQuery;

    @Mock private JPAQuery<Long> countQuery;

    private ServiceConditionBuilder conditionBuilder;
    private ServiceQueryDslRepository sut;

    @BeforeEach
    void setUp() {
        conditionBuilder = new ServiceConditionBuilder();
        sut = new ServiceQueryDslRepository(queryFactory, conditionBuilder);
    }

    private void stubSelectFromChain(ServiceJpaEntity fetchOneResult) {
        given(queryFactory.selectFrom(serviceJpaEntity)).willReturn(selectFromQuery);
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
    @DisplayName("findByServiceId 메서드")
    class FindByServiceId {

        @Test
        @DisplayName("성공: Entity가 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithEntity_WhenExists() {
            // given
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);
            stubSelectFromChain(entity);

            // when
            Optional<ServiceJpaEntity> result = sut.findByServiceId(1L);

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
            Optional<ServiceJpaEntity> result = sut.findByServiceId(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByServiceId 메서드")
    class ExistsByServiceId {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(serviceJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result = sut.existsByServiceId(1L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(serviceJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result = sut.existsByServiceId(999L);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByServiceCode 메서드")
    class FindByServiceCode {

        @Test
        @DisplayName("성공: Entity가 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithEntity_WhenExists() {
            // given
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithCode("SVC_STORE");
            given(queryFactory.selectFrom(serviceJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class))).willReturn(selectFromQuery);
            given(selectFromQuery.fetchOne()).willReturn(entity);

            // when
            Optional<ServiceJpaEntity> result = sut.findByServiceCode("SVC_STORE");

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(entity);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotFound() {
            // given
            given(queryFactory.selectFrom(serviceJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class))).willReturn(selectFromQuery);
            given(selectFromQuery.fetchOne()).willReturn(null);

            // when
            Optional<ServiceJpaEntity> result = sut.findByServiceCode("NONEXISTENT");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByServiceCode 메서드")
    class ExistsByServiceCode {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(serviceJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(1);

            // when
            boolean result = sut.existsByServiceCode("SVC_STORE");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            given(queryFactory.selectOne()).willReturn(selectOneQuery);
            given(selectOneQuery.from(serviceJpaEntity)).willReturn(selectOneQuery);
            given(selectOneQuery.where(any(BooleanExpression.class))).willReturn(selectOneQuery);
            given(selectOneQuery.fetchFirst()).willReturn(null);

            // when
            boolean result = sut.existsByServiceCode("NONEXISTENT");

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
            ServiceSearchCriteria criteria = createTestCriteria();
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);
            stubSelectFromChain(entity);

            // when
            List<ServiceJpaEntity> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(entity);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            ServiceSearchCriteria criteria = createTestCriteria();
            given(queryFactory.selectFrom(serviceJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(Predicate.class))).willReturn(selectFromQuery);
            given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
            given(selectFromQuery.offset(anyLong())).willReturn(selectFromQuery);
            given(selectFromQuery.limit(anyLong())).willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of());

            // when
            List<ServiceJpaEntity> result = sut.findAllByCriteria(criteria);

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
            ServiceSearchCriteria criteria = createTestCriteria();
            given(queryFactory.select(serviceJpaEntity.count())).willReturn(countQuery);
            given(countQuery.from(serviceJpaEntity)).willReturn(countQuery);
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
            ServiceSearchCriteria criteria = createTestCriteria();
            given(queryFactory.select(serviceJpaEntity.count())).willReturn(countQuery);
            given(countQuery.from(serviceJpaEntity)).willReturn(countQuery);
            given(countQuery.where(any(Predicate.class))).willReturn(countQuery);
            given(countQuery.fetchOne()).willReturn(null);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("findAllActive 메서드")
    class FindAllActive {

        @Test
        @DisplayName("성공: 활성 상태 Entity 목록 반환")
        void shouldReturnEntityList_WhenActiveExists() {
            // given
            ServiceJpaEntity entity = ServiceJpaEntityFixture.createWithId(1L);
            given(queryFactory.selectFrom(serviceJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class))).willReturn(selectFromQuery);
            given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of(entity));

            // when
            List<ServiceJpaEntity> result = sut.findAllActive();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(entity);
        }

        @Test
        @DisplayName("활성 상태 서비스가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoActiveServices() {
            // given
            given(queryFactory.selectFrom(serviceJpaEntity)).willReturn(selectFromQuery);
            given(selectFromQuery.where(any(BooleanExpression.class))).willReturn(selectFromQuery);
            given(selectFromQuery.orderBy(any(OrderSpecifier.class))).willReturn(selectFromQuery);
            given(selectFromQuery.fetch()).willReturn(List.of());

            // when
            List<ServiceJpaEntity> result = sut.findAllActive();

            // then
            assertThat(result).isEmpty();
        }
    }

    private ServiceSearchCriteria createTestCriteria() {
        return ServiceSearchCriteria.of(
                null,
                ServiceSearchField.SERVICE_CODE,
                null,
                DateRange.of(null, null),
                ServiceSortKey.CREATED_AT,
                SortDirection.ASC,
                PageRequest.of(0, 10));
    }
}
