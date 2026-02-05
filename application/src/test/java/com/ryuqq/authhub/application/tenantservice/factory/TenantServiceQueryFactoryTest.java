package com.ryuqq.authhub.application.tenantservice.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.tenantservice.dto.query.TenantServiceSearchParams;
import com.ryuqq.authhub.application.tenantservice.fixture.TenantServiceQueryFixtures;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
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
 * TenantServiceQueryFactory 단위 테스트
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
@DisplayName("TenantServiceQueryFactory 단위 테스트")
class TenantServiceQueryFactoryTest {

    @Mock private CommonVoFactory commonVoFactory;

    private TenantServiceQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceQueryFactory(commonVoFactory);
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: SearchParams를 Criteria로 변환")
        void shouldConvertSearchParamsToCriteria() {
            // given
            TenantServiceSearchParams params = TenantServiceQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            TenantServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.statuses()).isEmpty();
        }

        @Test
        @DisplayName("테넌트 필터가 있는 경우 Criteria에 반영")
        void shouldIncludeTenantFilter_InCriteria() {
            // given
            String tenantId = TenantServiceQueryFixtures.defaultTenantId();
            TenantServiceSearchParams params =
                    TenantServiceQueryFixtures.searchParamsWithTenant(tenantId);
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            TenantServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.tenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("서비스 필터가 있는 경우 Criteria에 반영")
        void shouldIncludeServiceFilter_InCriteria() {
            // given
            Long serviceId = TenantServiceQueryFixtures.defaultServiceId();
            TenantServiceSearchParams params =
                    TenantServiceQueryFixtures.searchParamsWithService(serviceId);
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            TenantServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.serviceId()).isEqualTo(serviceId);
        }

        @Test
        @DisplayName("상태 필터가 있는 경우 Enum으로 변환하여 Criteria에 반영")
        void shouldConvertStatusStringsToEnums_InCriteria() {
            // given
            List<String> statuses = List.of("active", "inactive");
            TenantServiceSearchParams params =
                    TenantServiceQueryFixtures.searchParamsWithStatuses(statuses);
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            TenantServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).hasSize(2);
            assertThat(result.statuses())
                    .extracting(Enum::name)
                    .containsExactlyInAnyOrder("ACTIVE", "INACTIVE");
        }

        @Test
        @DisplayName("상태 필터가 없는 경우 빈 목록으로 Criteria 생성")
        void shouldCreateCriteriaWithEmptyStatuses_WhenNoStatusFilter() {
            // given
            TenantServiceSearchParams params = TenantServiceQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            TenantServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).isEmpty();
        }

        @Test
        @DisplayName("statuses가 null인 경우 빈 목록으로 Criteria 생성")
        void shouldCreateCriteriaWithEmptyStatuses_WhenStatusesIsNull() {
            // given
            TenantServiceSearchParams params =
                    TenantServiceSearchParams.of(
                            TenantServiceQueryFixtures.defaultCommonSearchParams(),
                            null,
                            null,
                            null);
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            TenantServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).isEmpty();
        }
    }
}
