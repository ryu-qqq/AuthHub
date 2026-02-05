package com.ryuqq.authhub.application.tenant.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.tenant.dto.query.TenantSearchParams;
import com.ryuqq.authhub.application.tenant.fixture.TenantQueryFixtures;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantSearchField;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.util.Collections;
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
 * TenantQueryFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantQueryFactory 단위 테스트")
class TenantQueryFactoryTest {

    @Mock private CommonVoFactory commonVoFactory;

    private TenantQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new TenantQueryFactory(commonVoFactory);
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: TenantSearchParams로 TenantSearchCriteria 생성")
        void shouldCreateCriteria_FromSearchParams() {
            // given
            TenantSearchParams params = TenantQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(any(), any())).willReturn(dateRange);

            // when
            TenantSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isEqualTo(TenantSearchField.NAME);
            assertThat(result.statuses()).isNull();
            assertThat(result.dateRange()).isEqualTo(dateRange);
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().sortKey()).isEqualTo(TenantSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("statuses가 null이면 criteria.statuses()는 null")
        void shouldCreateCriteriaWithNullStatuses_WhenStatusesIsNull() {
            // given
            TenantSearchParams params = TenantQueryFixtures.searchParams();
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            TenantSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).isNull();
        }

        @Test
        @DisplayName("statuses가 빈 목록이면 criteria.statuses()는 null")
        void shouldCreateCriteriaWithNullStatuses_WhenStatusesIsEmpty() {
            // given
            TenantSearchParams params =
                    TenantQueryFixtures.searchParamsWithStatuses(Collections.emptyList());
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            TenantSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).isNull();
        }

        @Test
        @DisplayName("statuses가 있으면 criteria에 반영됨")
        void shouldCreateCriteriaWithStatuses_WhenStatusesProvided() {
            // given
            List<String> statuses = List.of("ACTIVE", "INACTIVE");
            TenantSearchParams params = TenantQueryFixtures.searchParamsWithStatuses(statuses);
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            TenantSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).hasSize(2);
            assertThat(result.statuses()).contains(TenantStatus.ACTIVE, TenantStatus.INACTIVE);
        }
    }
}
