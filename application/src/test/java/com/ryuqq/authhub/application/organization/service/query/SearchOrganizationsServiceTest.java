package com.ryuqq.authhub.application.organization.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.factory.query.OrganizationQueryFactory;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
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
 * SearchOrganizationsService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchOrganizationsService 단위 테스트")
class SearchOrganizationsServiceTest {

    @Mock private OrganizationQueryFactory queryFactory;

    @Mock private OrganizationReadManager readManager;

    @Mock private OrganizationAssembler assembler;

    private SearchOrganizationsService service;

    @BeforeEach
    void setUp() {
        service = new SearchOrganizationsService(queryFactory, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("조직 목록을 성공적으로 조회한다")
        void shouldSearchOrganizationsSuccessfully() {
            // given
            Organization organization = OrganizationFixture.create();
            List<Organization> organizations = List.of(organization);
            SearchOrganizationsQuery query =
                    SearchOrganizationsQuery.of(OrganizationFixture.defaultTenantUUID(), 0, 20);

            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(OrganizationFixture.defaultTenantUUID()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));

            OrganizationResponse orgResponse =
                    new OrganizationResponse(
                            organization.organizationIdValue(),
                            organization.tenantIdValue(),
                            organization.nameValue(),
                            organization.statusValue(),
                            organization.createdAt(),
                            organization.updatedAt());

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(organizations);
            given(readManager.countByCriteria(criteria)).willReturn(1L);
            given(assembler.toResponseList(organizations)).willReturn(List.of(orgResponse));

            // when
            PageResponse<OrganizationResponse> result = service.execute(query);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.first()).isTrue();
            assertThat(result.last()).isTrue();
            verify(queryFactory).toCriteria(query);
            verify(readManager).findAllByCriteria(criteria);
            verify(readManager).countByCriteria(criteria);
        }

        @Test
        @DisplayName("이름 필터로 조직을 검색한다")
        void shouldSearchOrganizationsByName() {
            // given
            Organization organization = OrganizationFixture.createWithName("Test Org");
            List<Organization> organizations = List.of(organization);
            SearchOrganizationsQuery query =
                    new SearchOrganizationsQuery(
                            OrganizationFixture.defaultTenantUUID(), "Test", null, 0, 20);

            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(OrganizationFixture.defaultTenantUUID()),
                            "Test",
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));

            OrganizationResponse orgResponse =
                    new OrganizationResponse(
                            organization.organizationIdValue(),
                            organization.tenantIdValue(),
                            organization.nameValue(),
                            organization.statusValue(),
                            organization.createdAt(),
                            organization.updatedAt());

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(organizations);
            given(readManager.countByCriteria(criteria)).willReturn(1L);
            given(assembler.toResponseList(organizations)).willReturn(List.of(orgResponse));

            // when
            PageResponse<OrganizationResponse> result = service.execute(query);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).contains("Test");
        }

        @Test
        @DisplayName("상태 필터로 조직을 검색한다")
        void shouldSearchOrganizationsByStatus() {
            // given
            Organization inactiveOrganization = OrganizationFixture.createInactive();
            List<Organization> organizations = List.of(inactiveOrganization);
            SearchOrganizationsQuery query =
                    new SearchOrganizationsQuery(
                            OrganizationFixture.defaultTenantUUID(), null, "INACTIVE", 0, 20);

            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(OrganizationFixture.defaultTenantUUID()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));

            OrganizationResponse orgResponse =
                    new OrganizationResponse(
                            inactiveOrganization.organizationIdValue(),
                            inactiveOrganization.tenantIdValue(),
                            inactiveOrganization.nameValue(),
                            inactiveOrganization.statusValue(),
                            inactiveOrganization.createdAt(),
                            inactiveOrganization.updatedAt());

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(organizations);
            given(readManager.countByCriteria(criteria)).willReturn(1L);
            given(assembler.toResponseList(organizations)).willReturn(List.of(orgResponse));

            // when
            PageResponse<OrganizationResponse> result = service.execute(query);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("빈 결과 조회 시 빈 PageResponse를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
            SearchOrganizationsQuery query =
                    SearchOrganizationsQuery.of(OrganizationFixture.defaultTenantUUID());

            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(OrganizationFixture.defaultTenantUUID()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(Collections.emptyList());
            given(readManager.countByCriteria(criteria)).willReturn(0L);
            given(assembler.toResponseList(Collections.emptyList()))
                    .willReturn(Collections.emptyList());

            // when
            PageResponse<OrganizationResponse> result = service.execute(query);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
            assertThat(result.first()).isTrue();
            assertThat(result.last()).isTrue();
        }

        @Test
        @DisplayName("페이지네이션이 정확하게 동작한다")
        void shouldPaginateCorrectly() {
            // given
            List<Organization> organizations = new java.util.ArrayList<>();
            List<OrganizationResponse> orgResponses = new java.util.ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Organization org = OrganizationFixture.createWithName("Org" + i);
                organizations.add(org);
                orgResponses.add(
                        new OrganizationResponse(
                                org.organizationIdValue(),
                                org.tenantIdValue(),
                                org.nameValue(),
                                org.statusValue(),
                                org.createdAt(),
                                org.updatedAt()));
            }
            SearchOrganizationsQuery query =
                    new SearchOrganizationsQuery(
                            OrganizationFixture.defaultTenantUUID(), null, null, 1, 10);

            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(OrganizationFixture.defaultTenantUUID()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(1, 10));

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(organizations);
            given(readManager.countByCriteria(criteria)).willReturn(25L);
            given(assembler.toResponseList(organizations)).willReturn(orgResponses);

            // when
            PageResponse<OrganizationResponse> result = service.execute(query);

            // then
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(25L);
            assertThat(result.totalPages()).isEqualTo(3);
            assertThat(result.first()).isFalse();
            assertThat(result.last()).isFalse();
        }

        @Test
        @DisplayName("마지막 페이지 조회 시 last가 true이다")
        void shouldReturnIsLastTrueWhenLastPage() {
            // given
            Organization organization = OrganizationFixture.create();
            List<Organization> organizations = List.of(organization);
            SearchOrganizationsQuery query =
                    new SearchOrganizationsQuery(
                            OrganizationFixture.defaultTenantUUID(), null, null, 2, 10);

            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            TenantId.of(OrganizationFixture.defaultTenantUUID()),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(2, 10));

            OrganizationResponse orgResponse =
                    new OrganizationResponse(
                            organization.organizationIdValue(),
                            organization.tenantIdValue(),
                            organization.nameValue(),
                            organization.statusValue(),
                            organization.createdAt(),
                            organization.updatedAt());

            given(queryFactory.toCriteria(query)).willReturn(criteria);
            given(readManager.findAllByCriteria(criteria)).willReturn(organizations);
            given(readManager.countByCriteria(criteria)).willReturn(25L);
            given(assembler.toResponseList(organizations)).willReturn(List.of(orgResponse));

            // when
            PageResponse<OrganizationResponse> result = service.execute(query);

            // then
            assertThat(result.page()).isEqualTo(2);
            assertThat(result.last()).isTrue();
        }
    }
}
