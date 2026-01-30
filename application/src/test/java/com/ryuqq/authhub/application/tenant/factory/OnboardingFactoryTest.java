package com.ryuqq.authhub.application.tenant.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.tenant.dto.bundle.OnboardingBundle;
import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.application.tenant.fixture.TenantCommandFixtures;
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
 * OnboardingFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OnboardingFactory 단위 테스트")
class OnboardingFactoryTest {

    @Mock private TimeProvider timeProvider;
    @Mock private IdGeneratorPort idGeneratorPort;

    private OnboardingFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String TENANT_ID = "01933abc-1234-7000-8000-000000000001";
    private static final String ORG_ID = "01933abc-1234-7000-8000-000000000002";

    @BeforeEach
    void setUp() {
        sut = new OnboardingFactory(timeProvider, idGeneratorPort);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 OnboardingBundle 생성 (Tenant + Organization)")
        void shouldCreateOnboardingBundle_FromCommand() {
            // given
            OnboardingCommand command = TenantCommandFixtures.createOnboardingCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);
            given(idGeneratorPort.generate()).willReturn(TENANT_ID).willReturn(ORG_ID);

            // when
            OnboardingBundle result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenant()).isNotNull();
            assertThat(result.organization()).isNotNull();

            assertThat(result.tenant().tenantIdValue()).isEqualTo(TENANT_ID);
            assertThat(result.tenant().nameValue()).isEqualTo(command.tenantName());
            assertThat(result.tenant().createdAt()).isEqualTo(FIXED_TIME);

            assertThat(result.organization().organizationIdValue()).isEqualTo(ORG_ID);
            assertThat(result.organization().tenantIdValue()).isEqualTo(TENANT_ID);
            assertThat(result.organization().nameValue()).isEqualTo(command.organizationName());
            assertThat(result.organization().createdAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("성공: 지정된 이름으로 Tenant와 Organization 생성")
        void shouldCreateBundle_WithSpecifiedNames() {
            // given
            OnboardingCommand command =
                    TenantCommandFixtures.createOnboardingCommand("My Tenant", "My Org");

            given(timeProvider.now()).willReturn(FIXED_TIME);
            given(idGeneratorPort.generate()).willReturn(TENANT_ID, ORG_ID);

            // when
            OnboardingBundle result = sut.create(command);

            // then
            assertThat(result.tenant().nameValue()).isEqualTo("My Tenant");
            assertThat(result.organization().nameValue()).isEqualTo("My Org");
        }
    }
}
