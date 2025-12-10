package com.ryuqq.authhub.application.user.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.factory.command.UserCommandFactory;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.application.user.manager.query.UserReadManager;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserIdentifierException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateUserService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserService 단위 테스트")
class CreateUserServiceTest {

    @Mock private UserCommandFactory commandFactory;

    @Mock private UserTransactionManager transactionManager;

    @Mock private UserReadManager readManager;

    @Mock private UserAssembler assembler;

    private CreateUserService service;

    @BeforeEach
    void setUp() {
        service = new CreateUserService(commandFactory, transactionManager, readManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자를 성공적으로 생성한다")
        void shouldCreateUserSuccessfully() {
            // given
            CreateUserCommand command =
                    new CreateUserCommand(
                            UserFixture.defaultTenantUUID(),
                            UserFixture.defaultOrganizationUUID(),
                            "newuser@example.com",
                            "password123");
            User newUser = UserFixture.createNew();
            User savedUser = UserFixture.create();
            UserResponse expectedResponse =
                    new UserResponse(
                            savedUser.userIdValue(),
                            savedUser.tenantIdValue(),
                            savedUser.organizationIdValue(),
                            savedUser.getIdentifier(),
                            savedUser.getUserStatus().name(),
                            savedUser.getCreatedAt(),
                            savedUser.getUpdatedAt());

            given(
                            readManager.existsByTenantIdAndOrganizationIdAndIdentifier(
                                    any(TenantId.class),
                                    any(OrganizationId.class),
                                    eq(command.identifier())))
                    .willReturn(false);
            given(commandFactory.create(command)).willReturn(newUser);
            given(transactionManager.persist(newUser)).willReturn(savedUser);
            given(assembler.toResponse(savedUser)).willReturn(expectedResponse);

            // when
            UserResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(readManager)
                    .existsByTenantIdAndOrganizationIdAndIdentifier(
                            any(TenantId.class),
                            any(OrganizationId.class),
                            eq(command.identifier()));
            verify(commandFactory).create(command);
            verify(transactionManager).persist(newUser);
            verify(assembler).toResponse(savedUser);
        }

        @Test
        @DisplayName("중복 식별자 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateIdentifier() {
            // given
            CreateUserCommand command =
                    new CreateUserCommand(
                            UserFixture.defaultTenantUUID(),
                            UserFixture.defaultOrganizationUUID(),
                            "existing@example.com",
                            "password123");

            given(
                            readManager.existsByTenantIdAndOrganizationIdAndIdentifier(
                                    any(TenantId.class),
                                    any(OrganizationId.class),
                                    eq(command.identifier())))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateUserIdentifierException.class);

            verify(commandFactory, never()).create(any());
            verify(transactionManager, never()).persist(any());
            verify(assembler, never()).toResponse(any());
        }
    }
}
