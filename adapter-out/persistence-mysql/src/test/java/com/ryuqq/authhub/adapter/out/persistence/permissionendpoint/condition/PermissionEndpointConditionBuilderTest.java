package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.condition;

import static com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.QPermissionEndpointJpaEntity.permissionEndpointJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSearchField;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSortKey;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointConditionBuilder 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>ConditionBuilder는 순수 조건 생성 로직 → Mock 불필요
 *   <li>buildCondition / buildOrderSpecifier 검증
 *   <li>null/빈 필터 제외 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointConditionBuilder 단위 테스트")
class PermissionEndpointConditionBuilderTest {

    private PermissionEndpointConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: 검색 조건이 모두 포함된 BooleanBuilder 생성")
        void shouldBuildCondition_WithAllFilters() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            List.of(1L, 2L),
                            "users",
                            PermissionEndpointSearchField.URL_PATTERN,
                            List.of(HttpMethod.GET, HttpMethod.POST),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            BooleanBuilder result = sut.buildCondition(criteria);

            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("permissionIds가 없으면 해당 조건 제외")
        void shouldExcludePermissionIds_WhenEmpty() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            "api",
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.URL_PATTERN,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            BooleanBuilder result = sut.buildCondition(criteria);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("searchWord가 없으면 검색어 조건 제외")
        void shouldExcludeSearchWord_WhenBlank() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            List.of(1L),
                            null,
                            PermissionEndpointSearchField.DESCRIPTION,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            BooleanBuilder result = sut.buildCondition(criteria);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("httpMethods가 없으면 HTTP 메서드 조건 제외")
        void shouldExcludeHttpMethods_WhenEmpty() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            List.of(1L),
                            "read",
                            PermissionEndpointSearchField.DESCRIPTION,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            BooleanBuilder result = sut.buildCondition(criteria);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("날짜 범위가 없으면 날짜 조건 제외")
        void shouldExcludeDateRange_WhenEmpty() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forPermission(1L, 0, 10);

            BooleanBuilder result = sut.buildCondition(criteria);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("buildOrderSpecifier 메서드")
    class BuildOrderSpecifier {

        @Test
        @DisplayName("CREATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtAsc() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            assertThat(result).isEqualTo(permissionEndpointJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            assertThat(result).isEqualTo(permissionEndpointJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("UPDATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtAsc() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            assertThat(result).isEqualTo(permissionEndpointJpaEntity.updatedAt.asc());
        }

        @Test
        @DisplayName("URL_PATTERN 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUrlPattern() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.URL_PATTERN,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            assertThat(result).isEqualTo(permissionEndpointJpaEntity.urlPattern.asc());
        }

        @Test
        @DisplayName("HTTP_METHOD 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithHttpMethod() {
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.of(
                            null,
                            null,
                            PermissionEndpointSearchField.URL_PATTERN,
                            null,
                            DateRange.of(null, null),
                            PermissionEndpointSortKey.HTTP_METHOD,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            assertThat(result).isEqualTo(permissionEndpointJpaEntity.httpMethod.desc());
        }
    }
}
