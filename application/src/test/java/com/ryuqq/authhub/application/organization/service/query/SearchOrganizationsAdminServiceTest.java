package com.ryuqq.authhub.application.organization.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationAdminQueryPort;
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
 * SearchOrganizationsAdminService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchOrganizationsAdminService 단위 테스트")
class SearchOrganizationsAdminServiceTest {

    @Mock private OrganizationAdminQueryPort adminQueryPort;

    private SearchOrganizationsAdminService service;

    private static final UUID TENANT_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new SearchOrganizationsAdminService(adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("조직 목록을 성공적으로 검색한다")
        void shouldSearchOrganizationsSuccessfully() {
            // given
            SearchOrganizationsQuery query =
                    new SearchOrganizationsQuery(
                            TENANT_UUID, null, null, null, null, "createdAt", "DESC", 0, 20);
            OrganizationSummaryResponse orgSummary =
                    new OrganizationSummaryResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "테스트 테넌트",
                            "테스트 조직",
                            "ACTIVE",
                            5,
                            Instant.now(),
                            Instant.now());
            PageResponse<OrganizationSummaryResponse> expectedResponse =
                    PageResponse.of(List.of(orgSummary), 0, 20, 1L, 1, true, true);

            given(adminQueryPort.searchOrganizations(query)).willReturn(expectedResponse);

            // when
            PageResponse<OrganizationSummaryResponse> response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.content()).hasSize(1);
            verify(adminQueryPort).searchOrganizations(query);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
            SearchOrganizationsQuery query =
                    new SearchOrganizationsQuery(
                            TENANT_UUID,
                            "nonexistent",
                            null,
                            null,
                            null,
                            "createdAt",
                            "DESC",
                            0,
                            20);
            PageResponse<OrganizationSummaryResponse> emptyResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(adminQueryPort.searchOrganizations(query)).willReturn(emptyResponse);

            // when
            PageResponse<OrganizationSummaryResponse> response = service.execute(query);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("상태 필터로 조직을 검색한다")
        void shouldSearchOrganizationsWithStatusFilter() {
            // given
            SearchOrganizationsQuery query =
                    new SearchOrganizationsQuery(
                            TENANT_UUID, null, "ACTIVE", null, null, "createdAt", "DESC", 0, 20);
            OrganizationSummaryResponse orgSummary =
                    new OrganizationSummaryResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "테스트 테넌트",
                            "활성 조직",
                            "ACTIVE",
                            3,
                            Instant.now(),
                            Instant.now());
            PageResponse<OrganizationSummaryResponse> expectedResponse =
                    PageResponse.of(List.of(orgSummary), 0, 20, 1L, 1, true, true);

            given(adminQueryPort.searchOrganizations(query)).willReturn(expectedResponse);

            // when
            PageResponse<OrganizationSummaryResponse> response = service.execute(query);

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).status()).isEqualTo("ACTIVE");
        }
    }
}
