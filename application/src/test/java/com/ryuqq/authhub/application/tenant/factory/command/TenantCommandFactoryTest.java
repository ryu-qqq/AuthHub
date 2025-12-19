package com.ryuqq.authhub.application.tenant.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import java.time.Clock;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantCommandFactory 단위 테스트")
class TenantCommandFactoryTest {

    @Mock private UuidHolder uuidHolder;

    private TenantCommandFactory factory;

    @BeforeEach
    void setUp() {
        Clock fixedClock = TenantFixture.fixedClock();
        factory = new TenantCommandFactory(fixedClock, uuidHolder);
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("Command를 Domain으로 변환한다")
        void shouldConvertCommandToDomain() {
            // given
            UUID generatedUuid = UUID.randomUUID();
            given(uuidHolder.random()).willReturn(generatedUuid);
            CreateTenantCommand command = new CreateTenantCommand("New Tenant");

            // when
            Tenant tenant = factory.create(command);

            // then
            assertThat(tenant).isNotNull();
            assertThat(tenant.isNew()).isFalse();
            assertThat(tenant.nameValue()).isEqualTo("New Tenant");
            assertThat(tenant.isActive()).isTrue();
        }

        @Test
        @DisplayName("생성된 Tenant는 ID가 할당된다")
        void createdTenantShouldHaveId() {
            // given
            UUID generatedUuid = UUID.randomUUID();
            given(uuidHolder.random()).willReturn(generatedUuid);
            CreateTenantCommand command = new CreateTenantCommand("Test Tenant");

            // when
            Tenant tenant = factory.create(command);

            // then
            assertThat(tenant.tenantIdValue()).isEqualTo(generatedUuid);
            assertThat(tenant.isNew()).isFalse();
        }

        @Test
        @DisplayName("생성된 Tenant는 ACTIVE 상태이다")
        void createdTenantShouldBeActive() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            CreateTenantCommand command = new CreateTenantCommand("Active Tenant");

            // when
            Tenant tenant = factory.create(command);

            // then
            assertThat(tenant.isActive()).isTrue();
            assertThat(tenant.statusValue()).isEqualTo("ACTIVE");
        }
    }
}
