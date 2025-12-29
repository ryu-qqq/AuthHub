package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.factory.command.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
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
 * UpdateTenantNameService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTenantNameService 단위 테스트")
class UpdateTenantNameServiceTest {

    @Mock private TenantReadManager readManager;

    @Mock private TenantValidator validator;

    @Mock private TenantCommandFactory commandFactory;

    @Mock private TenantTransactionManager transactionManager;

    @Mock private TenantAssembler assembler;

    private Clock clock;

    private UpdateTenantNameService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneOffset.UTC);
        service =
                new UpdateTenantNameService(
                        readManager, validator, commandFactory, transactionManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("테넌트 이름을 성공적으로 변경한다")
        void shouldUpdateTenantNameSuccessfully() {
            // given
            Tenant existingTenant = TenantFixture.create();
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "New Name");

            TenantName newName = TenantName.of("New Name");
            Tenant updatedTenant = existingTenant.changeName(newName, clock);
            TenantResponse expectedResponse =
                    new TenantResponse(
                            updatedTenant.tenantIdValue(),
                            updatedTenant.nameValue(),
                            updatedTenant.statusValue(),
                            updatedTenant.createdAt(),
                            updatedTenant.updatedAt());

            given(readManager.findById(any(TenantId.class))).willReturn(existingTenant);
            given(commandFactory.toName("New Name")).willReturn(newName);
            // validator는 예외를 던지지 않으면 통과 (doNothing 기본 동작)
            given(commandFactory.applyNameChange(any(Tenant.class), any(TenantName.class)))
                    .willReturn(updatedTenant);
            given(transactionManager.persist(any(Tenant.class))).willReturn(updatedTenant);
            given(assembler.toResponse(updatedTenant)).willReturn(expectedResponse);

            // when
            TenantResponse response = service.execute(command);

            // then
            assertThat(response.name()).isEqualTo("New Name");
            verify(readManager).findById(any(TenantId.class));
            verify(commandFactory).toName("New Name");
            verify(validator)
                    .validateNameNotDuplicatedExcluding(
                            any(TenantName.class), any(TenantName.class));
            verify(commandFactory).applyNameChange(any(Tenant.class), any(TenantName.class));
            verify(transactionManager).persist(any(Tenant.class));
            verify(assembler).toResponse(any(Tenant.class));
        }

        @Test
        @DisplayName("같은 이름으로 변경 시에도 Validator가 호출된다")
        void shouldCallValidatorEvenWhenSameName() {
            // given
            Tenant existingTenant = TenantFixture.createWithName("Same Name");
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "Same Name");

            TenantName sameName = TenantName.of("Same Name");
            Tenant updatedTenant = existingTenant.changeName(sameName, clock);
            TenantResponse expectedResponse =
                    new TenantResponse(
                            updatedTenant.tenantIdValue(),
                            updatedTenant.nameValue(),
                            updatedTenant.statusValue(),
                            updatedTenant.createdAt(),
                            updatedTenant.updatedAt());

            given(readManager.findById(any(TenantId.class))).willReturn(existingTenant);
            given(commandFactory.toName("Same Name")).willReturn(sameName);
            // validator 내부에서 같은 이름이면 중복 검사를 건너뜀
            given(commandFactory.applyNameChange(any(Tenant.class), any(TenantName.class)))
                    .willReturn(updatedTenant);
            given(transactionManager.persist(any(Tenant.class))).willReturn(updatedTenant);
            given(assembler.toResponse(updatedTenant)).willReturn(expectedResponse);

            // when
            TenantResponse response = service.execute(command);

            // then
            assertThat(response.name()).isEqualTo("Same Name");
            verify(commandFactory).toName("Same Name");
            verify(validator)
                    .validateNameNotDuplicatedExcluding(
                            any(TenantName.class), any(TenantName.class));
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "New Name");
            given(readManager.findById(any(TenantId.class)))
                    .willThrow(new TenantNotFoundException(TenantFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(TenantNotFoundException.class);
        }

        @Test
        @DisplayName("중복 이름 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateName() {
            // given
            Tenant existingTenant = TenantFixture.create();
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "Duplicate Name");

            TenantName duplicateName = TenantName.of("Duplicate Name");

            given(readManager.findById(any(TenantId.class))).willReturn(existingTenant);
            given(commandFactory.toName("Duplicate Name")).willReturn(duplicateName);
            org.mockito.Mockito.doThrow(new DuplicateTenantNameException(duplicateName))
                    .when(validator)
                    .validateNameNotDuplicatedExcluding(
                            any(TenantName.class), any(TenantName.class));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicateTenantNameException.class);
        }

        @Test
        @DisplayName("삭제된 테넌트 이름 변경 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantIsDeleted() {
            // given
            Tenant deletedTenant = TenantFixture.createDeleted();
            UpdateTenantNameCommand command =
                    new UpdateTenantNameCommand(TenantFixture.defaultUUID(), "New Name");

            TenantName newName = TenantName.of("New Name");

            given(readManager.findById(any(TenantId.class))).willReturn(deletedTenant);
            given(commandFactory.toName("New Name")).willReturn(newName);
            // validator는 통과, Factory에서 예외 발생
            given(commandFactory.applyNameChange(any(Tenant.class), any(TenantName.class)))
                    .willThrow(
                            new InvalidTenantStateException(
                                    deletedTenant.getStatus(), "cannot change name"));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidTenantStateException.class);
        }
    }
}
