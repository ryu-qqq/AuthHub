package com.ryuqq.authhub.application.user.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.factory.CommonVoFactory;
import com.ryuqq.authhub.application.user.dto.query.UserSearchParams;
import com.ryuqq.authhub.application.user.fixture.UserQueryFixtures;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.UserSearchField;
import com.ryuqq.authhub.domain.user.vo.UserSortKey;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
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
 * UserQueryFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryFactory 단위 테스트")
class UserQueryFactoryTest {

    @Mock private CommonVoFactory commonVoFactory;

    private UserQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new UserQueryFactory(commonVoFactory);
    }

    @Nested
    @DisplayName("toCriteria 메서드")
    class ToCriteria {

        @Test
        @DisplayName("성공: UserSearchParams로 UserSearchCriteria 생성")
        void shouldCreateCriteria_FromSearchParams() {
            // given
            UserSearchParams params = UserQueryFixtures.searchParams();
            DateRange dateRange = DateRange.of(null, null);

            given(commonVoFactory.createDateRange(any(), any())).willReturn(dateRange);

            // when
            UserSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isNull();
            assertThat(result.searchField()).isEqualTo(UserSearchField.IDENTIFIER);
            assertThat(result.statuses()).isEmpty();
            assertThat(result.dateRange()).isEqualTo(dateRange);
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().sortKey()).isEqualTo(UserSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("statuses가 null이면 criteria.statuses()는 빈 목록")
        void shouldCreateCriteriaWithEmptyStatuses_WhenStatusesIsNull() {
            // given
            UserSearchParams params = UserQueryFixtures.searchParams();
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            UserSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).isEmpty();
        }

        @Test
        @DisplayName("statuses가 빈 목록이면 criteria.statuses()는 빈 목록")
        void shouldCreateCriteriaWithEmptyStatuses_WhenStatusesIsEmpty() {
            // given
            UserSearchParams params =
                    UserQueryFixtures.searchParamsWithStatuses(Collections.emptyList());
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            UserSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).isEmpty();
        }

        @Test
        @DisplayName("statuses가 있으면 criteria에 반영됨")
        void shouldCreateCriteriaWithStatuses_WhenStatusesProvided() {
            // given
            List<String> statuses = List.of("ACTIVE", "INACTIVE");
            UserSearchParams params = UserQueryFixtures.searchParamsWithStatuses(statuses);
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            UserSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.statuses()).hasSize(2);
            assertThat(result.statuses()).contains(UserStatus.ACTIVE, UserStatus.INACTIVE);
        }

        @Test
        @DisplayName("searchField가 null이면 IDENTIFIER로 fallback")
        void shouldUseIdentifier_WhenSearchFieldIsNull() {
            // given
            UserSearchParams params = UserQueryFixtures.searchParams();
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            UserSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.searchField()).isEqualTo(UserSearchField.IDENTIFIER);
        }

        @Test
        @DisplayName("organizationId가 있으면 criteria에 반영됨")
        void shouldCreateCriteriaWithOrganizationId_WhenProvided() {
            // given
            String orgId = "org-1";
            UserSearchParams params = UserQueryFixtures.searchParamsWithOrganization(orgId);
            given(commonVoFactory.createDateRange(any(), any()))
                    .willReturn(DateRange.of(null, null));

            // when
            UserSearchCriteria result = sut.toCriteria(params);

            // then
            assertThat(result.organizationIds()).hasSize(1);
            assertThat(result.organizationIds().get(0).value()).isEqualTo(orgId);
        }
    }
}
