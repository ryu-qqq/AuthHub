package com.ryuqq.authhub.application.tenantservice.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;
import com.ryuqq.authhub.application.tenantservice.factory.TenantServiceCommandFactory;
import com.ryuqq.authhub.application.tenantservice.fixture.TenantServiceCommandFixtures;
import com.ryuqq.authhub.application.tenantservice.manager.TenantServiceCommandManager;
import com.ryuqq.authhub.application.tenantservice.validator.TenantServiceValidator;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.exception.DuplicateTenantServiceException;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SubscribeTenantServiceService 단위 테스트
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
@DisplayName("SubscribeTenantServiceService 단위 테스트")
class SubscribeTenantServiceServiceTest {

    @Mock private TenantServiceValidator validator;

    @Mock private TenantServiceCommandFactory commandFactory;

    @Mock private TenantServiceCommandManager commandManager;

    private SubscribeTenantServiceService sut;

    @BeforeEach
    void setUp() {
        sut = new SubscribeTenantServiceService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Validator → Factory → Manager 순서로 호출하고 ID 반환")
        void shouldOrchestrate_ValidatorThenFactoryThenManager_AndReturnId() {
            // given
            SubscribeTenantServiceCommand command = TenantServiceCommandFixtures.subscribeCommand();
            TenantService tenantService = TenantServiceFixture.createNew();
            Long expectedId = TenantServiceFixture.defaultIdValue();

            given(commandFactory.create(command)).willReturn(tenantService);
            given(commandManager.persist(tenantService)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(validator)
                    .should()
                    .validateNotDuplicated(
                            TenantId.of(command.tenantId()), ServiceId.of(command.serviceId()));
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(tenantService);
        }

        @Test
        @DisplayName("실패: 중복 구독일 경우 DuplicateTenantServiceException 발생")
        void shouldThrowException_WhenSubscriptionIsDuplicated() {
            // given
            SubscribeTenantServiceCommand command = TenantServiceCommandFixtures.subscribeCommand();

            willThrow(new DuplicateTenantServiceException(command.tenantId(), command.serviceId()))
                    .given(validator)
                    .validateNotDuplicated(any(TenantId.class), any(ServiceId.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateTenantServiceException.class);

            then(commandFactory).should(never()).create(any());
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("검증 통과 후 Factory를 통해 Domain 객체 생성")
        void shouldCreateDomain_ThroughFactory_AfterValidation() {
            // given
            SubscribeTenantServiceCommand command = TenantServiceCommandFixtures.subscribeCommand();
            TenantService tenantService = TenantServiceFixture.createNew();

            given(commandFactory.create(command)).willReturn(tenantService);
            given(commandManager.persist(tenantService)).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().create(command);
        }

        @Test
        @DisplayName("Domain 객체 생성 후 Manager를 통해 영속화 수행")
        void shouldPersistDomain_ThroughManager() {
            // given
            SubscribeTenantServiceCommand command = TenantServiceCommandFixtures.subscribeCommand();
            TenantService tenantService = TenantServiceFixture.createNew();

            given(commandFactory.create(command)).willReturn(tenantService);
            given(commandManager.persist(tenantService)).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(tenantService);
        }
    }
}
