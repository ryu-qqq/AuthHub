package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.tenant.dto.command.DeleteTenantCommand;
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
 * DeleteTenantService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteTenantService 단위 테스트")
class DeleteTenantServiceTest {

    @Mock private TenantReadManager readManager;

    @Mock private TenantTransactionManager transactionManager;

    private Clock clock;

    private DeleteTenantService service;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2025-01-02T00:00:00Z"), ZoneOffset.UTC);
        service = new DeleteTenantService(readManager, transactionManager, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("ACTIVE 테넌트를 성공적으로 삭제한다")
        void shouldDeleteActiveTenant() {
            // given
            Tenant activeTenant = TenantFixture.create();
            DeleteTenantCommand command = new DeleteTenantCommand(TenantFixture.defaultUUID());

            Tenant deletedTenant = activeTenant.delete(clock);

            given(readManager.findById(any(TenantId.class))).willReturn(activeTenant);
            given(transactionManager.persist(any(Tenant.class))).willReturn(deletedTenant);

            // when & then
            assertThatCode(() -> service.execute(command)).doesNotThrowAnyException();
            verify(readManager).findById(any(TenantId.class));
            verify(transactionManager).persist(any(Tenant.class));
        }

        @Test
        @DisplayName("INACTIVE 테넌트를 성공적으로 삭제한다")
        void shouldDeleteInactiveTenant() {
            // given
            Tenant inactiveTenant = TenantFixture.createInactive();
            DeleteTenantCommand command = new DeleteTenantCommand(TenantFixture.defaultUUID());

            Tenant deletedTenant = inactiveTenant.delete(clock);

            given(readManager.findById(any(TenantId.class))).willReturn(inactiveTenant);
            given(transactionManager.persist(any(Tenant.class))).willReturn(deletedTenant);

            // when & then
            assertThatCode(() -> service.execute(command)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantNotFound() {
            // given
            DeleteTenantCommand command = new DeleteTenantCommand(TenantFixture.defaultUUID());
            given(readManager.findById(any(TenantId.class)))
                    .willThrow(new TenantNotFoundException(TenantFixture.defaultId()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(TenantNotFoundException.class);
        }

        @Test
        @DisplayName("이미 삭제된 테넌트 삭제 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenTenantAlreadyDeleted() {
            // given
            Tenant deletedTenant = TenantFixture.createDeleted();
            DeleteTenantCommand command = new DeleteTenantCommand(TenantFixture.defaultUUID());

            given(readManager.findById(any(TenantId.class))).willReturn(deletedTenant);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidTenantStateException.class);
        }
    }
}
