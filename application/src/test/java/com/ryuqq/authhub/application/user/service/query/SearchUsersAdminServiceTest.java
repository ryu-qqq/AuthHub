package com.ryuqq.authhub.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;
import com.ryuqq.authhub.application.user.port.out.query.UserAdminQueryPort;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SearchUsersAdminService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchUsersAdminService 단위 테스트")
class SearchUsersAdminServiceTest {

    @Mock private UserAdminQueryPort adminQueryPort;

    private SearchUsersAdminService service;

    private static final UUID TENANT_UUID = UserFixture.defaultTenantUUID();
    private static final UUID ORG_UUID = UserFixture.defaultOrganizationUUID();

    @BeforeEach
    void setUp() {
        service = new SearchUsersAdminService(adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자 목록을 성공적으로 검색한다")
        void shouldSearchUsersSuccessfully() {
            // given
            SearchUsersQuery query = SearchUsersQuery.of(TENANT_UUID, ORG_UUID, null, null, 0, 20);
            UserSummaryResponse userSummary =
                    new UserSummaryResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "테스트 테넌트",
                            ORG_UUID,
                            "테스트 조직",
                            "user@example.com",
                            "ACTIVE",
                            2,
                            Instant.now(),
                            Instant.now());
            PageResponse<UserSummaryResponse> expectedResponse =
                    PageResponse.of(List.of(userSummary), 0, 20, 1L, 1, true, true);

            given(adminQueryPort.searchUsers(query)).willReturn(expectedResponse);

            // when
            PageResponse<UserSummaryResponse> response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.content()).hasSize(1);
            verify(adminQueryPort).searchUsers(query);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
            SearchUsersQuery query =
                    SearchUsersQuery.of(TENANT_UUID, ORG_UUID, "nonexistent", null, 0, 20);
            PageResponse<UserSummaryResponse> emptyResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(adminQueryPort.searchUsers(query)).willReturn(emptyResponse);

            // when
            PageResponse<UserSummaryResponse> response = service.execute(query);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
