package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleAdminQueryDslRepository;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
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
 * RoleAdminQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleAdminQueryAdapter 단위 테스트")
class RoleAdminQueryAdapterTest {

    @Mock private RoleAdminQueryDslRepository repository;

    private RoleAdminQueryAdapter adapter;

    private static final UUID ROLE_UUID = UUID.randomUUID();
    private static final UUID TENANT_UUID = UUID.randomUUID();
    private static final Instant CREATED_FROM = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant CREATED_TO = Instant.parse("2025-12-31T23:59:59Z");

    @BeforeEach
    void setUp() {
        adapter = new RoleAdminQueryAdapter(repository);
    }

    @Nested
    @DisplayName("searchRoles 메서드")
    class SearchRolesTest {

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

            given(repository.searchRoles(query)).willReturn(List.of(roleSummary));
            given(repository.countRoles(query)).willReturn(1L);

            // when
            PageResponse<RoleSummaryResponse> result = adapter.searchRoles(query);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            verify(repository).searchRoles(query);
            verify(repository).countRoles(query);
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

            given(repository.searchRoles(query)).willReturn(List.of());
            given(repository.countRoles(query)).willReturn(0L);

            // when
            PageResponse<RoleSummaryResponse> result = adapter.searchRoles(query);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("findRoleDetail 메서드")
    class FindRoleDetailTest {

        @Test
        @DisplayName("역할 상세 정보를 성공적으로 조회한다")
        void shouldFindRoleDetailSuccessfully() {
            // given
            RoleId roleId = RoleId.of(ROLE_UUID);
            RoleDetailResponse detailResponse =
                    new RoleDetailResponse(
                            ROLE_UUID,
                            TENANT_UUID,
                            "테스트 테넌트",
                            "관리자",
                            "관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            List.of(),
                            5,
                            Instant.now(),
                            Instant.now());

            given(repository.findRoleDetail(ROLE_UUID)).willReturn(Optional.of(detailResponse));

            // when
            Optional<RoleDetailResponse> result = adapter.findRoleDetail(roleId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().roleId()).isEqualTo(ROLE_UUID);
            verify(repository).findRoleDetail(ROLE_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 역할은 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenRoleNotFound() {
            // given
            UUID nonExistingRoleId = UUID.randomUUID();
            RoleId roleId = RoleId.of(nonExistingRoleId);

            given(repository.findRoleDetail(nonExistingRoleId)).willReturn(Optional.empty());

            // when
            Optional<RoleDetailResponse> result = adapter.findRoleDetail(roleId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countRoles 메서드")
    class CountRolesTest {

        @Test
        @DisplayName("역할 수를 성공적으로 조회한다")
        void shouldCountRolesSuccessfully() {
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

            given(repository.countRoles(query)).willReturn(10L);

            // when
            long count = adapter.countRoles(query);

            // then
            assertThat(count).isEqualTo(10L);
            verify(repository).countRoles(query);
        }
    }
}
