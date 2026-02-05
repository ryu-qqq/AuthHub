package com.ryuqq.authhub.domain.role.query.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.vo.RoleSearchField;
import com.ryuqq.authhub.domain.role.vo.RoleSortKey;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleSearchCriteria 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleSearchCriteria 테스트")
class RoleSearchCriteriaTest {

    private static final TenantId TEST_TENANT_ID =
            TenantId.of("01941234-5678-7000-8000-123456789abc");
    private static final Long TEST_SERVICE_ID = 1L;
    private static final DateRange TEST_DATE_RANGE =
            DateRange.of(LocalDate.now().minusDays(30), LocalDate.now());
    private static final PageRequest TEST_PAGE_REQUEST = PageRequest.of(0, 20);

    @Nested
    @DisplayName("RoleSearchCriteria 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            TEST_TENANT_ID,
                            TEST_SERVICE_ID,
                            "검색어",
                            RoleSearchField.NAME,
                            List.of(RoleType.CUSTOM),
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // then
            assertThat(criteria.tenantId()).isEqualTo(TEST_TENANT_ID);
            assertThat(criteria.serviceId()).isEqualTo(TEST_SERVICE_ID);
            assertThat(criteria.searchWord()).isEqualTo("검색어");
            assertThat(criteria.searchField()).isEqualTo(RoleSearchField.NAME);
            assertThat(criteria.types()).containsExactly(RoleType.CUSTOM);
            assertThat(criteria.dateRange()).isEqualTo(TEST_DATE_RANGE);
        }

        @Test
        @DisplayName("ofGlobal() 팩토리 메서드로 Global 역할 조회용 Criteria를 생성한다")
        void shouldCreateGlobalCriteriaViaFactoryMethod() {
            // when
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(
                            "검색어", List.of(RoleType.SYSTEM), TEST_DATE_RANGE, 0, 20);

            // then
            assertThat(criteria.tenantId()).isNull();
            assertThat(criteria.serviceId()).isNull();
            assertThat(criteria.searchWord()).isEqualTo("검색어");
            assertThat(criteria.searchField()).isEqualTo(RoleSearchField.defaultField());
            assertThat(criteria.types()).containsExactly(RoleType.SYSTEM);
            assertThat(criteria.dateRange()).isEqualTo(TEST_DATE_RANGE);
            assertThat(criteria.sortKey()).isEqualTo(RoleSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(criteria.pageNumber()).isEqualTo(0);
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("ofTenant() 팩토리 메서드로 테넌트 역할 조회용 Criteria를 생성한다")
        void shouldCreateTenantCriteriaViaFactoryMethod() {
            // when
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofTenant(
                            TEST_TENANT_ID,
                            "검색어",
                            List.of(RoleType.CUSTOM),
                            TEST_DATE_RANGE,
                            0,
                            20);

            // then
            assertThat(criteria.tenantId()).isEqualTo(TEST_TENANT_ID);
            assertThat(criteria.serviceId()).isNull();
            assertThat(criteria.searchWord()).isEqualTo("검색어");
            assertThat(criteria.searchField()).isEqualTo(RoleSearchField.defaultField());
            assertThat(criteria.types()).containsExactly(RoleType.CUSTOM);
            assertThat(criteria.dateRange()).isEqualTo(TEST_DATE_RANGE);
            assertThat(criteria.sortKey()).isEqualTo(RoleSortKey.CREATED_AT);
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(criteria.pageNumber()).isEqualTo(0);
            assertThat(criteria.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("RoleSearchCriteria 필터 확인 테스트")
    class FilterTests {

        @Test
        @DisplayName("hasTenantFilter()는 tenantId가 있으면 true를 반환한다")
        void shouldReturnTrueWhenTenantIdExists() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            TEST_TENANT_ID,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasTenantFilter()).isTrue();
        }

        @Test
        @DisplayName("hasTenantFilter()는 tenantId가 null이면 false를 반환한다")
        void shouldReturnFalseWhenTenantIdIsNull() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasTenantFilter()).isFalse();
        }

        @Test
        @DisplayName("isGlobalOnly()는 tenantId가 null이면 true를 반환한다")
        void shouldReturnTrueWhenTenantIdIsNull() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.isGlobalOnly()).isTrue();
        }

        @Test
        @DisplayName("isGlobalOnly()는 tenantId가 있으면 false를 반환한다")
        void shouldReturnFalseWhenTenantIdExists() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            TEST_TENANT_ID,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.isGlobalOnly()).isFalse();
        }

        @Test
        @DisplayName("hasServiceIdFilter()는 serviceId가 있으면 true를 반환한다")
        void shouldReturnTrueWhenServiceIdExists() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            TEST_SERVICE_ID,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasServiceIdFilter()).isTrue();
        }

        @Test
        @DisplayName("hasServiceIdFilter()는 serviceId가 null이면 false를 반환한다")
        void shouldReturnFalseWhenServiceIdIsNull() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasServiceIdFilter()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 있으면 true를 반환한다")
        void shouldReturnTrueWhenSearchWordExists() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            "검색어",
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasSearchWord()).isTrue();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 null이면 false를 반환한다")
        void shouldReturnFalseWhenSearchWordIsNull() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasSearchWord()는 searchWord가 빈 문자열이면 false를 반환한다")
        void shouldReturnFalseWhenSearchWordIsBlank() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            "   ",
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasSearchWord()).isFalse();
        }

        @Test
        @DisplayName("hasTypeFilter()는 types가 있으면 true를 반환한다")
        void shouldReturnTrueWhenTypesExist() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            List.of(RoleType.CUSTOM),
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasTypeFilter()).isTrue();
        }

        @Test
        @DisplayName("hasTypeFilter()는 types가 null이면 false를 반환한다")
        void shouldReturnFalseWhenTypesIsNull() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasTypeFilter()).isFalse();
        }

        @Test
        @DisplayName("hasTypeFilter()는 types가 빈 리스트이면 false를 반환한다")
        void shouldReturnFalseWhenTypesIsEmpty() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            List.of(),
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasTypeFilter()).isFalse();
        }

        @Test
        @DisplayName("hasDateRange()는 dateRange가 있으면 true를 반환한다")
        void shouldReturnTrueWhenDateRangeExists() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.hasDateRange()).isTrue();
        }
    }

    @Nested
    @DisplayName("RoleSearchCriteria 페이징/정렬 테스트")
    class PagingSortTests {

        @Test
        @DisplayName("offset()은 올바른 오프셋을 반환한다")
        void shouldReturnCorrectOffset() {
            // given
            PageRequest pageRequest = PageRequest.of(2, 20); // page 2, size 20
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            pageRequest);

            // when & then
            assertThat(criteria.offset()).isEqualTo(40L); // page 2 * size 20
        }

        @Test
        @DisplayName("size()는 페이지 크기를 반환한다")
        void shouldReturnPageSize() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
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
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            pageRequest);

            // when & then
            assertThat(criteria.pageNumber()).isEqualTo(2);
        }

        @Test
        @DisplayName("sortKey()는 정렬 키를 반환한다")
        void shouldReturnSortKey() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.NAME,
                            SortDirection.ASC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.sortKey()).isEqualTo(RoleSortKey.NAME);
        }

        @Test
        @DisplayName("sortDirection()는 정렬 방향을 반환한다")
        void shouldReturnSortDirection() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            TEST_DATE_RANGE,
                            RoleSortKey.NAME,
                            SortDirection.ASC,
                            TEST_PAGE_REQUEST);

            // when & then
            assertThat(criteria.sortDirection()).isEqualTo(SortDirection.ASC);
        }
    }
}
