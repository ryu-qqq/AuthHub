package com.ryuqq.authhub.application.organization.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.organization.assembler.OrganizationAssembler;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.factory.command.OrganizationCommandFactory;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.organization.manager.query.OrganizationReadManager;
import com.ryuqq.authhub.application.organization.validator.OrganizationValidator;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.DuplicateOrganizationNameException;
import com.ryuqq.authhub.domain.organization.exception.InvalidOrganizationStateException;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UpdateOrganizationService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateOrganizationService 단위 테스트")
class UpdateOrganizationServiceTest {

    @Mock private OrganizationReadManager readManager;

    @Mock private OrganizationValidator validator;

    @Mock private OrganizationCommandFactory commandFactory;

    @Mock private OrganizationTransactionManager transactionManager;

    @Mock private OrganizationAssembler assembler;

    private Clock clock;

    private UpdateOrganizationService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneOffset.UTC);
        service =
                new UpdateOrganizationService(
                        readManager, validator, commandFactory, transactionManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("조직 이름을 성공적으로 변경한다")
        void shouldUpdateOrganizationNameSuccessfully() {
            // given
            Organization organization = OrganizationFixture.create();
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(
                            OrganizationFixture.defaultUUID(), "Updated Name");

            OrganizationName newName = OrganizationName.of("Updated Name");
            Organization updatedOrganization = organization.changeName(newName, clock);
            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            updatedOrganization.organizationIdValue(),
                            updatedOrganization.tenantIdValue(),
                            updatedOrganization.nameValue(),
                            updatedOrganization.statusValue(),
                            updatedOrganization.createdAt(),
                            updatedOrganization.updatedAt());

            given(readManager.findById(any(OrganizationId.class))).willReturn(organization);
            given(commandFactory.toName("Updated Name")).willReturn(newName);
            // validator는 예외를 던지지 않으면 통과 (doNothing 기본 동작)
            given(
                            commandFactory.applyNameChange(
                                    any(Organization.class), any(OrganizationName.class)))
                    .willReturn(updatedOrganization);
            given(transactionManager.persist(any(Organization.class)))
                    .willReturn(updatedOrganization);
            given(assembler.toResponse(updatedOrganization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(command);

            // then
            assertThat(response.name()).isEqualTo("Updated Name");
            verify(readManager).findById(any(OrganizationId.class));
            verify(validator)
                    .validateNameNotDuplicatedExcluding(
                            any(TenantId.class),
                            any(OrganizationName.class),
                            any(OrganizationName.class));
            verify(transactionManager).persist(any(Organization.class));
            verify(assembler).toResponse(any(Organization.class));
        }

        @Test
        @DisplayName("동일한 이름으로 변경 시에도 Validator가 호출된다")
        void shouldCallValidatorEvenWhenSameName() {
            // given
            Organization organization = OrganizationFixture.createWithName("Same Name");
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(OrganizationFixture.defaultUUID(), "Same Name");

            OrganizationName sameName = OrganizationName.of("Same Name");
            Organization updatedOrganization = organization.changeName(sameName, clock);
            OrganizationResponse expectedResponse =
                    new OrganizationResponse(
                            updatedOrganization.organizationIdValue(),
                            updatedOrganization.tenantIdValue(),
                            updatedOrganization.nameValue(),
                            updatedOrganization.statusValue(),
                            updatedOrganization.createdAt(),
                            updatedOrganization.updatedAt());

            given(readManager.findById(any(OrganizationId.class))).willReturn(organization);
            given(commandFactory.toName("Same Name")).willReturn(sameName);
            // validator 내부에서 같은 이름이면 중복 검사를 건너뜀
            given(
                            commandFactory.applyNameChange(
                                    any(Organization.class), any(OrganizationName.class)))
                    .willReturn(updatedOrganization);
            given(transactionManager.persist(any(Organization.class)))
                    .willReturn(updatedOrganization);
            given(assembler.toResponse(updatedOrganization)).willReturn(expectedResponse);

            // when
            OrganizationResponse response = service.execute(command);

            // then
            assertThat(response.name()).isEqualTo("Same Name");
            verify(validator)
                    .validateNameNotDuplicatedExcluding(
                            any(TenantId.class),
                            any(OrganizationName.class),
                            any(OrganizationName.class));
        }

        @Test
        @DisplayName("존재하지 않는 조직 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationNotFound() {
            // given
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(OrganizationFixture.defaultUUID(), "New Name");
            given(readManager.findById(any(OrganizationId.class)))
                    .willThrow(new OrganizationNotFoundException(OrganizationFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }

        @Test
        @DisplayName("테넌트 내 중복 이름 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateName() {
            // given
            Organization organization = OrganizationFixture.create();
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(
                            OrganizationFixture.defaultUUID(), "Duplicate Name");

            OrganizationName duplicateName = OrganizationName.of("Duplicate Name");

            given(readManager.findById(any(OrganizationId.class))).willReturn(organization);
            given(commandFactory.toName("Duplicate Name")).willReturn(duplicateName);
            org.mockito.Mockito.doThrow(
                            new DuplicateOrganizationNameException(
                                    organization.getTenantId(), duplicateName))
                    .when(validator)
                    .validateNameNotDuplicatedExcluding(
                            any(TenantId.class),
                            any(OrganizationName.class),
                            any(OrganizationName.class));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateOrganizationNameException.class);
        }

        @Test
        @DisplayName("삭제된 조직 수정 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenOrganizationIsDeleted() {
            // given
            Organization deletedOrganization = OrganizationFixture.createDeleted();
            UpdateOrganizationCommand command =
                    new UpdateOrganizationCommand(OrganizationFixture.defaultUUID(), "New Name");

            OrganizationName newName = OrganizationName.of("New Name");

            given(readManager.findById(any(OrganizationId.class))).willReturn(deletedOrganization);
            given(commandFactory.toName("New Name")).willReturn(newName);
            // validator는 통과, Factory에서 예외 발생
            given(
                            commandFactory.applyNameChange(
                                    any(Organization.class), any(OrganizationName.class)))
                    .willThrow(
                            new InvalidOrganizationStateException(
                                    deletedOrganization.getStatus(), "cannot change name"));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidOrganizationStateException.class);
        }
    }
}
