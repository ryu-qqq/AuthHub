package com.ryuqq.authhub.application.organization.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.factory.command.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
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
 * CreateOrganizationService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrganizationService 단위 테스트")
class CreateOrganizationServiceTest {

    @Mock private OrganizationValidator validator;

    @Mock private OrganizationCommandFactory commandFactory;

    @Mock private OrganizationTransactionManager transactionManager;

    @Mock private OrganizationAssembler assembler;

    private CreateOrganizationService service;

    @BeforeEach
    void setUp() {
        service =
                new CreateOrganizationService(
                        validator, commandFactory, transactionManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("조직을 성공적으로 생성한다")
        void shouldCreateOrganizationSuccessfully() {
            // given
            CreateOrganizationCommand command =
                    new CreateOrganizationCommand(
                            OrganizationFixture.defaultTenantUUID(), "New Organization");
            Organization newOrganization = OrganizationFixture.createNew();
            Organization savedOrganization = OrganizationFixture.create();
            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            savedOrganization.organizationIdValue(),
                            savedOrganization.tenantIdValue(),
                            savedOrganization.nameValue(),
                            savedOrganization.statusValue(),
                            savedOrganization.createdAt(),
                            savedOrganization.updatedAt());

            // validator는 예외를 던지지 않으면 통과 (doNothing 기본 동작)
            given(commandFactory.create(command)).willReturn(newOrganization);
            given(transactionManager.persist(newOrganization)).willReturn(savedOrganization);
            given(assembler.toResponse(savedOrganization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(validator)
                    .validateNameNotDuplicated(any(TenantId.class), any(OrganizationName.class));
            verify(commandFactory).create(command);
            verify(transactionManager).persist(newOrganization);
            verify(assembler).toResponse(savedOrganization);
        }

        @Test
        @DisplayName("테넌트 내 중복 이름 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateName() {
            // given
            CreateOrganizationCommand command =
                    new CreateOrganizationCommand(
                            OrganizationFixture.defaultTenantUUID(), "Existing Org");
            org.mockito.Mockito.doThrow(
                            new DuplicateOrganizationNameException(
                                    TenantId.of(OrganizationFixture.defaultTenantUUID()),
                                    OrganizationName.of("Existing Org")))
                    .when(validator)
                    .validateNameNotDuplicated(any(TenantId.class), any(OrganizationName.class));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateOrganizationNameException.class);
        }
    }
}
