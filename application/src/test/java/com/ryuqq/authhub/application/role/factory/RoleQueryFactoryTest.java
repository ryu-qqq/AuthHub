package com.ryuqq.authhub.application.role.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.role.dto.query.RoleSearchParams;
import com.ryuqq.authhub.application.role.fixture.RoleQueryFixtures;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleSearchField;
import com.ryuqq.authhub.domain.role.vo.RoleType;
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
 * RoleQueryFactory 단위 테스트
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
@DisplayName("RoleQueryFactory 단위 테스트")
class RoleQueryFactoryTest {

    @Mock private CommonVoFactory commonVoFactory;

    private RoleQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new RoleQueryFactory(commonVoFactory);
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: 기본 검색 파라미터를 Criteria로 변환")
        void shouldConvertDefaultParamsToCriteria() {
            // given
            RoleSearchParams params = RoleQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            RoleSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantId()).isNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isEqualTo(RoleSearchField.defaultField());
            assertThat(result.types()).isNull();
            assertThat(result.dateRange()).isEqualTo(dateRange);
        }

        @Test
        @DisplayName("검색어가 포함된 파라미터를 Criteria로 변환")
        void shouldConvertParamsWithSearchWordToCriteria() {
            // given
            RoleSearchParams params = RoleQueryFixtures.searchParamsWithWord("관리자");
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            RoleSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.searchWord()).isEqualTo("관리자");
        }

        @Test
        @DisplayName("역할 유형 필터가 포함된 파라미터를 Criteria로 변환")
        void shouldConvertParamsWithTypesToCriteria() {
            // given
            RoleSearchParams params =
                    RoleQueryFixtures.searchParamsWithTypes(List.of("SYSTEM", "CUSTOM"));
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(params.startDate(), params.endDate()))
                    .willReturn(dateRange);

            // when
            RoleSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.types()).containsExactly(RoleType.SYSTEM, RoleType.CUSTOM);
        }
    }
}
