package com.ryuqq.authhub.application.tenant.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenant.port.out.command.TenantCommandPort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantCommandManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantCommandManager 단위 테스트")
class TenantCommandManagerTest {

    @Mock private TenantCommandPort persistencePort;

    private TenantCommandManager sut;

    @BeforeEach
    void setUp() {
        sut = new TenantCommandManager(persistencePort);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Tenant 영속화 후 ID 반환")
        void shouldPersistAndReturnId() {
            // given
            Tenant tenant = TenantFixture.createNew();
            String expectedId = TenantFixture.defaultIdString();

            given(persistencePort.persist(tenant)).willReturn(expectedId);

            // when
            String result = sut.persist(tenant);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(persistencePort).should().persist(tenant);
        }
    }
}
