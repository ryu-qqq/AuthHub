package com.ryuqq.authhub.adapter.out.persistence.service.condition;

import static com.ryuqq.authhub.adapter.out.persistence.service.entity.QServiceJpaEntity.serviceJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceSearchField;
import com.ryuqq.authhub.domain.service.vo.ServiceSortKey;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceConditionBuilder 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>ConditionBuilder는 순수 조건 생성 로직 → Mock 불필요
 *   <li>각 조건 메서드가 올바른 QueryDSL Expression 생성 검증
 *   <li>null-safe 처리 검증
 *   <li>복합 조건 조합 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceConditionBuilder 단위 테스트")
class ServiceConditionBuilderTest {

    private ServiceConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: 검색 조건이 모두 포함된 BooleanBuilder 생성")
        void shouldBuildCondition_WithAllFilters() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "STORE",
                            ServiceSearchField.SERVICE_CODE,
                            List.of(ServiceStatus.ACTIVE),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("검색어가 없으면 해당 조건 제외")
        void shouldExcludeSearchCondition_WhenSearchWordIsNull() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 없으면 해당 조건 제외")
        void shouldExcludeStatusCondition_WhenStatusesIsEmpty() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "STORE",
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("날짜 범위가 없으면 해당 조건 제외")
        void shouldExcludeDateCondition_WhenDateRangeIsNull() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "STORE",
                            ServiceSearchField.SERVICE_CODE,
                            List.of(ServiceStatus.ACTIVE),
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("buildOrderSpecifier 메서드")
    class BuildOrderSpecifier {

        @Test
        @DisplayName("CREATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtAsc() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(serviceJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(serviceJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("UPDATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtAsc() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(serviceJpaEntity.updatedAt.asc());
        }

        @Test
        @DisplayName("NAME DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithNameDesc() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.NAME,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(serviceJpaEntity.name.desc());
        }

        @Test
        @DisplayName("sortKey가 기본값일 때 CREATED_AT 정렬 사용")
        void shouldUseCreatedAtSort_WhenDefaultSortKey() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(serviceJpaEntity.createdAt.asc());
        }
    }

    @Nested
    @DisplayName("searchByField 메서드")
    class SearchByField {

        @Test
        @DisplayName("SERVICE_CODE 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithServiceCodeField() {
            // when
            BooleanExpression result = sut.searchByField(ServiceSearchField.SERVICE_CODE, "STORE");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(serviceJpaEntity.serviceCode.containsIgnoreCase("STORE"));
        }

        @Test
        @DisplayName("NAME 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithNameField() {
            // when
            BooleanExpression result = sut.searchByField(ServiceSearchField.NAME, "자사몰");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(serviceJpaEntity.name.containsIgnoreCase("자사몰"));
        }

        @Test
        @DisplayName("searchField가 null이면 null 반환")
        void shouldReturnNull_WhenSearchFieldIsNull() {
            // when
            BooleanExpression result = sut.searchByField(null, "STORE");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchWord가 null이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsNull() {
            // when
            BooleanExpression result = sut.searchByField(ServiceSearchField.SERVICE_CODE, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchWord가 빈 문자열이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsBlank() {
            // when
            BooleanExpression result = sut.searchByField(ServiceSearchField.SERVICE_CODE, "   ");

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("조건 메서드 테스트")
    class ConditionMethods {

        @Test
        @DisplayName("serviceIdEquals: Long ID 일치 조건 생성")
        void shouldCreateServiceIdEqualsCondition() {
            // when
            BooleanExpression result = sut.serviceIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(serviceJpaEntity.serviceId.eq(1L));
        }

        @Test
        @DisplayName("serviceIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenServiceIdIsNull() {
            // when
            BooleanExpression result = sut.serviceIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("serviceCodeEquals: 서비스 코드 일치 조건 생성")
        void shouldCreateServiceCodeEqualsCondition() {
            // when
            BooleanExpression result = sut.serviceCodeEquals("SVC_STORE");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(serviceJpaEntity.serviceCode.eq("SVC_STORE"));
        }

        @Test
        @DisplayName("serviceCodeEquals: null이면 null 반환")
        void shouldReturnNull_WhenServiceCodeIsNull() {
            // when
            BooleanExpression result = sut.serviceCodeEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("statusActive: 활성 상태 조건 생성")
        void shouldCreateStatusActiveCondition() {
            // when
            BooleanExpression result = sut.statusActive();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(serviceJpaEntity.status.eq(ServiceStatus.ACTIVE));
        }

        @Test
        @DisplayName("createdAtGoe: 생성일시 이상 조건 생성")
        void shouldCreateCreatedAtGoeCondition() {
            // given
            Instant startInstant = Instant.parse("2025-01-01T00:00:00Z");

            // when
            BooleanExpression result = sut.createdAtGoe(startInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(serviceJpaEntity.createdAt.goe(startInstant));
        }

        @Test
        @DisplayName("createdAtGoe: null이면 null 반환")
        void shouldReturnNull_WhenStartInstantIsNull() {
            // when
            BooleanExpression result = sut.createdAtGoe(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("createdAtLoe: 생성일시 이하 조건 생성")
        void shouldCreateCreatedAtLoeCondition() {
            // given
            Instant endInstant = Instant.parse("2025-12-31T23:59:59Z");

            // when
            BooleanExpression result = sut.createdAtLoe(endInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(serviceJpaEntity.createdAt.loe(endInstant));
        }

        @Test
        @DisplayName("createdAtLoe: null이면 null 반환")
        void shouldReturnNull_WhenEndInstantIsNull() {
            // when
            BooleanExpression result = sut.createdAtLoe(null);

            // then
            assertThat(result).isNull();
        }
    }
}
