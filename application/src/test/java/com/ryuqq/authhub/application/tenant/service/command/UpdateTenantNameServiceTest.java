package com.ryuqq.authhub.application.tenant.service.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.factory.TenantCommandFactory;
import com.ryuqq.authhub.application.tenant.fixture.TenantCommandFixtures;
import com.ryuqq.authhub.application.tenant.manager.TenantCommandManager;
import com.ryuqq.authhub.application.tenant.validator.TenantValidator;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
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
 * UpdateTenantNameService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTenantNameService 단위 테스트")
class UpdateTenantNameServiceTest {

    @Mock private TenantValidator validator;

    @Mock private TenantCommandFactory commandFactory;

    @Mock private TenantCommandManager commandManager;

    private UpdateTenantNameService sut;

    @BeforeEach
    void setUp() {
        sut = new UpdateTenantNameService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Validator → Factory → Manager 순서로 호출")
        void shouldOrchestrate_ValidatorThenFactoryThenManager() {
            // given
            UpdateTenantNameCommand command = TenantCommandFixtures.updateNameCommand();
            Tenant tenant = TenantFixture.create();
            UpdateContext<TenantId, TenantName> context =
                    new UpdateContext<>(
                            TenantId.of(command.tenantId()),
                            TenantName.of(command.name()),
                            Instant.now());

            given(commandFactory.createNameUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(tenant);

            // when
            assertThatCode(() -> sut.execute(command)).doesNotThrowAnyException();

            // then
            then(validator)
                    .should()
                    .validateNameNotDuplicatedExcluding(context.updateData(), context.id());
            then(validator).should().findExistingOrThrow(context.id());
            then(commandFactory).should().createNameUpdateContext(command);
            then(commandManager).should().persist(tenant);
        }

        @Test
        @DisplayName("실패: 중복 이름이면 DuplicateTenantNameException 발생")
        void shouldThrowException_WhenNameIsDuplicated() {
            // given
            UpdateTenantNameCommand command = TenantCommandFixtures.updateNameCommand();
            UpdateContext<TenantId, TenantName> context =
                    new UpdateContext<>(
                            TenantId.of(command.tenantId()),
                            TenantName.of(command.name()),
                            Instant.now());

            given(commandFactory.createNameUpdateContext(command)).willReturn(context);
            doThrow(new DuplicateTenantNameException(context.updateData()))
                    .when(validator)
                    .validateNameNotDuplicatedExcluding(any(), any());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateTenantNameException.class);
            then(validator).should().validateNameNotDuplicatedExcluding(any(), any());
            then(validator).should(never()).findExistingOrThrow(any());
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("실패: 테넌트가 없으면 TenantNotFoundException 발생")
        void shouldThrowException_WhenTenantNotFound() {
            // given
            UpdateTenantNameCommand command = TenantCommandFixtures.updateNameCommand();
            UpdateContext<TenantId, TenantName> context =
                    new UpdateContext<>(
                            TenantId.of(command.tenantId()),
                            TenantName.of(command.name()),
                            Instant.now());

            given(commandFactory.createNameUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id()))
                    .willThrow(new TenantNotFoundException(context.id()));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TenantNotFoundException.class);
            then(commandManager).should(never()).persist(any());
        }
    }
}
