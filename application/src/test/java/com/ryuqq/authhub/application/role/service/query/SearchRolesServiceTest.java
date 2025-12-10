package com.ryuqq.authhub.application.role.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
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
                    new SearchRolesQuery(RoleFixture.defaultTenantUUID(), null, null, null, 0, 20);

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
            given(assembler.toResponse(any(Role.class))).willReturn(response1, response2);

            // when
            List<RoleResponse> responses = service.execute(query);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0)).isNotNull();
            assertThat(responses.get(1)).isNotNull();
            verify(readManager).search(query);
        }

        @Test
        @DisplayName("역할이 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoRolesFound() {
            // given
            SearchRolesQuery query =
                    new SearchRolesQuery(RoleFixture.defaultTenantUUID(), null, null, null, 0, 20);

            given(readManager.search(query)).willReturn(List.of());

            // when
            List<RoleResponse> responses = service.execute(query);

            // then
            assertThat(responses).isEmpty();
            verify(readManager).search(query);
        }
    }
}
