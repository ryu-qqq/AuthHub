package com.ryuqq.authhub.domain.tenant.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenant.vo.TenantSearchField;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantSearchCriteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantSearchCriteria 테스트")
class TenantSearchCriteriaTest {

    private static final DateRange DATE_RANGE =
            DateRange.of(LocalDate.now().minusDays(30), LocalDate.now());

    @Nested
    @DisplayName("TenantSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of()로 TenantSearchCriteria를 생성한다")
        void shouldCreateWithOf() {
            // given
            String searchWord = "test";
            TenantSearchField searchField = TenantSearchField.NAME;
            List<TenantStatus> statuses = List.of(TenantStatus.ACTIVE);
            TenantSortKey sortKey = TenantSortKey.CREATED_AT;
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);

            // when
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            searchWord,
                            searchField,
                            statuses,
                            DATE_RANGE,
                            sortKey,
                            sortDirection,
                            pageRequest);

            // then
            assertThat(criteria.searchWord()).isEqualTo(searchWord);
            assertThat(criteria.searchField()).isEqualTo(searchField);
            assertThat(criteria.statuses()).isEqualTo(statuses);
            assertThat(criteria.dateRange()).isEqualTo(DATE_RANGE);
        }

        @Test
        @DisplayName("ofSimple()로 TenantSearchCriteria를 생성한다")
        void shouldCreateWithOfSimple() {
            // given
            String searchWord = "test";
            List<TenantStatus> statuses = List.of(TenantStatus.ACTIVE);
            int pageNumber = 0;
            int pageSize = 20;

            // when
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.ofSimple(
                            searchWord, statuses, DATE_RANGE, pageNumber, pageSize);

            // then
            assertThat(criteria.searchWord()).isEqualTo(searchWord);
            assertThat(criteria.statuses()).isEqualTo(statuses);
            assertThat(criteria.searchField()).isEqualTo(TenantSearchField.defaultField());
            assertThat(criteria.sortKey()).isEqualTo(TenantSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("null searchWord로 생성할 수 있다")
        void shouldCreateWithNullSearchWord() {
            // when
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.searchWord()).isNull();
        }

        @Test
        @DisplayName("null statuses로 생성할 수 있다")
        void shouldCreateWithNullStatuses() {
            // when
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.statuses()).isNull();
        }
    }

    @Nested
    @DisplayName("TenantSearchCriteria 필터 확인 테스트")
    class FilterCheckTests {

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 있으면 true를 반환한다")
        void hasSearchWordShouldReturnTrueWhenSearchWordExists() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasSearchWord()).isTrue();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 null이면 false를 반환한다")
        void hasSearchWordShouldReturnFalseWhenSearchWordIsNull() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 빈 값이면 false를 반환한다")
        void hasSearchWordShouldReturnFalseWhenSearchWordIsBlank() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "   ",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 있으면 true를 반환한다")
        void hasStatusFilterShouldReturnTrueWhenStatusesExist() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            List.of(TenantStatus.ACTIVE),
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 null이면 false를 반환한다")
        void hasStatusFilterShouldReturnFalseWhenStatusesIsNull() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 빈 리스트이면 false를 반환한다")
        void hasStatusFilterShouldReturnFalseWhenStatusesIsEmpty() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            List.of(),
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 있으면 true를 반환한다")
        void hasDateRangeShouldReturnTrueWhenDateRangeExists() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.hasDateRange()).isTrue();
        }
    }

    @Nested
    @DisplayName("TenantSearchCriteria 페이징 테스트")
    class PagingTests {

        @Test
        @DisplayName("offset()은 queryContext의 offset을 반환한다")
        void offsetShouldReturnQueryContextOffset() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(1, 20));

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("size()는 queryContext의 size를 반환한다")
        void sizeShouldReturnQueryContextSize() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("pageNumber()는 queryContext의 page를 반환한다")
        void pageNumberShouldReturnQueryContextPage() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 20));

            // then
            assertThat(criteria.pageNumber()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("TenantSearchCriteria 정렬 테스트")
    class SortTests {

        @Test
        @DisplayName("sortKey()는 queryContext의 sortKey를 반환한다")
        void sortKeyShouldReturnQueryContextSortKey() {
            // given
            TenantSortKey expectedSortKey = TenantSortKey.UPDATED_AT;
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
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
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            DATE_RANGE,
                            TenantSortKey.CREATED_AT,
                            expectedDirection,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.sortDirection()).isEqualTo(expectedDirection);
        }
    }

    @Nested
    @DisplayName("TenantSearchCriteria DateRange 편의 메서드 테스트")
    class DateRangeConvenienceTests {

        @Test
        @DisplayName("startInstant()는 dateRange의 startInstant를 반환한다")
        void startInstantShouldReturnDateRangeStartInstant() {
            // given
            LocalDate startDate = LocalDate.now().minusDays(30);
            DateRange dateRange = DateRange.of(startDate, LocalDate.now());
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            dateRange,
                            TenantSortKey.CREATED_AT,
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
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "test",
                            TenantSearchField.NAME,
                            null,
                            dateRange,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.endInstant()).isNotNull();
        }
    }
}
