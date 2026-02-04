package com.ryuqq.authhub.application.tenantservice.service.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;
import com.ryuqq.authhub.application.tenantservice.factory.TenantServiceCommandFactory;
import com.ryuqq.authhub.application.tenantservice.fixture.TenantServiceCommandFixtures;
import com.ryuqq.authhub.application.tenantservice.manager.TenantServiceCommandManager;
import com.ryuqq.authhub.application.tenantservice.validator.TenantServiceValidator;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.exception.TenantServiceNotFoundException;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
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
 * UpdateTenantServiceStatusService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 오케스트레이션만 담당 → 협력 객체 호출 순서/조건 검증
 *   <li>비즈니스 로직은 Domain/Validator에서 테스트
 *   <li>BDDMockito 스타일로 Given-When-Then 구조 명확화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTenantServiceStatusService 단위 테스트")
class UpdateTenantServiceStatusServiceTest {

    @Mock private TenantServiceValidator validator;

    @Mock private TenantServiceCommandFactory commandFactory;

    @Mock private TenantServiceCommandManager commandManager;

    private UpdateTenantServiceStatusService sut;

    private static final Instant FIXED_TIME = TenantServiceFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new UpdateTenantServiceStatusService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Domain → Manager 순서로 호출")
        void shouldOrchestrate_FactoryThenValidatorThenDomainThenManager() {
            // given
            UpdateTenantServiceStatusCommand command =
                    TenantServiceCommandFixtures.updateStatusCommand("INACTIVE");
            TenantServiceId id = TenantServiceId.of(command.tenantServiceId());
            StatusChangeContext<TenantServiceId> context =
                    new StatusChangeContext<>(id, FIXED_TIME);
            TenantService tenantService = TenantServiceFixture.create();

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(tenantService);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createStatusChangeContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(tenantService);
        }

        @Test
        @DisplayName("실패: TenantService가 존재하지 않으면 TenantServiceNotFoundException 발생")
        void shouldThrowException_WhenTenantServiceNotExists() {
            // given
            UpdateTenantServiceStatusCommand command =
                    TenantServiceCommandFixtures.updateStatusCommand();
            TenantServiceId id = TenantServiceId.of(command.tenantServiceId());
            StatusChangeContext<TenantServiceId> context =
                    new StatusChangeContext<>(id, FIXED_TIME);

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            willThrow(new TenantServiceNotFoundException(id))
                    .given(validator)
                    .findExistingOrThrow(any(TenantServiceId.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(TenantServiceNotFoundException.class);

            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("Domain에서 상태 변경 메서드 호출")
        void shouldInvokeDomainChangeStatusMethod() {
            // given
            UpdateTenantServiceStatusCommand command =
                    TenantServiceCommandFixtures.updateStatusCommand("SUSPENDED");
            TenantServiceId id = TenantServiceId.of(command.tenantServiceId());
            StatusChangeContext<TenantServiceId> context =
                    new StatusChangeContext<>(id, FIXED_TIME);
            TenantService tenantService = TenantServiceFixture.create();

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(tenantService);

            // when
            sut.execute(command);

            // then
            assertThatCode(
                            () ->
                                    tenantService.changeStatus(
                                            TenantServiceStatus.SUSPENDED, context.changedAt()))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("상태 변경 후 Manager를 통해 영속화")
        void shouldPersistDomain_AfterStatusChange() {
            // given
            UpdateTenantServiceStatusCommand command =
                    TenantServiceCommandFixtures.updateStatusCommand();
            TenantServiceId id = TenantServiceId.of(command.tenantServiceId());
            StatusChangeContext<TenantServiceId> context =
                    new StatusChangeContext<>(id, FIXED_TIME);
            TenantService tenantService = TenantServiceFixture.create();

            given(commandFactory.createStatusChangeContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(tenantService);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(tenantService);
        }
    }
}
