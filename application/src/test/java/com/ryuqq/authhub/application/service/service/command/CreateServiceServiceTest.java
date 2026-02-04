package com.ryuqq.authhub.application.service.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;
import com.ryuqq.authhub.application.service.factory.ServiceCommandFactory;
import com.ryuqq.authhub.application.service.fixture.ServiceCommandFixtures;
import com.ryuqq.authhub.application.service.manager.ServiceCommandManager;
import com.ryuqq.authhub.application.service.validator.ServiceValidator;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.exception.DuplicateServiceIdException;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreateServiceService 단위 테스트
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
@DisplayName("CreateServiceService 단위 테스트")
class CreateServiceServiceTest {

    @Mock private ServiceValidator validator;

    @Mock private ServiceCommandFactory commandFactory;

    @Mock private ServiceCommandManager commandManager;

    private CreateServiceService sut;

    @BeforeEach
    void setUp() {
        sut = new CreateServiceService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Validator → Factory → Manager 순서로 호출하고 ID 반환")
        void shouldOrchestrate_ValidatorThenFactoryThenManager_AndReturnId() {
            // given
            CreateServiceCommand command = ServiceCommandFixtures.createCommand();
            Service service = ServiceFixture.createNew();
            Long expectedId = ServiceFixture.defaultIdValue();

            given(commandFactory.create(command)).willReturn(service);
            given(commandManager.persist(service)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);

            then(validator)
                    .should()
                    .validateCodeNotDuplicated(ServiceCode.of(command.serviceCode()));
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(service);
        }

        @Test
        @DisplayName("실패: 중복 서비스 코드일 경우 DuplicateServiceIdException 발생")
        void shouldThrowException_WhenCodeIsDuplicated() {
            // given
            CreateServiceCommand command = ServiceCommandFixtures.createCommand();

            willThrow(new DuplicateServiceIdException(command.serviceCode()))
                    .given(validator)
                    .validateCodeNotDuplicated(any(ServiceCode.class));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(DuplicateServiceIdException.class);

            then(commandFactory).should(never()).create(any());
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("검증 통과 후 Manager를 통해 영속화 수행")
        void shouldPersistService_ThroughManager() {
            // given
            CreateServiceCommand command = ServiceCommandFixtures.createCommand();
            Service service = ServiceFixture.createNew();

            given(commandFactory.create(command)).willReturn(service);
            given(commandManager.persist(service)).willReturn(1L);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persist(service);
        }
    }
}
