package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.tenant.assembler.TenantAssembler;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.factory.command.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.InvalidTenantStateException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
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
 * UpdateTenantStatusService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTenantStatusService 단위 테스트")
class UpdateTenantStatusServiceTest {

    @Mock private TenantReadManager readManager;

    @Mock private TenantCommandFactory commandFactory;

    @Mock private TenantTransactionManager transactionManager;

    @Mock private TenantAssembler assembler;

    private Clock clock;

    private UpdateTenantStatusService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneOffset.UTC);
        service =
                new UpdateTenantStatusService(
                        readManager, commandFactory, transactionManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("ACTIVE 테넌트를 INACTIVE로 변경한다")
        void shouldDeactivateTenant() {
            // given
            Tenant activeTenant = TenantFixture.create(); // ACTIVE 상태
            UpdateTenantStatusCommand command =
                    new UpdateTenantStatusCommand(TenantFixture.defaultUUID(), "INACTIVE");

            Tenant deactivatedTenant = activeTenant.deactivate(clock);
            TenantResponse expectedResponse =
                    new TenantResponse(
                            deactivatedTenant.tenantIdValue(),
                            deactivatedTenant.nameValue(),
                            deactivatedTenant.statusValue(),
                            deactivatedTenant.createdAt(),
                            deactivatedTenant.updatedAt());

            given(readManager.findById(any(TenantId.class))).willReturn(activeTenant);
            given(commandFactory.applyStatusChange(any(Tenant.class), eq("INACTIVE")))
                    .willReturn(deactivatedTenant);
            given(transactionManager.persist(any(Tenant.class))).willReturn(deactivatedTenant);
            given(assembler.toResponse(deactivatedTenant)).willReturn(expectedResponse);

            // when
            TenantResponse response = service.execute(command);

            // then
            assertThat(response.status()).isEqualTo("INACTIVE");
            verify(readManager).findById(any(TenantId.class));
            verify(commandFactory).applyStatusChange(any(Tenant.class), eq("INACTIVE"));
            verify(transactionManager).persist(any(Tenant.class));
            verify(assembler).toResponse(any(Tenant.class));
        }

        @Test
        @DisplayName("INACTIVE 테넌트를 ACTIVE로 변경한다")
        void shouldActivateTenant() {
            // given
            Tenant inactiveTenant = TenantFixture.createInactive();
            UpdateTenantStatusCommand command =
                    new UpdateTenantStatusCommand(TenantFixture.defaultUUID(), "ACTIVE");

            Tenant activatedTenant = inactiveTenant.activate(clock);
            TenantResponse expectedResponse =
                    new TenantResponse(
                            activatedTenant.tenantIdValue(),
                            activatedTenant.nameValue(),
                            activatedTenant.statusValue(),
                            activatedTenant.createdAt(),
                            activatedTenant.updatedAt());

            given(readManager.findById(any(TenantId.class))).willReturn(inactiveTenant);
            given(commandFactory.applyStatusChange(any(Tenant.class), eq("ACTIVE")))
                    .willReturn(activatedTenant);
            given(transactionManager.persist(any(Tenant.class))).willReturn(activatedTenant);
            given(assembler.toResponse(activatedTenant)).willReturn(expectedResponse);

            // when
            TenantResponse response = service.execute(command);

            // then
            assertThat(response.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            UpdateTenantStatusCommand command =
                    new UpdateTenantStatusCommand(TenantFixture.defaultUUID(), "INACTIVE");
            given(readManager.findById(any(TenantId.class)))
                    .willThrow(new TenantNotFoundException(TenantFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(TenantNotFoundException.class);
        }

        @Test
        @DisplayName("삭제된 테넌트 상태 변경 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantIsDeleted() {
            // given
            Tenant deletedTenant = TenantFixture.createDeleted();
            UpdateTenantStatusCommand command =
                    new UpdateTenantStatusCommand(TenantFixture.defaultUUID(), "ACTIVE");

            given(readManager.findById(any(TenantId.class))).willReturn(deletedTenant);
            given(commandFactory.applyStatusChange(any(Tenant.class), eq("ACTIVE")))
                    .willThrow(
                            new InvalidTenantStateException(deletedTenant.getStatus(), "ACTIVE"));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidTenantStateException.class);
        }

        @Test
        @DisplayName("DELETED 상태로 변경 시도 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTargetStatusIsDeleted() {
            // given
            Tenant activeTenant = TenantFixture.create();
            UpdateTenantStatusCommand command =
                    new UpdateTenantStatusCommand(TenantFixture.defaultUUID(), "DELETED");

            given(readManager.findById(any(TenantId.class))).willReturn(activeTenant);
            given(commandFactory.applyStatusChange(any(Tenant.class), eq("DELETED")))
                    .willThrow(
                            new IllegalArgumentException(
                                    "DELETED 상태로의 변경은 DeleteTenantUseCase를 사용하세요"));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("DeleteTenantUseCase를 사용하세요");
        }

        @Test
        @DisplayName("이미 ACTIVE 상태인 테넌트를 ACTIVE로 변경 시도 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenAlreadyActive() {
            // given
            Tenant activeTenant = TenantFixture.create();
            UpdateTenantStatusCommand command =
                    new UpdateTenantStatusCommand(TenantFixture.defaultUUID(), "ACTIVE");

            given(readManager.findById(any(TenantId.class))).willReturn(activeTenant);
            given(commandFactory.applyStatusChange(any(Tenant.class), eq("ACTIVE")))
                    .willThrow(new InvalidTenantStateException(activeTenant.getStatus(), "ACTIVE"));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidTenantStateException.class);
        }
    }
}
