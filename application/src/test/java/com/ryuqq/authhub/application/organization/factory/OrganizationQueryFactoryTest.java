package com.ryuqq.authhub.application.organization.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.organization.dto.query.OrganizationSearchParams;
import com.ryuqq.authhub.application.organization.fixture.OrganizationQueryFixtures;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSearchField;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationQueryFactory 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Factory는 SearchParams → Criteria 변환 담당
 *   <li>VO 변환 로직이 올바르게 적용되는지 검증
 *   <li>필터 조건이 올바르게 변환되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationQueryFactory 단위 테스트")
class OrganizationQueryFactoryTest {

    @Mock private CommonVoFactory commonVoFactory;

    private OrganizationQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new OrganizationQueryFactory(commonVoFactory);
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: SearchParams를 Criteria로 변환")
        void shouldConvertSearchParamsToCriteria() {
            // given
            OrganizationSearchParams params = OrganizationQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            OrganizationSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantIds()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isEqualTo(OrganizationSearchField.NAME);
            assertThat(result.statuses()).isNull();
        }

        @Test
        @DisplayName("테넌트 ID 필터가 있는 경우 Criteria에 반영")
        void shouldIncludeTenantIds_InCriteria() {
            // given
            List<String> tenantIds = List.of(OrganizationQueryFixtures.defaultTenantId());
            OrganizationSearchParams params =
                    OrganizationQueryFixtures.searchParamsWithTenantIds(tenantIds);
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            OrganizationSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.tenantIds()).hasSize(1);
            assertThat(result.tenantIds().get(0).value())
                    .isEqualTo(OrganizationQueryFixtures.defaultTenantId());
        }

        @Test
        @DisplayName("검색어가 있는 경우 Criteria에 반영")
        void shouldIncludeSearchWord_InCriteria() {
            // given
            OrganizationSearchParams params =
                    OrganizationQueryFixtures.searchParamsWithSearchWord("keyword");
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            OrganizationSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.searchWord()).isEqualTo("keyword");
        }

        @Test
        @DisplayName("상태 필터가 있는 경우 Enum으로 변환하여 Criteria에 반영")
        void shouldConvertStatusStringsToEnums_InCriteria() {
            // given
            List<String> statuses = List.of("ACTIVE", "INACTIVE");
            OrganizationSearchParams params =
                    OrganizationQueryFixtures.searchParamsWithStatuses(statuses);
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            OrganizationSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).hasSize(2);
            assertThat(result.statuses())
                    .containsExactly(OrganizationStatus.ACTIVE, OrganizationStatus.INACTIVE);
        }

        @Test
        @DisplayName("tenantIds가 null인 경우 Criteria의 tenantIds는 null")
        void shouldCreateCriteriaWithNullTenantIds_WhenTenantIdsIsNull() {
            // given
            OrganizationSearchParams params =
                    OrganizationQueryFixtures.searchParamsWithNullFilters();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            OrganizationSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.tenantIds()).isNull();
        }

        @Test
        @DisplayName("statuses가 null인 경우 Criteria의 statuses는 null")
        void shouldCreateCriteriaWithNullStatuses_WhenStatusesIsNull() {
            // given
            OrganizationSearchParams params =
                    OrganizationQueryFixtures.searchParamsWithNullFilters();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            OrganizationSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).isNull();
        }
    }
}
