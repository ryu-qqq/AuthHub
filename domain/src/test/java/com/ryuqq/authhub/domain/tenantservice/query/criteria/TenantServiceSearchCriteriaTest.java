package com.ryuqq.authhub.domain.tenantservice.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceSearchCriteria Query Criteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceSearchCriteria 테스트")
class TenantServiceSearchCriteriaTest {

    private static final DateRange DATE_RANGE =
            DateRange.of(LocalDate.now().minusDays(30), LocalDate.now());
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 20);

    @Nested
    @DisplayName("TenantServiceSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // given
            String tenantId = "01941234-5678-7000-8000-123456789abc";
            Long serviceId = 1L;
            List<TenantServiceStatus> statuses = List.of(TenantServiceStatus.ACTIVE);

            // when
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            tenantId,
                            serviceId,
                            statuses,
                            DATE_RANGE,
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.tenantId()).isEqualTo(tenantId);
            assertThat(criteria.serviceId()).isEqualTo(serviceId);
            assertThat(criteria.statuses()).isEqualTo(statuses);
            assertThat(criteria.dateRange()).isEqualTo(DATE_RANGE);
        }

        @Test
        @DisplayName("null tenantId로 생성할 수 있다")
        void shouldCreateWithNullTenantId() {
            // when
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.tenantId()).isNull();
        }

        @Test
        @DisplayName("null serviceId로 생성할 수 있다")
        void shouldCreateWithNullServiceId() {
            // when
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            null,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.serviceId()).isNull();
        }

        @Test
        @DisplayName("null statuses로 생성할 수 있다")
        void shouldCreateWithNullStatuses() {
            // when
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.statuses()).isNull();
        }
    }

    @Nested
    @DisplayName("TenantServiceSearchCriteria Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("hasTenantId()는 tenantId가 있으면 true를 반환한다")
        void hasTenantIdShouldReturnTrueWhenTenantIdExists() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            null,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasTenantId()).isTrue();
        }

        @Test
        @DisplayName("hasTenantId()는 tenantId가 null이면 false를 반환한다")
        void hasTenantIdShouldReturnFalseWhenTenantIdIsNull() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasTenantId()).isFalse();
        }

        @Test
        @DisplayName("hasTenantId()는 tenantId가 blank면 false를 반환한다")
        void hasTenantIdShouldReturnFalseWhenTenantIdIsBlank() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "   ",
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasTenantId()).isFalse();
        }

        @Test
        @DisplayName("hasServiceId()는 serviceId가 있으면 true를 반환한다")
        void hasServiceIdShouldReturnTrueWhenServiceIdExists() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasServiceId()).isTrue();
        }

        @Test
        @DisplayName("hasServiceId()는 serviceId가 null이면 false를 반환한다")
        void hasServiceIdShouldReturnFalseWhenServiceIdIsNull() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            null,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasServiceId()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 있으면 true를 반환한다")
        void hasStatusFilterShouldReturnTrueWhenStatusesExist() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            List.of(TenantServiceStatus.ACTIVE),
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 null이면 false를 반환한다")
        void hasStatusFilterShouldReturnFalseWhenStatusesIsNull() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 빈 목록이면 false를 반환한다")
        void hasStatusFilterShouldReturnFalseWhenStatusesIsEmpty() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            List.of(),
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 있으면 true를 반환한다")
        void hasDateRangeShouldReturnTrueWhenDateRangeExists() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasDateRange()).isTrue();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 null이면 false를 반환한다")
        void hasDateRangeShouldReturnFalseWhenDateRangeIsNull() {
            // given - DateRange with both null (isEmpty returns true)
            DateRange emptyRange = DateRange.of(null, null);
            TenantServiceSearchCriteria criteria =
                    new TenantServiceSearchCriteria(
                            null,
                            null,
                            null,
                            emptyRange,
                            QueryContext.of(
                                    TenantServiceSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PAGE_REQUEST));

            // then - empty DateRange means hasDateRange returns false (isEmpty is true)
            assertThat(criteria.hasDateRange()).isFalse();
        }

        @Test
        @DisplayName("offset()는 queryContext의 offset을 반환한다")
        void offsetShouldReturnQueryContextOffset() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(1, 10));

            // then
            assertThat(criteria.offset()).isEqualTo(10L);
        }

        @Test
        @DisplayName("size()는 queryContext의 size를 반환한다")
        void sizeShouldReturnQueryContextSize() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("pageNumber()는 queryContext의 page를 반환한다")
        void pageNumberShouldReturnQueryContextPage() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.pageNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("sortKey()는 queryContext의 sortKey를 반환한다")
        void sortKeyShouldReturnQueryContextSortKey() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortKey()).isEqualTo(TenantServiceSortKey.SUBSCRIBED_AT);
        }

        @Test
        @DisplayName("sortDirection()는 queryContext의 sortDirection을 반환한다")
        void sortDirectionShouldReturnQueryContextSortDirection() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "tenant-1",
                            1L,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("startInstant()는 dateRange가 있으면 시작 일시를 반환한다")
        void startInstantShouldReturnStartWhenDateRangeExists() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.startInstant()).isNotNull();
        }

        @Test
        @DisplayName("endInstant()는 dateRange가 있으면 종료 일시를 반환한다")
        void endInstantShouldReturnEndWhenDateRangeExists() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DATE_RANGE,
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.endInstant()).isNotNull();
        }
    }
}
