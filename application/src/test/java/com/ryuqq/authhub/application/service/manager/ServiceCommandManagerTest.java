package com.ryuqq.authhub.application.service.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.service.port.out.command.ServiceCommandPort;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ServiceCommandManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>CommandManager는 CommandPort 위임 담당
 *   <li>Port 호출이 올바르게 위임되는지 검증
 *   <li>반환값이 Port의 결과를 정확히 전달하는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceCommandManager 단위 테스트")
class ServiceCommandManagerTest {

    @Mock private ServiceCommandPort commandPort;

    private ServiceCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new ServiceCommandManager(commandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Service를 CommandPort에 위임하여 영속화하고 ID 반환")
        void shouldDelegatePersistToCommandPort() {
            // given
            Service service = ServiceFixture.createNew();
            Long expectedId = ServiceFixture.defaultIdValue();

            given(commandPort.persist(service)).willReturn(expectedId);

            // when
            Long result = sut.persist(service);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(service);
        }

        @Test
        @DisplayName("기존 Service 영속화 시에도 ID 반환")
        void shouldReturnId_WhenPersistingExistingService() {
            // given
            Service service = ServiceFixture.create();
            Long expectedId = ServiceFixture.defaultIdValue();

            given(commandPort.persist(service)).willReturn(expectedId);

            // when
            Long result = sut.persist(service);

            // then
            assertThat(result).isEqualTo(expectedId);
        }
    }
}
