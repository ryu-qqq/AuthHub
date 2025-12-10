package com.ryuqq.authhub.application.role.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.assembler.RoleAssembler;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.factory.command.RoleCommandFactory;
import com.ryuqq.authhub.application.role.manager.command.RoleTransactionManager;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateRoleService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateRoleService 단위 테스트")
class CreateRoleServiceTest {

    @Mock private RoleCommandFactory commandFactory;

    @Mock private RoleTransactionManager transactionManager;

    @Mock private RoleReadManager readManager;

    @Mock private RoleAssembler assembler;

    private CreateRoleService service;

    @BeforeEach
    void setUp() {
        service = new CreateRoleService(commandFactory, transactionManager, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할을 성공적으로 생성한다")
        void shouldCreateRoleSuccessfully() {
            // given
            Role role = RoleFixture.create();
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            RoleFixture.defaultTenantUUID(),
                            "TEST_ROLE",
                            "Test role description",
                            "ORGANIZATION",
                            false);
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

            given(readManager.existsByTenantIdAndName(any(TenantId.class), any(RoleName.class)))
                    .willReturn(false);
            given(commandFactory.create(command)).willReturn(role);
            given(transactionManager.persist(role)).willReturn(role);
            given(assembler.toResponse(role)).willReturn(expectedResponse);

            // when
            RoleResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.name()).isEqualTo("TEST_ROLE");
            verify(commandFactory).create(command);
            verify(transactionManager).persist(role);
            verify(assembler).toResponse(role);
        }

        @Test
        @DisplayName("중복 역할 이름 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateName() {
            // given
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            RoleFixture.defaultTenantUUID(),
                            "DUPLICATE_ROLE",
                            "Description",
                            "ORGANIZATION",
                            false);

            given(readManager.existsByTenantIdAndName(any(TenantId.class), any(RoleName.class)))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateRoleNameException.class);

            verify(commandFactory, never()).create(any());
            verify(transactionManager, never()).persist(any());
        }

        @Test
        @DisplayName("GLOBAL 역할에서 중복 이름 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateGlobalRoleName() {
            // given
            CreateRoleCommand command =
                    new CreateRoleCommand(null, "GLOBAL_ROLE", "Description", "GLOBAL", true);

            given(readManager.existsByTenantIdAndName(null, RoleName.of("GLOBAL_ROLE")))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateRoleNameException.class);

            verify(commandFactory, never()).create(any());
            verify(transactionManager, never()).persist(any());
        }
    }
}
