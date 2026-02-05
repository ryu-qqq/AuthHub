package com.ryuqq.authhub.domain.service.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.service.vo.ServiceSearchField;
import com.ryuqq.authhub.domain.service.vo.ServiceSortKey;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceSearchCriteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceSearchCriteria 테스트")
class ServiceSearchCriteriaTest {

    private static final DateRange TEST_DATE_RANGE =
            DateRange.of(LocalDate.now().minusDays(30), LocalDate.now());
    private static final PageRequest TEST_PAGE_REQUEST = PageRequest.of(0, 20);

    @Nested
    @DisplayName("ServiceSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "검색어",
                            ServiceSearchField.NAME,
                            List.of(ServiceStatus.ACTIVE),
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // then
            assertThat(criteria.searchWord()).isEqualTo("검색어");
            assertThat(criteria.searchField()).isEqualTo(ServiceSearchField.NAME);
            assertThat(criteria.statuses()).containsExactly(ServiceStatus.ACTIVE);
            assertThat(criteria.dateRange()).isEqualTo(TEST_DATE_RANGE);
        }

        @Test
        @DisplayName("ofSimple() 팩토리 메서드로 생성한다")
        void shouldCreateViaSimpleFactoryMethod() {
            // when
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.ofSimple(
                            "검색어", List.of(ServiceStatus.ACTIVE), TEST_DATE_RANGE, 0, 20);

            // then
            assertThat(criteria.searchWord()).isEqualTo("검색어");
            assertThat(criteria.searchField()).isEqualTo(ServiceSearchField.defaultField());
            assertThat(criteria.statuses()).containsExactly(ServiceStatus.ACTIVE);
            assertThat(criteria.dateRange()).isEqualTo(TEST_DATE_RANGE);
            assertThat(criteria.sortKey()).isEqualTo(ServiceSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(criteria.pageNumber()).isEqualTo(0);
            assertThat(criteria.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("ServiceSearchCriteria 필터 확인 테스트")
    class FilterTests {

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 있으면 true를 반환한다")
        void shouldReturnTrueWhenSearchWordExists() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "검색어",
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasSearchWord()).isTrue();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 null이면 false를 반환한다")
        void shouldReturnFalseWhenSearchWordIsNull() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 빈 문자열이면 false를 반환한다")
        void shouldReturnFalseWhenSearchWordIsBlank() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "   ",
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 있으면 true를 반환한다")
        void shouldReturnTrueWhenStatusesExist() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            List.of(ServiceStatus.ACTIVE),
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 null이면 false를 반환한다")
        void shouldReturnFalseWhenStatusesIsNull() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 빈 리스트이면 false를 반환한다")
        void shouldReturnFalseWhenStatusesIsEmpty() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            List.of(),
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 있으면 true를 반환한다")
        void shouldReturnTrueWhenDateRangeExists() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasDateRange()).isTrue();
        }
    }

    @Nested
    @DisplayName("ServiceSearchCriteria 페이징/정렬 테스트")
    class PagingSortTests {

        @Test
        @DisplayName("offset()은 올바른 오프셋을 반환한다")
        void shouldReturnCorrectOffset() {
            // given
            PageRequest pageRequest = PageRequest.of(2, 20); // page 2, size 20
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            pageRequest);

            // when & then
            assertThat(criteria.offset()).isEqualTo(40L); // page 2 * size 20
        }

        @Test
        @DisplayName("size()는 페이지 크기를 반환한다")
        void shouldReturnPageSize() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("pageNumber()는 페이지 번호를 반환한다")
        void shouldReturnPageNumber() {
            // given
            PageRequest pageRequest = PageRequest.of(2, 20);
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            pageRequest);

            // when & then
            assertThat(criteria.pageNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("sortKey()는 정렬 키를 반환한다")
        void shouldReturnSortKey() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.NAME,
                            SortDirection.ASC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.sortKey()).isEqualTo(ServiceSortKey.NAME);
        }

        @Test
        @DisplayName("sortDirection()는 정렬 방향을 반환한다")
        void shouldReturnSortDirection() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.NAME,
                            SortDirection.ASC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.ASC);
        }
    }

    @Nested
    @DisplayName("ServiceSearchCriteria DateRange 위임 메서드 테스트")
    class DateRangeDelegateTests {

        @Test
        @DisplayName("startInstant()는 dateRange의 시작 일시를 반환한다")
        void shouldReturnStartInstant() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.startInstant()).isEqualTo(TEST_DATE_RANGE.startInstant());
        }

        @Test
        @DisplayName("endInstant()는 dateRange의 종료 일시를 반환한다")
        void shouldReturnEndInstant() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.endInstant()).isEqualTo(TEST_DATE_RANGE.endInstant());
        }

        @Test
        @DisplayName("dateRange가 null이면 startInstant()는 null을 반환한다")
        void shouldReturnNullWhenDateRangeIsNull() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            null,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.startInstant()).isNull();
        }

        @Test
        @DisplayName("dateRange가 null이면 endInstant()는 null을 반환한다")
        void shouldReturnNullForEndInstantWhenDateRangeIsNull() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.NAME,
                            null,
                            null,
                            ServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.endInstant()).isNull();
        }
    }
}
