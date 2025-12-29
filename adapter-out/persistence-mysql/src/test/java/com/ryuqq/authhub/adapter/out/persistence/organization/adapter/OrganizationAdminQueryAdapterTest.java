package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationAdminQueryDslRepository;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationDetailResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationSummaryResponse;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
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
 * OrganizationAdminQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationAdminQueryAdapter 단위 테스트")
class OrganizationAdminQueryAdapterTest {

    @Mock private OrganizationAdminQueryDslRepository repository;

    private OrganizationAdminQueryAdapter adapter;

    private static final UUID ORGANIZATION_UUID = UUID.randomUUID();
    private static final UUID TENANT_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new OrganizationAdminQueryAdapter(repository);
    }

    @Nested
    @DisplayName("searchOrganizations 메서드")
    class SearchOrganizationsTest {

        @Test
        @DisplayName("조직 목록을 성공적으로 검색한다")
        void shouldSearchOrganizationsSuccessfully() {
            // given
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

            given(repository.searchOrganizations(criteria)).willReturn(List.of(orgSummary));
            given(repository.countOrganizations(criteria)).willReturn(1L);

            // when
            PageResponse<OrganizationSummaryResponse> result =
                    adapter.searchOrganizations(criteria);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            verify(repository).searchOrganizations(criteria);
            verify(repository).countOrganizations(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
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

            given(repository.searchOrganizations(criteria)).willReturn(List.of());
            given(repository.countOrganizations(criteria)).willReturn(0L);

            // when
            PageResponse<OrganizationSummaryResponse> result =
                    adapter.searchOrganizations(criteria);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }

        @Test
        @DisplayName("페이징 정보를 올바르게 계산한다")
        void shouldCalculatePagingInfoCorrectly() {
            // given
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(TENANT_UUID),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(2, 10));
            OrganizationSummaryResponse orgSummary =
                    new OrganizationSummaryResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "테스트 테넌트",
                            "테스트 조직",
                            "ACTIVE",
                            3,
                            Instant.now(),
                            Instant.now());

            given(repository.searchOrganizations(criteria)).willReturn(List.of(orgSummary));
            given(repository.countOrganizations(criteria)).willReturn(35L);

            // when
            PageResponse<OrganizationSummaryResponse> result =
                    adapter.searchOrganizations(criteria);

            // then
            assertThat(result.page()).isEqualTo(2);
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(35L);
            assertThat(result.totalPages()).isEqualTo(4);
            assertThat(result.first()).isFalse();
            assertThat(result.last()).isFalse();
        }
    }

    @Nested
    @DisplayName("findOrganizationDetail 메서드")
    class FindOrganizationDetailTest {

        @Test
        @DisplayName("조직 상세 정보를 성공적으로 조회한다")
        void shouldFindOrganizationDetailSuccessfully() {
            // given
            OrganizationId organizationId = OrganizationId.of(ORGANIZATION_UUID);
            OrganizationDetailResponse detailResponse =
                    new OrganizationDetailResponse(
                            ORGANIZATION_UUID,
                            TENANT_UUID,
                            "테스트 테넌트",
                            "테스트 조직",
                            "ACTIVE",
                            List.of(),
                            5,
                            Instant.now(),
                            Instant.now());

            given(repository.findOrganizationDetail(ORGANIZATION_UUID))
                    .willReturn(Optional.of(detailResponse));

            // when
            Optional<OrganizationDetailResponse> result =
                    adapter.findOrganizationDetail(organizationId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().organizationId()).isEqualTo(ORGANIZATION_UUID);
            verify(repository).findOrganizationDetail(ORGANIZATION_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 조직은 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenOrganizationNotFound() {
            // given
            UUID nonExistingOrgId = UUID.randomUUID();
            OrganizationId organizationId = OrganizationId.of(nonExistingOrgId);

            given(repository.findOrganizationDetail(nonExistingOrgId)).willReturn(Optional.empty());

            // when
            Optional<OrganizationDetailResponse> result =
                    adapter.findOrganizationDetail(organizationId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
