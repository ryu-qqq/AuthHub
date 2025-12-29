package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantAdminQueryDslRepository;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantDetailResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantSummaryResponse;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SearchType;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
 * TenantAdminQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantAdminQueryAdapter 단위 테스트")
class TenantAdminQueryAdapterTest {

    @Mock private TenantAdminQueryDslRepository repository;

    private TenantAdminQueryAdapter adapter;

    private static final UUID TENANT_UUID = UUID.randomUUID();
    private static final LocalDate CREATED_FROM = LocalDate.of(2025, 1, 1);
    private static final LocalDate CREATED_TO = LocalDate.of(2025, 12, 31);

    @BeforeEach
    void setUp() {
        adapter = new TenantAdminQueryAdapter(repository);
    }

    @Nested
    @DisplayName("searchTenants 메서드")
    class SearchTenantsTest {

        @Test
        @DisplayName("테넌트 목록을 성공적으로 검색한다")
        void shouldSearchTenantsSuccessfully() {
            // given
            TenantCriteria criteria =
                    TenantCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(CREATED_FROM, CREATED_TO),
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));
            TenantSummaryResponse tenantSummary =
                    new TenantSummaryResponse(
                            UUID.randomUUID(),
                            "테스트 테넌트",
                            "ACTIVE",
                            5,
                            Instant.now(),
                            Instant.now());

            given(repository.searchTenants(criteria)).willReturn(List.of(tenantSummary));
            given(repository.countTenants(criteria)).willReturn(1L);

            // when
            PageResponse<TenantSummaryResponse> result = adapter.searchTenants(criteria);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            verify(repository).searchTenants(criteria);
            verify(repository).countTenants(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
            TenantCriteria criteria =
                    TenantCriteria.of(
                            "nonexistent",
                            SearchType.CONTAINS_LIKE,
                            null,
                            DateRange.of(CREATED_FROM, CREATED_TO),
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            given(repository.searchTenants(criteria)).willReturn(List.of());
            given(repository.countTenants(criteria)).willReturn(0L);

            // when
            PageResponse<TenantSummaryResponse> result = adapter.searchTenants(criteria);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }

        @Test
        @DisplayName("페이징 정보를 올바르게 계산한다")
        void shouldCalculatePagingInfoCorrectly() {
            // given
            TenantCriteria criteria =
                    TenantCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(CREATED_FROM, CREATED_TO),
                            TenantSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(1, 10));
            TenantSummaryResponse tenantSummary =
                    new TenantSummaryResponse(
                            UUID.randomUUID(),
                            "테스트 테넌트",
                            "ACTIVE",
                            3,
                            Instant.now(),
                            Instant.now());

            given(repository.searchTenants(criteria)).willReturn(List.of(tenantSummary));
            given(repository.countTenants(criteria)).willReturn(25L);

            // when
            PageResponse<TenantSummaryResponse> result = adapter.searchTenants(criteria);

            // then
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(25L);
            assertThat(result.totalPages()).isEqualTo(3);
            assertThat(result.first()).isFalse();
            assertThat(result.last()).isFalse();
        }
    }

    @Nested
    @DisplayName("findTenantDetail 메서드")
    class FindTenantDetailTest {

        @Test
        @DisplayName("테넌트 상세 정보를 성공적으로 조회한다")
        void shouldFindTenantDetailSuccessfully() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            TenantDetailResponse detailResponse =
                    new TenantDetailResponse(
                            TENANT_UUID,
                            "테스트 테넌트",
                            "ACTIVE",
                            List.of(),
                            3,
                            Instant.now(),
                            Instant.now());

            given(repository.findTenantDetail(TENANT_UUID)).willReturn(Optional.of(detailResponse));

            // when
            Optional<TenantDetailResponse> result = adapter.findTenantDetail(tenantId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().tenantId()).isEqualTo(TENANT_UUID);
            verify(repository).findTenantDetail(TENANT_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 테넌트는 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenTenantNotFound() {
            // given
            UUID nonExistingTenantId = UUID.randomUUID();
            TenantId tenantId = TenantId.of(nonExistingTenantId);

            given(repository.findTenantDetail(nonExistingTenantId)).willReturn(Optional.empty());

            // when
            Optional<TenantDetailResponse> result = adapter.findTenantDetail(tenantId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
