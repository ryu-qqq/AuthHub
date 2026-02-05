package com.ryuqq.authhub.application.tenantservice.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenantservice.port.out.command.TenantServiceCommandPort;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
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
 * TenantServiceCommandManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>CommandManager는 CommandPort 위임 + @Transactional 관리 담당
 *   <li>Port 호출이 올바르게 위임되는지 검증
 *   <li>반환값이 올바르게 전달되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantServiceCommandManager 단위 테스트")
class TenantServiceCommandManagerTest {

    @Mock private TenantServiceCommandPort commandPort;

    private TenantServiceCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceCommandManager(commandPort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: TenantService 영속화 후 ID 반환")
        void shouldPersistAndReturnId() {
            // given
            TenantService tenantService = TenantServiceFixture.createNew();
            Long expectedId = TenantServiceFixture.defaultIdValue();

            given(commandPort.persist(tenantService)).willReturn(expectedId);

            // when
            Long result = sut.persist(tenantService);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(tenantService);
        }

        @Test
        @DisplayName("CommandPort에 TenantService를 위임하여 영속화")
        void shouldDelegateToCommandPort() {
            // given
            TenantService tenantService = TenantServiceFixture.create();
            Long persistedId = 1L;

            given(commandPort.persist(tenantService)).willReturn(persistedId);

            // when
            sut.persist(tenantService);

            // then
            then(commandPort).should().persist(tenantService);
        }

        @Test
        @DisplayName("새로운 TenantService 영속화 시 ID 생성 후 반환")
        void shouldGenerateAndReturnId_WhenPersistingNewTenantService() {
            // given
            TenantService newTenantService = TenantServiceFixture.createNew();
            Long generatedId = 999L;

            given(commandPort.persist(newTenantService)).willReturn(generatedId);

            // when
            Long result = sut.persist(newTenantService);

            // then
            assertThat(result).isEqualTo(generatedId);
        }
    }
}
