package com.ryuqq.authhub.application.role.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetRoleService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetRoleService 단위 테스트")
class GetRoleServiceTest {

    @Mock private RoleReadManager readManager;

    @Mock private RoleAssembler assembler;

    private GetRoleService service;

    @BeforeEach
    void setUp() {
        service = new GetRoleService(readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할을 성공적으로 조회한다")
        void shouldRetrieveRoleSuccessfully() {
            // given
            Role role = RoleFixture.create();
            GetRoleQuery query = new GetRoleQuery(role.roleIdValue());
            RoleResponse expectedResponse =
                    new RoleResponse(
                            role.roleIdValue(),
                            role.tenantIdValue(),
                            role.nameValue(),
                            role.descriptionValue(),
                            role.getScope().name(),
                            role.getType().name(),
                            role.createdAt(),
                            role.updatedAt());

            given(readManager.getById(RoleId.of(query.roleId()))).willReturn(role);
            given(assembler.toResponse(role)).willReturn(expectedResponse);

            // when
            RoleResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.roleId()).isEqualTo(role.roleIdValue());
            verify(readManager).getById(RoleId.of(query.roleId()));
            verify(assembler).toResponse(role);
        }

        @Test
        @DisplayName("존재하지 않는 역할 조회 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenRoleNotFound() {
            // given
            GetRoleQuery query = new GetRoleQuery(RoleFixture.defaultUUID());

            given(readManager.getById(RoleId.of(query.roleId())))
                    .willThrow(new RoleNotFoundException(query.roleId()));

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(RoleNotFoundException.class);
        }
    }
}
