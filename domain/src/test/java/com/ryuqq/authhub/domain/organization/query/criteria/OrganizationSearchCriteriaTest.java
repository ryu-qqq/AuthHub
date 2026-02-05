package com.ryuqq.authhub.domain.organization.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSearchField;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSortKey;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationSearchCriteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("OrganizationSearchCriteria 테스트")
class OrganizationSearchCriteriaTest {

    private static final TenantId TENANT_ID_1 = TenantId.of("01941234-5678-7000-8000-123456789abc");
    private static final TenantId TENANT_ID_2 = TenantId.of("01941234-5678-7000-8000-123456789def");
    private static final DateRange DATE_RANGE =
            DateRange.of(LocalDate.now().minusDays(30), LocalDate.now());

    @Nested
    @DisplayName("OrganizationSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of()로 OrganizationSearchCriteria를 생성한다")
        void shouldCreateWithOf() {
            // given
            List<TenantId> tenantIds = List.of(TENANT_ID_1);
            String searchWord = "test";
            OrganizationSearchField searchField = OrganizationSearchField.NAME;
            List<OrganizationStatus> statuses = List.of(OrganizationStatus.ACTIVE);
            OrganizationSortKey sortKey = OrganizationSortKey.CREATED_AT;
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);

            // when
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            tenantIds,
                            searchWord,
                            searchField,
                            statuses,
                            DATE_RANGE,
                            sortKey,
                            sortDirection,
                            pageRequest);

            // then
            assertThat(criteria.tenantIds()).isEqualTo(tenantIds);
            assertThat(criteria.searchWord()).isEqualTo(searchWord);
            assertThat(criteria.searchField()).isEqualTo(searchField);
            assertThat(criteria.statuses()).isEqualTo(statuses);
            assertThat(criteria.dateRange()).isEqualTo(DATE_RANGE);
        }

        @Test
        @DisplayName("ofSimple()로 OrganizationSearchCriteria를 생성한다")
        void shouldCreateWithOfSimple() {
            // given
            List<TenantId> tenantIds = List.of(TENANT_ID_1);
            String searchWord = "test";
            List<OrganizationStatus> statuses = List.of(OrganizationStatus.ACTIVE);
            int pageNumber = 0;
            int pageSize = 20;

            // when
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.ofSimple(
                            tenantIds, searchWord, statuses, DATE_RANGE, pageNumber, pageSize);

            // then
            assertThat(criteria.tenantIds()).isEqualTo(tenantIds);
            assertThat(criteria.searchWord()).isEqualTo(searchWord);
            assertThat(criteria.statuses()).isEqualTo(statuses);
            assertThat(criteria.searchField()).isEqualTo(OrganizationSearchField.defaultField());
            assertThat(criteria.sortKey()).isEqualTo(OrganizationSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("null tenantIds로 생성할 수 있다")
        void shouldCreateWithNullTenantIds() {
            // when
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.tenantIds()).isNull();
        }

        @Test
        @DisplayName("null searchWord로 생성할 수 있다")
        void shouldCreateWithNullSearchWord() {
            // when
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            null,
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.searchWord()).isNull();
        }

        @Test
        @DisplayName("null statuses로 생성할 수 있다")
        void shouldCreateWithNullStatuses() {
            // when
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.statuses()).isNull();
        }
    }

    @Nested
    @DisplayName("OrganizationSearchCriteria 필터 확인 테스트")
    class FilterCheckTests {

        @Test
        @DisplayName("hasTenantFilter()는 tenantIds가 있으면 true를 반환한다")
        void hasTenantFilterShouldReturnTrueWhenTenantIdsExist() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasTenantFilter()).isTrue();
        }

        @Test
        @DisplayName("hasTenantFilter()는 tenantIds가 null이면 false를 반환한다")
        void hasTenantFilterShouldReturnFalseWhenTenantIdsIsNull() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            null,
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasTenantFilter()).isFalse();
        }

        @Test
        @DisplayName("hasTenantFilter()는 tenantIds가 빈 리스트이면 false를 반환한다")
        void hasTenantFilterShouldReturnFalseWhenTenantIdsIsEmpty() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasTenantFilter()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 있으면 true를 반환한다")
        void hasSearchWordShouldReturnTrueWhenSearchWordExists() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasSearchWord()).isTrue();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 null이면 false를 반환한다")
        void hasSearchWordShouldReturnFalseWhenSearchWordIsNull() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            null,
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 빈 값이면 false를 반환한다")
        void hasSearchWordShouldReturnFalseWhenSearchWordIsBlank() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "   ",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 있으면 true를 반환한다")
        void hasStatusFilterShouldReturnTrueWhenStatusesExist() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            List.of(OrganizationStatus.ACTIVE),
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 null이면 false를 반환한다")
        void hasStatusFilterShouldReturnFalseWhenStatusesIsNull() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 빈 리스트이면 false를 반환한다")
        void hasStatusFilterShouldReturnFalseWhenStatusesIsEmpty() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            List.of(),
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 있으면 true를 반환한다")
        void hasDateRangeShouldReturnTrueWhenDateRangeExists() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasDateRange()).isTrue();
        }
    }

    @Nested
    @DisplayName("OrganizationSearchCriteria 페이징 테스트")
    class PagingTests {

        @Test
        @DisplayName("offset()은 queryContext의 offset을 반환한다")
        void offsetShouldReturnQueryContextOffset() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(1, 20));

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("size()는 queryContext의 size를 반환한다")
        void sizeShouldReturnQueryContextSize() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("pageNumber()는 queryContext의 page를 반환한다")
        void pageNumberShouldReturnQueryContextPage() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 20));

            // then
            assertThat(criteria.pageNumber()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("OrganizationSearchCriteria 정렬 테스트")
    class SortTests {

        @Test
        @DisplayName("sortKey()는 queryContext의 sortKey를 반환한다")
        void sortKeyShouldReturnQueryContextSortKey() {
            // given
            OrganizationSortKey expectedSortKey = OrganizationSortKey.NAME;
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            expectedSortKey,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.sortKey()).isEqualTo(expectedSortKey);
        }

        @Test
        @DisplayName("sortDirection()는 queryContext의 sortDirection을 반환한다")
        void sortDirectionShouldReturnQueryContextSortDirection() {
            // given
            SortDirection expectedDirection = SortDirection.ASC;
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            DATE_RANGE,
                            OrganizationSortKey.CREATED_AT,
                            expectedDirection,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.sortDirection()).isEqualTo(expectedDirection);
        }
    }

    @Nested
    @DisplayName("OrganizationSearchCriteria DateRange 편의 메서드 테스트")
    class DateRangeConvenienceTests {

        @Test
        @DisplayName("startInstant()는 dateRange의 startInstant를 반환한다")
        void startInstantShouldReturnDateRangeStartInstant() {
            // given
            LocalDate startDate = LocalDate.now().minusDays(30);
            DateRange dateRange = DateRange.of(startDate, LocalDate.now());
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            dateRange,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.startInstant()).isNotNull();
        }

        @Test
        @DisplayName("endInstant()는 dateRange의 endInstant를 반환한다")
        void endInstantShouldReturnDateRangeEndInstant() {
            // given
            LocalDate endDate = LocalDate.now();
            DateRange dateRange = DateRange.of(LocalDate.now().minusDays(30), endDate);
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TENANT_ID_1),
                            "test",
                            OrganizationSearchField.NAME,
                            null,
                            dateRange,
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.endInstant()).isNotNull();
        }
    }
}
