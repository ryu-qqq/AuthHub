package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.factory.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.fixture.TenantCommandFixtures;
import com.ryuqq.authhub.application.tenant.manager.TenantCommandManager;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
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

    @Mock private TenantValidator validator;

    @Mock private TenantCommandFactory commandFactory;

    @Mock private TenantCommandManager commandManager;

    private UpdateTenantStatusService sut;

    @BeforeEach
    void setUp() {
        sut = new UpdateTenantStatusService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Manager 순서로 호출")
        void shouldOrchestrate_FactoryThenValidatorThenManager() {
            // given
            UpdateTenantStatusCommand command = TenantCommandFixtures.deactivateCommand();
            Tenant tenant = TenantFixture.create();
            StatusChangeContext<TenantId> context =
                    new StatusChangeContext<>(TenantId.of(command.tenantId()), Instant.now());

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(tenant);

            // when
            assertThatCode(() -> sut.execute(command)).doesNotThrowAnyException();

            // then
            then(commandFactory).should().createStatusChangeContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(tenant);
        }

        @Test
        @DisplayName("실패: 테넌트가 없으면 TenantNotFoundException 발생")
        void shouldThrowException_WhenTenantNotFound() {
            // given
            UpdateTenantStatusCommand command = TenantCommandFixtures.deactivateCommand();
            StatusChangeContext<TenantId> context =
                    new StatusChangeContext<>(TenantId.of(command.tenantId()), Instant.now());

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id()))
                    .willThrow(new TenantNotFoundException(context.id()));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TenantNotFoundException.class);
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("실패: 잘못된 status 문자열이면 IllegalArgumentException 발생")
        void shouldThrowException_WhenStatusIsInvalid() {
            // given
            UpdateTenantStatusCommand command =
                    new UpdateTenantStatusCommand(
                            TenantCommandFixtures.defaultTenantId(), "INVALID");

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(IllegalArgumentException.class);
            then(validator).should(never()).findExistingOrThrow(any());
            then(commandManager).should(never()).persist(any());
        }
    }
}
