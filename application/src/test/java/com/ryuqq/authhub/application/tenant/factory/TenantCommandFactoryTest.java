package com.ryuqq.authhub.application.tenant.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.application.tenant.fixture.TenantCommandFixtures;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
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
 * TenantCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantCommandFactory 단위 테스트")
class TenantCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    @Mock private IdGeneratorPort idGeneratorPort;

    private TenantCommandFactory sut;

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String GENERATED_ID = "generated-uuid-v7";

    @BeforeEach
    void setUp() {
        sut = new TenantCommandFactory(timeProvider, idGeneratorPort);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 Tenant 도메인 객체 생성")
        void shouldCreateTenant_FromCommand() {
            // given
            CreateTenantCommand command = TenantCommandFixtures.createCommand();

            given(idGeneratorPort.generate()).willReturn(GENERATED_ID);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Tenant result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantIdValue()).isEqualTo(GENERATED_ID);
            assertThat(result.nameValue()).isEqualTo(command.name());
            assertThat(result.createdAt()).isEqualTo(FIXED_TIME);
        }
    }

    @Nested
    @DisplayName("createNameUpdateContext 메서드")
    class CreateNameUpdateContext {

        @Test
        @DisplayName("성공: Command로부터 UpdateContext 생성")
        void shouldCreateUpdateContext_FromCommand() {
            // given
            UpdateTenantNameCommand command = TenantCommandFixtures.updateNameCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<TenantId, TenantName> result = sut.createNameUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.tenantId());
            assertThat(result.updateData().value()).isEqualTo(command.name());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }

    @Nested
    @DisplayName("createStatusChangeContext 메서드")
    class CreateStatusChangeContext {

        @Test
        @DisplayName("성공: Command로부터 StatusChangeContext 생성")
        void shouldCreateStatusChangeContext_FromCommand() {
            // given
            UpdateTenantStatusCommand command = TenantCommandFixtures.deactivateCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            StatusChangeContext<TenantId> result = sut.createStatusChangeContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.tenantId());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
