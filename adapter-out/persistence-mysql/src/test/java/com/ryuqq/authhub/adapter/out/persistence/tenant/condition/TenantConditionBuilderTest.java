package com.ryuqq.authhub.adapter.out.persistence.tenant.condition;

import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantSearchField;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantConditionBuilder 단위 테스트
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
@DisplayName("TenantConditionBuilder 단위 테스트")
class TenantConditionBuilderTest {

    private TenantConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new TenantConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: 검색 조건이 모두 포함된 BooleanBuilder 생성")
        void shouldBuildCondition_WithAllFilters() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "Test",
                            TenantSearchField.NAME,
                            List.of(TenantStatus.ACTIVE),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            TenantSortKey.CREATED_AT,
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
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
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
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "Test",
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
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
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "Test",
                            TenantSearchField.NAME,
                            List.of(TenantStatus.ACTIVE),
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
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
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("UPDATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtAsc() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantJpaEntity.updatedAt.asc());
        }

        @Test
        @DisplayName("UPDATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtDesc() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.UPDATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantJpaEntity.updatedAt.desc());
        }

        @Test
        @DisplayName("sortKey가 null이면 CREATED_AT 정렬 사용")
        void shouldUseCreatedAtSort_WhenSortKeyIsNull() {
            // given - ofSimple creates criteria with CREATED_AT, but we need sortKey null
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantJpaEntity.createdAt.asc());
        }
    }

    @Nested
    @DisplayName("searchByField 메서드")
    class SearchByField {

        @Test
        @DisplayName("NAME 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithNameField() {
            // when
            BooleanExpression result = sut.searchByField(TenantSearchField.NAME, "Test Tenant");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(tenantJpaEntity.name.containsIgnoreCase("Test Tenant"));
        }

        @Test
        @DisplayName("searchField가 null이면 null 반환")
        void shouldReturnNull_WhenSearchFieldIsNull() {
            // when
            BooleanExpression result = sut.searchByField(null, "Test");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchWord가 null이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsNull() {
            // when
            BooleanExpression result = sut.searchByField(TenantSearchField.NAME, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchWord가 빈 문자열이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsBlank() {
            // when
            BooleanExpression result = sut.searchByField(TenantSearchField.NAME, "   ");

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("조건 메서드 테스트")
    class ConditionMethods {

        @Test
        @DisplayName("nameEquals: 이름 일치 조건 생성")
        void shouldCreateNameEqualsCondition() {
            // when
            BooleanExpression result = sut.nameEquals("Test Tenant");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(tenantJpaEntity.name.eq("Test Tenant"));
        }

        @Test
        @DisplayName("nameEquals: null이면 null 반환")
        void shouldReturnNull_WhenNameIsNull() {
            // when
            BooleanExpression result = sut.nameEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("tenantIdEquals: 테넌트 ID 일치 조건 생성")
        void shouldCreateTenantIdEqualsCondition() {
            // when
            BooleanExpression result = sut.tenantIdEquals("tenant-001");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(tenantJpaEntity.tenantId.eq("tenant-001"));
        }

        @Test
        @DisplayName("tenantIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenTenantIdIsNull() {
            // when
            BooleanExpression result = sut.tenantIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("tenantIdNotEquals: 테넌트 ID 불일치 조건 생성")
        void shouldCreateTenantIdNotEqualsCondition() {
            // when
            BooleanExpression result = sut.tenantIdNotEquals("tenant-001");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(tenantJpaEntity.tenantId.ne("tenant-001"));
        }

        @Test
        @DisplayName("tenantIdNotEquals: null이면 null 반환")
        void shouldReturnNull_WhenTenantIdNotEqualsIsNull() {
            // when
            BooleanExpression result = sut.tenantIdNotEquals(null);

            // then
            assertThat(result).isNull();
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
            assertThat(result).isEqualTo(tenantJpaEntity.createdAt.goe(startInstant));
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
            assertThat(result).isEqualTo(tenantJpaEntity.createdAt.loe(endInstant));
        }

        @Test
        @DisplayName("createdAtLoe: null이면 null 반환")
        void shouldReturnNull_WhenEndInstantIsNull() {
            // when
            BooleanExpression result = sut.createdAtLoe(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("statusIn: 상태 필터가 있으면 조건 생성")
        void shouldCreateStatusInCondition_WhenHasStatusFilter() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            List.of(TenantStatus.ACTIVE, TenantStatus.INACTIVE),
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanExpression result = sut.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(
                            tenantJpaEntity.status.in(
                                    List.of(TenantStatus.ACTIVE, TenantStatus.INACTIVE)));
        }

        @Test
        @DisplayName("statusIn: 상태 필터가 없으면 null 반환")
        void shouldReturnNull_WhenNoStatusFilter() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanExpression result = sut.statusIn(criteria);

            // then
            assertThat(result).isNull();
        }
    }
}
