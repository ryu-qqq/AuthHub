package com.ryuqq.authhub.application.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.service.dto.query.ServiceSearchParams;
import com.ryuqq.authhub.application.service.fixture.ServiceQueryFixtures;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceSearchField;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
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
 * ServiceQueryFactory 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Factory는 SearchParams → Criteria 변환 담당
 *   <li>CommonVoFactory 의존 → Mock으로 DateRange 생성 검증
 *   <li>변환 결과의 값 검증에 집중
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceQueryFactory 단위 테스트")
class ServiceQueryFactoryTest {

    @Mock private CommonVoFactory commonVoFactory;

    private ServiceQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceQueryFactory(commonVoFactory);
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: 기본 검색 파라미터를 Criteria로 변환")
        void shouldConvertDefaultParamsToCriteria() {
            // given
            ServiceSearchParams params = ServiceQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            ServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isEqualTo(ServiceSearchField.defaultField());
            assertThat(result.statuses()).isEmpty();
            assertThat(result.dateRange()).isEqualTo(dateRange);
        }

        @Test
        @DisplayName("검색어가 포함된 파라미터를 Criteria로 변환")
        void shouldConvertParamsWithSearchWordToCriteria() {
            // given
            ServiceSearchParams params =
                    ServiceQueryFixtures.searchParamsWithField("자사몰", "SERVICE_CODE");
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            ServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.searchWord()).isEqualTo("자사몰");
            assertThat(result.searchField()).isEqualTo(ServiceSearchField.SERVICE_CODE);
        }

        @Test
        @DisplayName("상태 필터가 포함된 파라미터를 Criteria로 변환")
        void shouldConvertParamsWithStatusesToCriteria() {
            // given
            ServiceSearchParams params =
                    ServiceQueryFixtures.searchParamsWithStatuses(List.of("ACTIVE", "INACTIVE"));
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            ServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses())
                    .containsExactly(ServiceStatus.ACTIVE, ServiceStatus.INACTIVE);
        }

        @Test
        @DisplayName("빈 상태 필터는 빈 리스트로 변환")
        void shouldConvertEmptyStatusesToEmptyList() {
            // given
            ServiceSearchParams params = ServiceQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            ServiceSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).isEmpty();
        }
    }
}
