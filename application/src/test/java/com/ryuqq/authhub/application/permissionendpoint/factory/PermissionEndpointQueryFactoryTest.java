package com.ryuqq.authhub.application.permissionendpoint.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.permissionendpoint.dto.query.PermissionEndpointSearchParams;
import com.ryuqq.authhub.application.permissionendpoint.fixture.PermissionEndpointQueryFixtures;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.PermissionEndpointSortKey;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointQueryFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointQueryFactory 단위 테스트")
class PermissionEndpointQueryFactoryTest {

    private PermissionEndpointQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointQueryFactory();
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: 기본 searchParams로 Criteria 변환")
        void shouldConvert_DefaultSearchParams() {
            PermissionEndpointSearchParams params = PermissionEndpointQueryFixtures.searchParams();

            PermissionEndpointSearchCriteria criteria = sut.toCriteria(params);

            assertThat(criteria).isNotNull();
            assertThat(criteria.sortKey()).isEqualTo(PermissionEndpointSortKey.CREATED_AT);
            assertThat(criteria.queryContext().sortDirection()).isEqualTo(SortDirection.DESC);
            assertThat(criteria.pageNumber()).isEqualTo(0);
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("성공: sortKey/sortDirection null이면 기본값 적용")
        void shouldUseDefaults_WhenSortKeyAndDirectionNull() {
            PermissionEndpointSearchParams params =
                    PermissionEndpointQueryFixtures.searchParamsWithNullSort();

            PermissionEndpointSearchCriteria criteria = sut.toCriteria(params);

            assertThat(criteria.sortKey()).isEqualTo(PermissionEndpointSortKey.CREATED_AT);
            assertThat(criteria.queryContext().sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("성공: httpMethods null이면 criteria의 httpMethods null")
        void shouldSetHttpMethodsNull_WhenParamsHttpMethodsNull() {
            PermissionEndpointSearchParams params =
                    PermissionEndpointQueryFixtures.searchParamsWithNullHttpMethods();

            PermissionEndpointSearchCriteria criteria = sut.toCriteria(params);

            assertThat(criteria.httpMethods()).isNull();
        }

        @Test
        @DisplayName("성공: startDate/endDate 둘 다 null이면 dateRange null")
        void shouldSetDateRangeNull_WhenStartAndEndDateNull() {
            PermissionEndpointSearchParams params = PermissionEndpointQueryFixtures.searchParams();

            PermissionEndpointSearchCriteria criteria = sut.toCriteria(params);

            assertThat(criteria.dateRange()).isNull();
        }

        @Test
        @DisplayName("성공: forPermission 파라미터로 Criteria 변환")
        void shouldConvert_ForPermissionParams() {
            PermissionEndpointSearchParams params =
                    PermissionEndpointQueryFixtures.searchParamsForPermission(1L, 0, 10);

            PermissionEndpointSearchCriteria criteria = sut.toCriteria(params);

            assertThat(criteria.permissionIds()).containsExactly(1L);
            assertThat(criteria.pageNumber()).isEqualTo(0);
            assertThat(criteria.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("성공: httpMethods 지정 시 HttpMethod 리스트로 변환")
        void shouldConvert_HttpMethodsToList() {
            PermissionEndpointSearchParams params =
                    PermissionEndpointQueryFixtures.ofDefault(
                            null, List.of("GET", "POST"), null, null, 0, 20);

            PermissionEndpointSearchCriteria criteria = sut.toCriteria(params);

            assertThat(criteria.httpMethods()).containsExactly(HttpMethod.GET, HttpMethod.POST);
        }
    }
}
