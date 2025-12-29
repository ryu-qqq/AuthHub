package com.ryuqq.authhub.application.role.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import java.time.Instant;
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
 * SearchRolesService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchRolesService 단위 테스트")
class SearchRolesServiceTest {

    @Mock private RoleReadManager readManager;

    @Mock private RoleAssembler assembler;

    private SearchRolesService service;

    private static final Instant CREATED_FROM = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant CREATED_TO = Instant.parse("2025-12-31T23:59:59Z");

    @BeforeEach
    void setUp() {
        service = new SearchRolesService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할 목록을 성공적으로 검색한다")
        void shouldSearchRolesSuccessfully() {
            // given
            Role role1 = RoleFixture.create();
            Role role2 = RoleFixture.createWithName("ANOTHER_ROLE");
            SearchRolesQuery query =
                    SearchRolesQuery.of(
                            RoleFixture.defaultTenantUUID(),
                            null,
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            0,
                            20);

            RoleResponse response1 =
                    new RoleResponse(
                            role1.roleIdValue(),
                            role1.tenantIdValue(),
                            role1.nameValue(),
                            role1.descriptionValue(),
                            role1.getScope().name(),
                            role1.getType().name(),
                            role1.createdAt(),
                            role1.updatedAt());
            RoleResponse response2 =
                    new RoleResponse(
                            role2.roleIdValue(),
                            role2.tenantIdValue(),
                            "ANOTHER_ROLE",
                            role2.descriptionValue(),
                            role2.getScope().name(),
                            role2.getType().name(),
                            role2.createdAt(),
                            role2.updatedAt());

            given(readManager.search(query)).willReturn(List.of(role1, role2));
            given(readManager.count(query)).willReturn(2L);
            given(assembler.toResponse(any(Role.class))).willReturn(response1, response2);

            // when
            PageResponse<RoleResponse> pageResponse = service.execute(query);

            // then
            assertThat(pageResponse).isNotNull();
            assertThat(pageResponse.content()).hasSize(2);
            assertThat(pageResponse.totalElements()).isEqualTo(2L);
            assertThat(pageResponse.totalPages()).isEqualTo(1);
            assertThat(pageResponse.first()).isTrue();
            assertThat(pageResponse.last()).isTrue();
            verify(readManager).search(query);
            verify(readManager).count(query);
        }

        @Test
        @DisplayName("역할이 없으면 빈 페이지를 반환한다")
        void shouldReturnEmptyPageWhenNoRolesFound() {
            // given
            SearchRolesQuery query =
                    SearchRolesQuery.of(
                            RoleFixture.defaultTenantUUID(),
                            null,
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            0,
                            20);

            given(readManager.search(query)).willReturn(List.of());
            given(readManager.count(query)).willReturn(0L);

            // when
            PageResponse<RoleResponse> pageResponse = service.execute(query);

            // then
            assertThat(pageResponse).isNotNull();
            assertThat(pageResponse.content()).isEmpty();
            assertThat(pageResponse.totalElements()).isEqualTo(0L);
            assertThat(pageResponse.totalPages()).isEqualTo(0);
            assertThat(pageResponse.first()).isTrue();
            assertThat(pageResponse.last()).isTrue();
            verify(readManager).search(query);
            verify(readManager).count(query);
        }

        @Test
        @DisplayName("페이징 정보가 올바르게 계산된다")
        void shouldCalculatePaginationCorrectly() {
            // given
            Role role = RoleFixture.create();
            SearchRolesQuery query =
                    SearchRolesQuery.of(
                            RoleFixture.defaultTenantUUID(),
                            null,
                            null,
                            null,
                            CREATED_FROM,
                            CREATED_TO,
                            1,
                            10);

            RoleResponse response =
                    new RoleResponse(
                            role.roleIdValue(),
                            role.tenantIdValue(),
                            role.nameValue(),
                            role.descriptionValue(),
                            role.getScope().name(),
                            role.getType().name(),
                            role.createdAt(),
                            role.updatedAt());

            given(readManager.search(query)).willReturn(List.of(role));
            given(readManager.count(query)).willReturn(25L);
            given(assembler.toResponse(any(Role.class))).willReturn(response);

            // when
            PageResponse<RoleResponse> pageResponse = service.execute(query);

            // then
            assertThat(pageResponse.page()).isEqualTo(1);
            assertThat(pageResponse.size()).isEqualTo(10);
            assertThat(pageResponse.totalElements()).isEqualTo(25L);
            assertThat(pageResponse.totalPages()).isEqualTo(3);
            assertThat(pageResponse.first()).isFalse();
            assertThat(pageResponse.last()).isFalse();
        }
    }
}
