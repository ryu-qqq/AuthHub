package com.ryuqq.authhub.domain.permission.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permission.vo.PermissionSearchField;
import com.ryuqq.authhub.domain.permission.vo.PermissionSortKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionSearchCriteria Query Criteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionSearchCriteria 테스트")
class PermissionSearchCriteriaTest {

    private static final Long SERVICE_ID = 1L;
    private static final String SEARCH_WORD = "user";
    private static final DateRange DATE_RANGE =
            DateRange.of(LocalDate.now().minusDays(30), LocalDate.now());
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 20);

    @Nested
    @DisplayName("PermissionSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // given
            QueryContext<PermissionSortKey> queryContext =
                    QueryContext.of(PermissionSortKey.CREATED_AT, SortDirection.DESC, PAGE_REQUEST);

            // when
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            List.of(PermissionType.SYSTEM),
                            List.of("user"),
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.serviceId()).isEqualTo(SERVICE_ID);
            assertThat(criteria.searchWord()).isEqualTo(SEARCH_WORD);
            assertThat(criteria.searchField()).isEqualTo(PermissionSearchField.PERMISSION_KEY);
            assertThat(criteria.types()).containsExactly(PermissionType.SYSTEM);
            assertThat(criteria.resources()).containsExactly("user");
            assertThat(criteria.dateRange()).isEqualTo(DATE_RANGE);
        }

        @Test
        @DisplayName("ofDefault() 팩토리 메서드로 생성한다")
        void shouldCreateViaDefaultFactoryMethod() {
            // when
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            SERVICE_ID,
                            SEARCH_WORD,
                            List.of(PermissionType.SYSTEM),
                            DATE_RANGE,
                            0,
                            20);

            // then
            assertThat(criteria.serviceId()).isEqualTo(SERVICE_ID);
            assertThat(criteria.searchWord()).isEqualTo(SEARCH_WORD);
            assertThat(criteria.searchField()).isEqualTo(PermissionSearchField.defaultField());
            assertThat(criteria.types()).containsExactly(PermissionType.SYSTEM);
            assertThat(criteria.resources()).isNull();
            assertThat(criteria.dateRange()).isEqualTo(DATE_RANGE);
            assertThat(criteria.sortKey()).isEqualTo(PermissionSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("null serviceId로 생성할 수 있다")
        void shouldCreateWithNullServiceId() {
            // when
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.serviceId()).isNull();
        }

        @Test
        @DisplayName("null searchWord로 생성할 수 있다")
        void shouldCreateWithNullSearchWord() {
            // when
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("PermissionSearchCriteria Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("hasServiceIdFilter()는 serviceId가 있으면 true를 반환한다")
        void hasServiceIdFilterShouldReturnTrueWhenServiceIdExists() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasServiceIdFilter()).isTrue();
        }

        @Test
        @DisplayName("hasServiceIdFilter()는 serviceId가 null이면 false를 반환한다")
        void hasServiceIdFilterShouldReturnFalseWhenServiceIdIsNull() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasServiceIdFilter()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 있으면 true를 반환한다")
        void hasSearchWordShouldReturnTrueWhenSearchWordExists() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasSearchWord()).isTrue();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 null이면 false를 반환한다")
        void hasSearchWordShouldReturnFalseWhenSearchWordIsNull() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 빈 문자열이면 false를 반환한다")
        void hasSearchWordShouldReturnFalseWhenSearchWordIsBlank() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            "",
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasTypeFilter()는 types가 있으면 true를 반환한다")
        void hasTypeFilterShouldReturnTrueWhenTypesExist() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            List.of(PermissionType.SYSTEM),
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasTypeFilter()).isTrue();
        }

        @Test
        @DisplayName("hasTypeFilter()는 types가 null이면 false를 반환한다")
        void hasTypeFilterShouldReturnFalseWhenTypesIsNull() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasTypeFilter()).isFalse();
        }

        @Test
        @DisplayName("hasTypeFilter()는 types가 빈 목록이면 false를 반환한다")
        void hasTypeFilterShouldReturnFalseWhenTypesIsEmpty() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            List.of(),
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasTypeFilter()).isFalse();
        }

        @Test
        @DisplayName("hasResourceFilter()는 resources가 있으면 true를 반환한다")
        void hasResourceFilterShouldReturnTrueWhenResourcesExist() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            List.of("user"),
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasResourceFilter()).isTrue();
        }

        @Test
        @DisplayName("hasResourceFilter()는 resources가 null이면 false를 반환한다")
        void hasResourceFilterShouldReturnFalseWhenResourcesIsNull() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasResourceFilter()).isFalse();
        }

        @Test
        @DisplayName("hasResourceFilter()는 resources가 빈 목록이면 false를 반환한다")
        void hasResourceFilterShouldReturnFalseWhenResourcesIsEmpty() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            List.of(),
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasResourceFilter()).isFalse();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 있으면 true를 반환한다")
        void hasDateRangeShouldReturnTrueWhenDateRangeExists() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasDateRange()).isTrue();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 null이면 false를 반환한다")
        void hasDateRangeShouldReturnFalseWhenDateRangeIsNull() {
            // given
            PermissionSearchCriteria criteria =
                    new PermissionSearchCriteria(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    PermissionSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PAGE_REQUEST));

            // then
            assertThat(criteria.hasDateRange()).isFalse();
        }

        @Test
        @DisplayName("offset()는 queryContext의 offset을 반환한다")
        void offsetShouldReturnQueryContextOffset() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.offset()).isEqualTo(0L);
        }

        @Test
        @DisplayName("size()는 queryContext의 size를 반환한다")
        void sizeShouldReturnQueryContextSize() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("pageNumber()는 queryContext의 page를 반환한다")
        void pageNumberShouldReturnQueryContextPage() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.pageNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("sortKey()는 queryContext의 sortKey를 반환한다")
        void sortKeyShouldReturnQueryContextSortKey() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortKey()).isEqualTo(PermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("sortDirection()는 queryContext의 sortDirection을 반환한다")
        void sortDirectionShouldReturnQueryContextSortDirection() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("startInstant()는 dateRange의 startInstant를 반환한다")
        void startInstantShouldReturnDateRangeStartInstant() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // when
            Instant startInstant = criteria.startInstant();

            // then
            assertThat(startInstant).isNotNull();
        }

        @Test
        @DisplayName("endInstant()는 dateRange의 endInstant를 반환한다")
        void endInstantShouldReturnDateRangeEndInstant() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DATE_RANGE,
                            PermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // when
            Instant endInstant = criteria.endInstant();

            // then
            assertThat(endInstant).isNotNull();
        }

        @Test
        @DisplayName("startInstant()는 dateRange가 null이면 null을 반환한다")
        void startInstantShouldReturnNullWhenDateRangeIsNull() {
            // given
            PermissionSearchCriteria criteria =
                    new PermissionSearchCriteria(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    PermissionSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PAGE_REQUEST));

            // when
            Instant startInstant = criteria.startInstant();

            // then
            assertThat(startInstant).isNull();
        }

        @Test
        @DisplayName("endInstant()는 dateRange가 null이면 null을 반환한다")
        void endInstantShouldReturnNullWhenDateRangeIsNull() {
            // given
            PermissionSearchCriteria criteria =
                    new PermissionSearchCriteria(
                            SERVICE_ID,
                            SEARCH_WORD,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    PermissionSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PAGE_REQUEST));

            // when
            Instant endInstant = criteria.endInstant();

            // then
            assertThat(endInstant).isNull();
        }
    }
}
