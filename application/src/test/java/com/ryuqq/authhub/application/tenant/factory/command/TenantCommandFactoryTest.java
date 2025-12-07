package com.ryuqq.authhub.application.tenant.factory.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantCommandFactory 단위 테스트")
class TenantCommandFactoryTest {

    private TenantCommandFactory factory;

    @BeforeEach
    void setUp() {
        Clock fixedClock = TenantFixture.fixedClock();
        factory = new TenantCommandFactory(fixedClock);
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("Command를 Domain으로 변환한다")
        void shouldConvertCommandToDomain() {
            // given
            CreateTenantCommand command = new CreateTenantCommand("New Tenant");

            // when
            Tenant tenant = factory.create(command);

            // then
            assertThat(tenant).isNotNull();
            assertThat(tenant.isNew()).isTrue();
            assertThat(tenant.nameValue()).isEqualTo("New Tenant");
            assertThat(tenant.isActive()).isTrue();
        }

        @Test
        @DisplayName("생성된 Tenant는 ID가 없다")
        void createdTenantShouldHaveNoId() {
            // given
            CreateTenantCommand command = new CreateTenantCommand("Test Tenant");

            // when
            Tenant tenant = factory.create(command);

            // then
            assertThat(tenant.tenantIdValue()).isNull();
            assertThat(tenant.isNew()).isTrue();
        }

        @Test
        @DisplayName("생성된 Tenant는 ACTIVE 상태이다")
        void createdTenantShouldBeActive() {
            // given
            CreateTenantCommand command = new CreateTenantCommand("Active Tenant");

            // when
            Tenant tenant = factory.create(command);

            // then
            assertThat(tenant.isActive()).isTrue();
            assertThat(tenant.statusValue()).isEqualTo("ACTIVE");
        }
    }
}
