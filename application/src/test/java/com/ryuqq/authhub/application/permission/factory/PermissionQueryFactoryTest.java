package com.ryuqq.authhub.application.permission.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.permission.dto.query.PermissionSearchParams;
import com.ryuqq.authhub.application.permission.fixture.PermissionQueryFixtures;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.permission.vo.PermissionSearchField;
import com.ryuqq.authhub.domain.permission.vo.PermissionSortKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
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
 * PermissionQueryFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionQueryFactory 단위 테스트")
class PermissionQueryFactoryTest {

    @Mock private CommonVoFactory commonVoFactory;

    private PermissionQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionQueryFactory(commonVoFactory);
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: PermissionSearchParams로 PermissionSearchCriteria 생성")
        void shouldCreateCriteria_FromSearchParams() {
            // given
            PermissionSearchParams params = PermissionQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(any(), any())).willReturn(dateRange);

            // when
            PermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isEqualTo(PermissionSearchField.PERMISSION_KEY);
            assertThat(result.types()).isNull();
            assertThat(result.resources()).isNull();
            assertThat(result.dateRange()).isEqualTo(dateRange);
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().sortKey()).isEqualTo(PermissionSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("types가 null이면 criteria.types()는 null")
        void shouldCreateCriteriaWithNullTypes_WhenTypesIsNull() {
            // given
            PermissionSearchParams params = PermissionQueryFixtures.searchParams();
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            PermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.types()).isNull();
        }

        @Test
        @DisplayName("types가 빈 목록이면 criteria.types()는 빈 목록")
        void shouldCreateCriteriaWithEmptyTypes_WhenTypesIsEmpty() {
            // given
            PermissionSearchParams params =
                    PermissionQueryFixtures.searchParamsWithTypes(Collections.emptyList());
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            PermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.types()).isNull();
        }

        @Test
        @DisplayName("types가 있으면 criteria에 반영됨")
        void shouldCreateCriteriaWithTypes_WhenTypesProvided() {
            // given
            List<String> types = List.of("SYSTEM", "CUSTOM");
            PermissionSearchParams params = PermissionQueryFixtures.searchParamsWithTypes(types);
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            PermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.types()).hasSize(2);
            assertThat(result.types()).contains(PermissionType.SYSTEM, PermissionType.CUSTOM);
        }

        @Test
        @DisplayName("searchField가 지정되면 criteria에 반영됨")
        void shouldCreateCriteriaWithSearchField_WhenSearchFieldProvided() {
            // given
            PermissionSearchParams params =
                    PermissionQueryFixtures.searchParamsWithSearch("user", "RESOURCE");
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            PermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo(PermissionSearchField.RESOURCE);
            assertThat(result.searchWord()).isEqualTo("user");
        }

        @Test
        @DisplayName("searchField가 null이면 기본값 PERMISSION_KEY")
        void shouldUseDefaultSearchField_WhenSearchFieldIsNull() {
            // given
            PermissionSearchParams params = PermissionQueryFixtures.searchParams();
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            PermissionSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo(PermissionSearchField.PERMISSION_KEY);
        }
    }
}
