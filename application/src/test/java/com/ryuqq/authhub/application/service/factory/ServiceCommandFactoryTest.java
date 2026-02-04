package com.ryuqq.authhub.application.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;
import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;
import com.ryuqq.authhub.application.service.fixture.ServiceCommandFixtures;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.aggregate.ServiceUpdateData;
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
 * ServiceCommandFactory 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Factory는 Command DTO → Domain 변환 담당
 *   <li>TimeProvider 의존 → Mock으로 시간 고정
 *   <li>변환 결과의 값 검증에 집중
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceCommandFactory 단위 테스트")
class ServiceCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private ServiceCommandFactory sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공: CreateServiceCommand → Service 도메인 객체 변환")
        void shouldCreateServiceFromCommand() {
            // given
            CreateServiceCommand command = ServiceCommandFixtures.createCommand();
            Instant now = ServiceFixture.fixedTime();

            given(timeProvider.now()).willReturn(now);

            // when
            Service result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isNew()).isTrue();
            assertThat(result.serviceCodeValue()).isEqualTo(command.serviceCode());
            assertThat(result.nameValue()).isEqualTo(command.name());
            assertThat(result.descriptionValue()).isEqualTo(command.description());
            assertThat(result.isActive()).isTrue();
        }

        @Test
        @DisplayName("생성된 Service는 ID가 미할당 (isNew == true)")
        void shouldCreateNewServiceWithoutId() {
            // given
            CreateServiceCommand command = ServiceCommandFixtures.createCommand();

            given(timeProvider.now()).willReturn(Instant.now());

            // when
            Service result = sut.create(command);

            // then
            assertThat(result.isNew()).isTrue();
            assertThat(result.serviceIdValue()).isNull();
        }

        @Test
        @DisplayName("생성된 Service는 ACTIVE 상태")
        void shouldCreateServiceWithActiveStatus() {
            // given
            CreateServiceCommand command =
                    ServiceCommandFixtures.createCommand("SVC_TEST", "테스트 서비스");

            given(timeProvider.now()).willReturn(Instant.now());

            // when
            Service result = sut.create(command);

            // then
            assertThat(result.isActive()).isTrue();
            assertThat(result.statusValue()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공: UpdateServiceCommand → UpdateContext 변환")
        void shouldCreateUpdateContextFromCommand() {
            // given
            UpdateServiceCommand command = ServiceCommandFixtures.updateCommandFull();
            Instant now = ServiceFixture.fixedTime();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<ServiceId, ServiceUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.serviceId());
            assertThat(result.updateData().name()).isEqualTo(command.name());
            assertThat(result.updateData().description()).isEqualTo(command.description());
            assertThat(result.updateData().status()).isEqualTo(command.status());
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("부분 수정 Command에서도 UpdateContext 정상 생성")
        void shouldCreateUpdateContextFromPartialCommand() {
            // given
            UpdateServiceCommand command = ServiceCommandFixtures.updateCommand();
            Instant now = ServiceFixture.fixedTime();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<ServiceId, ServiceUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result.updateData().hasName()).isTrue();
            assertThat(result.updateData().hasDescription()).isFalse();
            assertThat(result.updateData().hasStatus()).isFalse();
        }
    }
}
