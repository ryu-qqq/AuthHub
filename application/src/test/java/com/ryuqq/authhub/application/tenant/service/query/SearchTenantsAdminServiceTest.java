package com.ryuqq.authhub.application.tenant.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.query.SearchTenantsQuery;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import com.ryuqq.authhub.application.tenant.factory.query.TenantQueryFactory;
import com.ryuqq.authhub.application.tenant.port.out.query.TenantAdminQueryPort;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SearchType;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
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
 * SearchTenantsAdminService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchTenantsAdminService 단위 테스트")
class SearchTenantsAdminServiceTest {

    @Mock private TenantQueryFactory queryFactory;
    @Mock private TenantAdminQueryPort adminQueryPort;

    private SearchTenantsAdminService service;

    @BeforeEach
    void setUp() {
        service = new SearchTenantsAdminService(queryFactory, adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("테넌트 목록을 성공적으로 검색한다")
        void shouldSearchTenantsSuccessfully() {
            // given
            SearchTenantsQuery query =
                    new SearchTenantsQuery(
                            null, null, null, null, null, "createdAt", "DESC", 0, 20);
            TenantCriteria criteria =
                    new TenantCriteria(
                            null,
                            SearchType.CONTAINS_LIKE,
                            null,
                            null,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            new PageRequest(0, 20));

            TenantSummaryResponse tenantSummary =
                    new TenantSummaryResponse(
                            UUID.randomUUID(),
                            "테스트 테넌트",
                            "ACTIVE",
                            5,
                            Instant.now(),
                            Instant.now());
            PageResponse<TenantSummaryResponse> expectedResponse =
                    PageResponse.of(List.of(tenantSummary), 0, 20, 1L, 1, true, true);

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(adminQueryPort.searchTenants(criteria)).willReturn(expectedResponse);

            // when
            PageResponse<TenantSummaryResponse> response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.content()).hasSize(1);
            verify(queryFactory).toCriteria(query);
            verify(adminQueryPort).searchTenants(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
            SearchTenantsQuery query =
                    new SearchTenantsQuery(
                            "nonexistent", null, null, null, null, "createdAt", "DESC", 0, 20);
            TenantCriteria criteria =
                    new TenantCriteria(
                            "nonexistent",
                            SearchType.CONTAINS_LIKE,
                            null,
                            null,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            new PageRequest(0, 20));

            PageResponse<TenantSummaryResponse> emptyResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(adminQueryPort.searchTenants(criteria)).willReturn(emptyResponse);

            // when
            PageResponse<TenantSummaryResponse> response = service.execute(query);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("상태 필터로 테넌트를 검색한다")
        void shouldSearchTenantsWithStatusFilter() {
            // given
            SearchTenantsQuery query =
                    new SearchTenantsQuery(
                            null, null, List.of("ACTIVE"), null, null, "createdAt", "DESC", 0, 20);
            TenantCriteria criteria =
                    new TenantCriteria(
                            null,
                            SearchType.CONTAINS_LIKE,
                            List.of(TenantStatus.ACTIVE),
                            null,
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            new PageRequest(0, 20));

            TenantSummaryResponse tenantSummary =
                    new TenantSummaryResponse(
                            UUID.randomUUID(), "활성 테넌트", "ACTIVE", 3, Instant.now(), Instant.now());
            PageResponse<TenantSummaryResponse> expectedResponse =
                    PageResponse.of(List.of(tenantSummary), 0, 20, 1L, 1, true, true);

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(adminQueryPort.searchTenants(criteria)).willReturn(expectedResponse);

            // when
            PageResponse<TenantSummaryResponse> response = service.execute(query);

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).status()).isEqualTo("ACTIVE");
        }
    }
}
