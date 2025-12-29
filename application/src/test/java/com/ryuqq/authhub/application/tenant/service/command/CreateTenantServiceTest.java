package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.factory.command.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateTenantService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTenantService 단위 테스트")
class CreateTenantServiceTest {

    @Mock private TenantValidator validator;

    @Mock private TenantCommandFactory commandFactory;

    @Mock private TenantTransactionManager transactionManager;

    @Mock private TenantAssembler assembler;

    private CreateTenantService service;

    @BeforeEach
    void setUp() {
        service = new CreateTenantService(validator, commandFactory, transactionManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("테넌트를 성공적으로 생성한다")
        void shouldCreateTenantSuccessfully() {
            // given
            CreateTenantCommand command = new CreateTenantCommand("New Tenant");
            Tenant newTenant = TenantFixture.createNew();
            Tenant savedTenant = TenantFixture.create();
            TenantResponse expectedResponse =
                    new TenantResponse(
                            savedTenant.tenantIdValue(),
                            savedTenant.nameValue(),
                            savedTenant.statusValue(),
                            savedTenant.createdAt(),
                            savedTenant.updatedAt());

            // validator는 예외를 던지지 않으면 통과 (doNothing 기본 동작)
            given(commandFactory.create(command)).willReturn(newTenant);
            given(transactionManager.persist(newTenant)).willReturn(savedTenant);
            given(assembler.toResponse(savedTenant)).willReturn(expectedResponse);

            // when
            TenantResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(validator).validateNameNotDuplicated(any(TenantName.class));
            verify(commandFactory).create(command);
            verify(transactionManager).persist(newTenant);
            verify(assembler).toResponse(savedTenant);
        }

        @Test
        @DisplayName("중복 이름 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateName() {
            // given
            CreateTenantCommand command = new CreateTenantCommand("Existing Tenant");
            org.mockito.Mockito.doThrow(
                            new DuplicateTenantNameException(TenantName.of("Existing Tenant")))
                    .when(validator)
                    .validateNameNotDuplicated(any(TenantName.class));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateTenantNameException.class);
        }
    }
}
