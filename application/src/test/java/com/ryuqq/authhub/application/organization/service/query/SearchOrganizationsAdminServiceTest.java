package com.ryuqq.authhub.application.organization.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.application.organization.factory.query.OrganizationQueryFactory;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationAdminQueryPort;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
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

    @Mock private OrganizationQueryFactory queryFactory;

    @Mock private OrganizationAdminQueryPort adminQueryPort;

    private SearchOrganizationsAdminService service;

    private static final UUID TENANT_UUID = UUID.randomUUID();
    private static final Instant CREATED_FROM = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant CREATED_TO = Instant.parse("2025-12-31T23:59:59Z");

    @BeforeEach
    void setUp() {
        service = new SearchOrganizationsAdminService(queryFactory, adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("조직 목록을 성공적으로 검색한다")
        void shouldSearchOrganizationsSuccessfully() {
            // given
            SearchOrganizationsQuery query =
                    SearchOrganizationsQuery.ofAdmin(
                            TENANT_UUID,
                            null,
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            "createdAt",
                            "DESC",
                            0,
                            20);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(TENANT_UUID),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));
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

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(adminQueryPort.searchOrganizations(criteria)).willReturn(expectedResponse);

            // when
            PageResponse<OrganizationSummaryResponse> response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.content()).hasSize(1);
            verify(queryFactory).toCriteria(query);
            verify(adminQueryPort).searchOrganizations(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
            SearchOrganizationsQuery query =
                    SearchOrganizationsQuery.ofAdmin(
                            TENANT_UUID,
                            "nonexistent",
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            "createdAt",
                            "DESC",
                            0,
                            20);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(TENANT_UUID),
                            "nonexistent",
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));
            PageResponse<OrganizationSummaryResponse> emptyResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(adminQueryPort.searchOrganizations(criteria)).willReturn(emptyResponse);

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
                    SearchOrganizationsQuery.ofAdmin(
                            TENANT_UUID,
                            null,
                            null,
                            List.of("ACTIVE"),
                            CREATED_FROM,
                            CREATED_TO,
                            "createdAt",
                            "DESC",
                            0,
                            20);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(TENANT_UUID),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));
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

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(adminQueryPort.searchOrganizations(criteria)).willReturn(expectedResponse);

            // when
            PageResponse<OrganizationSummaryResponse> response = service.execute(query);

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).status()).isEqualTo("ACTIVE");
        }
    }
}
