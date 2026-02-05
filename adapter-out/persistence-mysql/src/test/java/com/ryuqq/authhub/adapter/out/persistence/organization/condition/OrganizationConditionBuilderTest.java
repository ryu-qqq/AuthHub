package com.ryuqq.authhub.adapter.out.persistence.organization.condition;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSearchField;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSortKey;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationConditionBuilder 단위 테스트
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
@DisplayName("OrganizationConditionBuilder 단위 테스트")
class OrganizationConditionBuilderTest {

    private OrganizationConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new OrganizationConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: 검색 조건이 모두 포함된 BooleanBuilder 생성")
        void shouldBuildCondition_WithAllFilters() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TenantId.forNew("01941234-5678-7000-8000-123456789abc")),
                            "searchWord",
                            OrganizationSearchField.NAME,
                            List.of(OrganizationStatus.ACTIVE),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("테넌트 ID 목록이 null이면 해당 조건 제외")
        void shouldExcludeTenantIdsCondition_WhenTenantIdsIsNull() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            "searchWord",
                            OrganizationSearchField.NAME,
                            List.of(OrganizationStatus.ACTIVE),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("테넌트 ID 목록이 비어있으면 해당 조건 제외")
        void shouldExcludeTenantIdsCondition_WhenTenantIdsIsEmpty() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(),
                            "searchWord",
                            OrganizationSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.NAME,
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
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TenantId.forNew("01941234-5678-7000-8000-123456789abc")),
                            "searchWord",
                            OrganizationSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
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
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TenantId.forNew("01941234-5678-7000-8000-123456789abc")),
                            "searchWord",
                            OrganizationSearchField.NAME,
                            List.of(OrganizationStatus.ACTIVE),
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
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
        @DisplayName("NAME ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithNameAsc() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.NAME,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.name.asc());
        }

        @Test
        @DisplayName("NAME DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithNameDesc() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.NAME,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.name.desc());
        }

        @Test
        @DisplayName("STATUS ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithStatusAsc() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.STATUS,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.status.asc());
        }

        @Test
        @DisplayName("STATUS DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithStatusDesc() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.STATUS,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.status.desc());
        }

        @Test
        @DisplayName("CREATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtAsc() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("UPDATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtAsc() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.updatedAt.asc());
        }

        @Test
        @DisplayName("UPDATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtDesc() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.UPDATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.updatedAt.desc());
        }

        @Test
        @DisplayName("기본 정렬 CREATED_AT ASC 반환")
        void shouldUseCreatedAtAsc_AsDefaultSort() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(organizationJpaEntity.createdAt.asc());
        }
    }

    @Nested
    @DisplayName("조건 메서드 테스트")
    class ConditionMethods {

        @Test
        @DisplayName("tenantIdsIn: 테넌트 ID 목록 포함 조건 생성")
        void shouldCreateTenantIdsInCondition() {
            // given
            List<TenantId> tenantIds = List.of(TenantId.forNew("tid-1"), TenantId.forNew("tid-2"));

            // when
            BooleanExpression result = sut.tenantIdsIn(tenantIds);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(organizationJpaEntity.tenantId.in("tid-1", "tid-2"));
        }

        @Test
        @DisplayName("tenantIdsIn: null이면 null 반환")
        void shouldReturnNull_WhenTenantIdsIsNull() {
            // when
            BooleanExpression result = sut.tenantIdsIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("tenantIdsIn: 빈 목록이면 null 반환")
        void shouldReturnNull_WhenTenantIdsIsEmpty() {
            // when
            BooleanExpression result = sut.tenantIdsIn(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchByField: NAME 검색 조건 생성")
        void shouldCreateSearchByFieldCondition_WithName() {
            // when
            BooleanExpression result = sut.searchByField(OrganizationSearchField.NAME, "Acme");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(organizationJpaEntity.name.containsIgnoreCase("Acme"));
        }

        @Test
        @DisplayName("searchByField: searchField가 null이면 null 반환")
        void shouldReturnNull_WhenSearchFieldIsNull() {
            // when
            BooleanExpression result = sut.searchByField(null, "word");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchByField: searchWord가 null이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsNull() {
            // when
            BooleanExpression result = sut.searchByField(OrganizationSearchField.NAME, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchByField: searchWord가 빈 문자열이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsBlank() {
            // when
            BooleanExpression result = sut.searchByField(OrganizationSearchField.NAME, "   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("organizationIdEquals: 조직 ID 일치 조건 생성")
        void shouldCreateOrganizationIdEqualsCondition() {
            // when
            BooleanExpression result = sut.organizationIdEquals("org-123");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(organizationJpaEntity.organizationId.eq("org-123"));
        }

        @Test
        @DisplayName("organizationIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenOrganizationIdIsNull() {
            // when
            BooleanExpression result = sut.organizationIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("tenantIdEquals: 테넌트 ID 일치 조건 생성")
        void shouldCreateTenantIdEqualsCondition() {
            // when
            BooleanExpression result = sut.tenantIdEquals("01941234-5678-7000-8000-123456789abc");

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(
                            organizationJpaEntity.tenantId.eq(
                                    "01941234-5678-7000-8000-123456789abc"));
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
        @DisplayName("nameEquals: 이름 일치 조건 생성")
        void shouldCreateNameEqualsCondition() {
            // when
            BooleanExpression result = sut.nameEquals("My Org");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(organizationJpaEntity.name.eq("My Org"));
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
        @DisplayName("createdAtGoe: 생성일시 이상 조건 생성")
        void shouldCreateCreatedAtGoeCondition() {
            // given
            Instant startInstant = Instant.parse("2025-01-01T00:00:00Z");

            // when
            BooleanExpression result = sut.createdAtGoe(startInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(organizationJpaEntity.createdAt.goe(startInstant));
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
            assertThat(result).isEqualTo(organizationJpaEntity.createdAt.loe(endInstant));
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
