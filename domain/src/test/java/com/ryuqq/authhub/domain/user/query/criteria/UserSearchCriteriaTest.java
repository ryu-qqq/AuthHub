package com.ryuqq.authhub.domain.user.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.vo.UserSearchField;
import com.ryuqq.authhub.domain.user.vo.UserSortKey;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserSearchCriteria Query Criteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserSearchCriteria 테스트")
class UserSearchCriteriaTest {

    private static final String ORG_ID = "01941234-5678-7000-8000-123456789def";
    private static final String SEARCH_WORD = "test@example.com";
    private static final DateRange DATE_RANGE =
            DateRange.of(LocalDate.now().minusDays(30), LocalDate.now());
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 20);

    @Nested
    @DisplayName("UserSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // given
            List<OrganizationId> orgIds = List.of(OrganizationId.of(ORG_ID));
            List<UserStatus> statuses = List.of(UserStatus.ACTIVE);

            // when
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            orgIds,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            statuses,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.organizationIds()).isEqualTo(orgIds);
            assertThat(criteria.searchWord()).isEqualTo(SEARCH_WORD);
            assertThat(criteria.searchField()).isEqualTo(UserSearchField.IDENTIFIER);
            assertThat(criteria.statuses()).isEqualTo(statuses);
            assertThat(criteria.dateRange()).isEqualTo(DATE_RANGE);
        }

        @Test
        @DisplayName("ofDefault() 팩토리 메서드로 생성한다")
        void shouldCreateViaDefaultFactoryMethod() {
            // given
            List<OrganizationId> orgIds = List.of(OrganizationId.of(ORG_ID));

            // when
            UserSearchCriteria criteria =
                    UserSearchCriteria.ofDefault(orgIds, SEARCH_WORD, null, DATE_RANGE, 0, 20);

            // then
            assertThat(criteria.organizationIds()).isEqualTo(orgIds);
            assertThat(criteria.searchWord()).isEqualTo(SEARCH_WORD);
            assertThat(criteria.searchField()).isEqualTo(UserSearchField.IDENTIFIER);
            assertThat(criteria.statuses()).isNull();
            assertThat(criteria.dateRange()).isEqualTo(DATE_RANGE);
            assertThat(criteria.sortKey()).isEqualTo(UserSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("null organizationIds로 생성할 수 있다")
        void shouldCreateWithNullOrganizationIds() {
            // when
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.organizationIds()).isNull();
        }

        @Test
        @DisplayName("null searchWord로 생성할 수 있다")
        void shouldCreateWithNullSearchWord() {
            // when
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(OrganizationId.of(ORG_ID)),
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("UserSearchCriteria Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("hasOrganizationFilter()는 organizationIds가 있으면 true를 반환한다")
        void hasOrganizationFilterShouldReturnTrueWhenOrganizationIdsExist() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(OrganizationId.of(ORG_ID)),
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasOrganizationFilter()).isTrue();
        }

        @Test
        @DisplayName("hasOrganizationFilter()는 organizationIds가 null이면 false를 반환한다")
        void hasOrganizationFilterShouldReturnFalseWhenOrganizationIdsIsNull() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasOrganizationFilter()).isFalse();
        }

        @Test
        @DisplayName("hasOrganizationFilter()는 organizationIds가 빈 목록이면 false를 반환한다")
        void hasOrganizationFilterShouldReturnFalseWhenOrganizationIdsIsEmpty() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(),
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasOrganizationFilter()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 있으면 true를 반환한다")
        void hasSearchWordShouldReturnTrueWhenSearchWordExists() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasSearchWord()).isTrue();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 null이면 false를 반환한다")
        void hasSearchWordShouldReturnFalseWhenSearchWordIsNull() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 빈 문자열이면 false를 반환한다")
        void hasSearchWordShouldReturnFalseWhenSearchWordIsBlank() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            "   ",
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 있으면 true를 반환한다")
        void hasStatusFilterShouldReturnTrueWhenStatusesExist() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            List.of(UserStatus.ACTIVE),
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 null이면 false를 반환한다")
        void hasStatusFilterShouldReturnFalseWhenStatusesIsNull() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasStatusFilter()는 statuses가 빈 목록이면 false를 반환한다")
        void hasStatusFilterShouldReturnFalseWhenStatusesIsEmpty() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            List.of(),
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 있으면 true를 반환한다")
        void hasDateRangeShouldReturnTrueWhenDateRangeExists() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.hasDateRange()).isTrue();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 null이면 false를 반환한다")
        void hasDateRangeShouldReturnFalseWhenDateRangeIsNull() {
            // given
            UserSearchCriteria criteria =
                    new UserSearchCriteria(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            null,
                            QueryContext.of(
                                    UserSortKey.CREATED_AT, SortDirection.DESC, PAGE_REQUEST));

            // then
            assertThat(criteria.hasDateRange()).isFalse();
        }

        @Test
        @DisplayName("offset()는 queryContext의 offset을 반환한다")
        void offsetShouldReturnQueryContextOffset() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.offset()).isEqualTo(0L);
        }

        @Test
        @DisplayName("size()는 queryContext의 size를 반환한다")
        void sizeShouldReturnQueryContextSize() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("pageNumber()는 queryContext의 page를 반환한다")
        void pageNumberShouldReturnQueryContextPage() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.pageNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("sortKey()는 queryContext의 sortKey를 반환한다")
        void sortKeyShouldReturnQueryContextSortKey() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortKey()).isEqualTo(UserSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("sortDirection()는 queryContext의 sortDirection을 반환한다")
        void sortDirectionShouldReturnQueryContextSortDirection() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PAGE_REQUEST);

            // then
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.ASC);
        }

        @Test
        @DisplayName("startInstant()는 dateRange의 startInstant를 반환한다")
        void startInstantShouldReturnDateRangeStartInstant() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
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
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            UserSortKey.CREATED_AT,
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
            UserSearchCriteria criteria =
                    new UserSearchCriteria(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            null,
                            QueryContext.of(
                                    UserSortKey.CREATED_AT, SortDirection.DESC, PAGE_REQUEST));

            // when
            Instant startInstant = criteria.startInstant();

            // then
            assertThat(startInstant).isNull();
        }

        @Test
        @DisplayName("endInstant()는 dateRange가 null이면 null을 반환한다")
        void endInstantShouldReturnNullWhenDateRangeIsNull() {
            // given
            UserSearchCriteria criteria =
                    new UserSearchCriteria(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            null,
                            QueryContext.of(
                                    UserSortKey.CREATED_AT, SortDirection.DESC, PAGE_REQUEST));

            // when
            Instant endInstant = criteria.endInstant();

            // then
            assertThat(endInstant).isNull();
        }

        @Test
        @DisplayName("includeDeleted()는 queryContext의 includeDeleted를 반환한다")
        void includeDeletedShouldReturnQueryContextIncludeDeleted() {
            // given
            QueryContext<UserSortKey> contextWithDeleted =
                    QueryContext.of(UserSortKey.CREATED_AT, SortDirection.DESC, PAGE_REQUEST, true);
            UserSearchCriteria criteria =
                    new UserSearchCriteria(
                            null,
                            SEARCH_WORD,
                            UserSearchField.IDENTIFIER,
                            null,
                            DATE_RANGE,
                            contextWithDeleted);

            // then
            assertThat(criteria.includeDeleted()).isTrue();
        }
    }
}
