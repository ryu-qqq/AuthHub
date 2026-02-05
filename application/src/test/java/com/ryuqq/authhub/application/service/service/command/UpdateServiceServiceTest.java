package com.ryuqq.authhub.application.service.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;
import com.ryuqq.authhub.application.service.factory.ServiceCommandFactory;
import com.ryuqq.authhub.application.service.fixture.ServiceCommandFixtures;
import com.ryuqq.authhub.application.service.manager.ServiceCommandManager;
import com.ryuqq.authhub.application.service.validator.ServiceValidator;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.aggregate.ServiceUpdateData;
import com.ryuqq.authhub.domain.service.exception.ServiceNotFoundException;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import com.ryuqq.authhub.domain.service.id.ServiceId;
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
 * UpdateServiceService 단위 테스트
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
@DisplayName("UpdateServiceService 단위 테스트")
class UpdateServiceServiceTest {

    @Mock private ServiceValidator validator;

    @Mock private ServiceCommandFactory commandFactory;

    @Mock private ServiceCommandManager commandManager;

    private UpdateServiceService sut;

    @BeforeEach
    void setUp() {
        sut = new UpdateServiceService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Domain.update → Manager 순서로 호출")
        void shouldOrchestrate_FactoryThenValidatorThenDomainThenManager() {
            // given
            UpdateServiceCommand command = ServiceCommandFixtures.updateCommandFull();
            Instant changedAt = ServiceFixture.fixedTime();
            ServiceId serviceId = ServiceFixture.defaultId();
            ServiceUpdateData updateData =
                    ServiceUpdateData.of(command.name(), command.description(), command.status());
            UpdateContext<ServiceId, ServiceUpdateData> context =
                    new UpdateContext<>(serviceId, updateData, changedAt);

            Service service = ServiceFixture.create();

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(serviceId)).willReturn(service);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(serviceId);
            then(commandManager).should().persist(service);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 서비스일 경우 ServiceNotFoundException 발생")
        void shouldThrowException_WhenServiceNotFound() {
            // given
            UpdateServiceCommand command = ServiceCommandFixtures.updateCommand();
            Instant changedAt = ServiceFixture.fixedTime();
            ServiceId serviceId = ServiceFixture.defaultId();
            ServiceUpdateData updateData =
                    ServiceUpdateData.of(command.name(), command.description(), command.status());
            UpdateContext<ServiceId, ServiceUpdateData> context =
                    new UpdateContext<>(serviceId, updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(serviceId))
                    .willThrow(new ServiceNotFoundException(serviceId));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ServiceNotFoundException.class);

            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("Validator를 통해 기존 서비스 조회 후 업데이트 수행")
        void shouldFindExistingServiceThenUpdate() {
            // given
            UpdateServiceCommand command = ServiceCommandFixtures.updateCommandFull();
            Instant changedAt = ServiceFixture.fixedTime();
            ServiceId serviceId = ServiceFixture.defaultId();
            ServiceUpdateData updateData =
                    ServiceUpdateData.of(command.name(), command.description(), command.status());
            UpdateContext<ServiceId, ServiceUpdateData> context =
                    new UpdateContext<>(serviceId, updateData, changedAt);

            Service service = ServiceFixture.create();

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(serviceId)).willReturn(service);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(service);
        }
    }
}
