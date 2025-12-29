package com.ryuqq.authhub.application.role.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;
import com.ryuqq.authhub.application.role.port.out.query.RoleAdminQueryPort;
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
 * SearchRolesAdminService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchRolesAdminService 단위 테스트")
class SearchRolesAdminServiceTest {

    @Mock private RoleAdminQueryPort adminQueryPort;

    private SearchRolesAdminService service;

    private static final UUID TENANT_UUID = UUID.randomUUID();
    private static final Instant CREATED_FROM = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant CREATED_TO = Instant.parse("2025-12-31T23:59:59Z");

    @BeforeEach
    void setUp() {
        service = new SearchRolesAdminService(adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할 목록을 성공적으로 검색한다")
        void shouldSearchRolesSuccessfully() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.ofAdmin(
                            TENANT_UUID,
                            null,
                            null,
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            "createdAt",
                            "DESC",
                            0,
                            20);
            RoleSummaryResponse roleSummary =
                    new RoleSummaryResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "테스트 테넌트",
                            "관리자",
                            "관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            3,
                            5,
                            Instant.now(),
                            Instant.now());
            PageResponse<RoleSummaryResponse> expectedResponse =
                    PageResponse.of(List.of(roleSummary), 0, 20, 1L, 1, true, true);

            given(adminQueryPort.searchRoles(query)).willReturn(expectedResponse);

            // when
            PageResponse<RoleSummaryResponse> response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.content()).hasSize(1);
            verify(adminQueryPort).searchRoles(query);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoResults() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.ofAdmin(
                            TENANT_UUID,
                            "nonexistent",
                            null,
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            "createdAt",
                            "DESC",
                            0,
                            20);
            PageResponse<RoleSummaryResponse> emptyResponse =
                    PageResponse.of(List.of(), 0, 20, 0L, 0, true, true);

            given(adminQueryPort.searchRoles(query)).willReturn(emptyResponse);

            // when
            PageResponse<RoleSummaryResponse> response = service.execute(query);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("범위와 유형 필터로 역할을 검색한다")
        void shouldSearchRolesWithScopeAndTypeFilter() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.ofAdmin(
                            TENANT_UUID,
                            null,
                            null,
                            List.of("TENANT"),
                            List.of("CUSTOM"),
                            CREATED_FROM,
                            CREATED_TO,
                            "createdAt",
                            "DESC",
                            0,
                            20);
            RoleSummaryResponse roleSummary =
                    new RoleSummaryResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "테스트 테넌트",
                            "커스텀 역할",
                            "커스텀 역할 설명",
                            "TENANT",
                            "CUSTOM",
                            2,
                            3,
                            Instant.now(),
                            Instant.now());
            PageResponse<RoleSummaryResponse> expectedResponse =
                    PageResponse.of(List.of(roleSummary), 0, 20, 1L, 1, true, true);

            given(adminQueryPort.searchRoles(query)).willReturn(expectedResponse);

            // when
            PageResponse<RoleSummaryResponse> response = service.execute(query);

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).scope()).isEqualTo("TENANT");
            assertThat(response.content().get(0).type()).isEqualTo("CUSTOM");
        }
    }
}
