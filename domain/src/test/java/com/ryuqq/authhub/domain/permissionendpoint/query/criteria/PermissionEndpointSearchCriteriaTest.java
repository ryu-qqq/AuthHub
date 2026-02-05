package com.ryuqq.authhub.domain.permissionendpoint.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSearchField;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSortKey;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointSearchCriteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointSearchCriteria 테스트")
class PermissionEndpointSearchCriteriaTest {

    private static final LocalDate START_DATE = LocalDate.of(2025, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2025, 1, 31);
    private static final DateRange DATE_RANGE = DateRange.of(START_DATE, END_DATE);

    @Nested
    @DisplayName("PermissionEndpointSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 메서드로 정상 생성한다")
        void shouldCreateWithOfMethod() {
            // given
            List<Long> permissionIds = List.of(1L, 2L);
            String searchWord = "users";
            PermissionEndpointSearchField searchField = PermissionEndpointSearchField.URL_PATTERN;
            List<HttpMethod> httpMethods = List.of(HttpMethod.GET, HttpMethod.POST);
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.CREATED_AT;
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);

            // when
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            permissionIds,
                            searchWord,
                            searchField,
                            httpMethods,
                            DATE_RANGE,
                            sortKey,
                            sortDirection,
                            pageRequest);

            // then
            assertThat(criteria.permissionIds()).isEqualTo(permissionIds);
            assertThat(criteria.searchWord()).isEqualTo(searchWord);
            assertThat(criteria.searchField()).isEqualTo(searchField);
            assertThat(criteria.httpMethods()).isEqualTo(httpMethods);
            assertThat(criteria.dateRange()).isEqualTo(DATE_RANGE);
            assertThat(criteria.queryContext().sortKey()).isEqualTo(sortKey);
            assertThat(criteria.queryContext().sortDirection()).isEqualTo(sortDirection);
            assertThat(criteria.queryContext().pageRequest()).isEqualTo(pageRequest);
        }

        @Test
        @DisplayName("null 값들이 포함된 Criteria를 생성한다")
        void shouldCreateWithNullValues() {
            // when
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // then
            assertThat(criteria.permissionIds()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.httpMethods()).isNull();
        }
    }

    @Nested
    @DisplayName("forPermission() 팩토리 메서드 테스트")
    class ForPermissionTests {

        @Test
        @DisplayName("특정 권한의 엔드포인트 조회용 Criteria를 생성한다")
        void shouldCreateForPermission() {
            // when
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forPermission(1L, 0, 20);

            // then
            assertThat(criteria.permissionIds()).containsExactly(1L);
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.searchField())
                    .isEqualTo(PermissionEndpointSearchField.defaultField());
            assertThat(criteria.httpMethods()).isNull();
            assertThat(criteria.dateRange()).isNull();
            assertThat(criteria.queryContext().sortKey())
                    .isEqualTo(PermissionEndpointSortKey.CREATED_AT);
            assertThat(criteria.queryContext().sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(criteria.queryContext().pageRequest().page()).isEqualTo(0);
            assertThat(criteria.queryContext().pageRequest().size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("forGateway() 팩토리 메서드 테스트")
    class ForGatewayTests {

        @Test
        @DisplayName("Gateway 조회용 Criteria를 생성한다")
        void shouldCreateForGateway() {
            // when
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forGateway("/api/v1/users", HttpMethod.GET);

            // then
            assertThat(criteria.permissionIds()).isNull();
            assertThat(criteria.searchWord()).isEqualTo("/api/v1/users");
            assertThat(criteria.searchField()).isEqualTo(PermissionEndpointSearchField.URL_PATTERN);
            assertThat(criteria.httpMethods()).containsExactly(HttpMethod.GET);
            assertThat(criteria.dateRange()).isNull();
            assertThat(criteria.queryContext().pageRequest().page()).isEqualTo(0);
            assertThat(criteria.queryContext().pageRequest().size()).isEqualTo(100);
        }

        @Test
        @DisplayName("null HTTP 메서드로 Gateway Criteria를 생성한다")
        void shouldCreateForGatewayWithNullHttpMethod() {
            // when
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forGateway("/api/v1/users", null);

            // then
            assertThat(criteria.httpMethods()).isNull();
        }
    }

    @Nested
    @DisplayName("hasPermissionIds() 테스트")
    class HasPermissionIdsTests {

        @Test
        @DisplayName("권한 ID 목록이 있으면 true를 반환한다")
        void shouldReturnTrueWhenPermissionIdsExist() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            List.of(1L, 2L),
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasPermissionIds()).isTrue();
        }

        @Test
        @DisplayName("권한 ID 목록이 null이면 false를 반환한다")
        void shouldReturnFalseWhenPermissionIdsIsNull() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasPermissionIds()).isFalse();
        }

        @Test
        @DisplayName("권한 ID 목록이 비어있으면 false를 반환한다")
        void shouldReturnFalseWhenPermissionIdsIsEmpty() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            List.of(),
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasPermissionIds()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchWord() 테스트")
    class HasSearchWordTests {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void shouldReturnTrueWhenSearchWordExists() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            "users",
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasSearchWord()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void shouldReturnFalseWhenSearchWordIsNull() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 false를 반환한다")
        void shouldReturnFalseWhenSearchWordIsBlank() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            "   ",
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasSearchWord()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasHttpMethodFilter() 테스트")
    class HasHttpMethodFilterTests {

        @Test
        @DisplayName("HTTP 메서드 필터가 있으면 true를 반환한다")
        void shouldReturnTrueWhenHttpMethodFilterExists() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            List.of(HttpMethod.GET),
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasHttpMethodFilter()).isTrue();
        }

        @Test
        @DisplayName("HTTP 메서드 필터가 null이면 false를 반환한다")
        void shouldReturnFalseWhenHttpMethodFilterIsNull() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasHttpMethodFilter()).isFalse();
        }

        @Test
        @DisplayName("HTTP 메서드 필터가 비어있으면 false를 반환한다")
        void shouldReturnFalseWhenHttpMethodFilterIsEmpty() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            List.of(),
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasHttpMethodFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasDateRange() 테스트")
    class HasDateRangeTests {

        @Test
        @DisplayName("날짜 범위가 있으면 true를 반환한다")
        void shouldReturnTrueWhenDateRangeExists() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasDateRange()).isTrue();
        }

        @Test
        @DisplayName("날짜 범위가 null이면 false를 반환한다")
        void shouldReturnFalseWhenDateRangeIsNull() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            null,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.hasDateRange()).isFalse();
        }
    }

    @Nested
    @DisplayName("QueryContext 위임 메서드 테스트")
    class QueryContextDelegationTests {

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void shouldReturnOffsetFromQueryContext() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 20));

            // when & then
            assertThat(criteria.offset()).isEqualTo(40L);
        }

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void shouldReturnSizeFromQueryContext() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 30));

            // when & then
            assertThat(criteria.size()).isEqualTo(30);
        }

        @Test
        @DisplayName("pageNumber()는 QueryContext의 page를 반환한다")
        void shouldReturnPageNumberFromQueryContext() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 20));

            // when & then
            assertThat(criteria.pageNumber()).isEqualTo(3);
        }

        @Test
        @DisplayName("sortKey()는 QueryContext의 sortKey를 반환한다")
        void shouldReturnSortKeyFromQueryContext() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.UPDATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.sortKey()).isEqualTo(PermissionEndpointSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("sortDirection()는 QueryContext의 sortDirection을 반환한다")
        void shouldReturnSortDirectionFromQueryContext() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.ASC);
        }
    }

    @Nested
    @DisplayName("DateRange 위임 메서드 테스트")
    class DateRangeDelegationTests {

        @Test
        @DisplayName("startInstant()는 DateRange의 startInstant를 반환한다")
        void shouldReturnStartInstantFromDateRange() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when
            Instant startInstant = criteria.startInstant();

            // then
            assertThat(startInstant).isNotNull();
            assertThat(startInstant).isEqualTo(DATE_RANGE.startInstant());
        }

        @Test
        @DisplayName("endInstant()는 DateRange의 endInstant를 반환한다")
        void shouldReturnEndInstantFromDateRange() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DATE_RANGE,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when
            Instant endInstant = criteria.endInstant();

            // then
            assertThat(endInstant).isNotNull();
            assertThat(endInstant).isEqualTo(DATE_RANGE.endInstant());
        }

        @Test
        @DisplayName("DateRange가 null이면 startInstant()는 null을 반환한다")
        void shouldReturnNullWhenDateRangeIsNull() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            null,
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when & then
            assertThat(criteria.startInstant()).isNull();
            assertThat(criteria.endInstant()).isNull();
        }
    }
}
