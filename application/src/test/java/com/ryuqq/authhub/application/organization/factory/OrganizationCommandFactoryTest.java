package com.ryuqq.authhub.application.organization.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.port.out.IdGeneratorPort;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationNameCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.fixture.OrganizationCommandFixtures;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
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
 * OrganizationCommandFactory 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Factory는 Command → Domain 변환 담당
 *   <li>시간/ID 생성 로직이 올바르게 적용되는지 검증
 *   <li>Domain 객체의 상태가 올바르게 초기화되는지 검증 ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationCommandFactory 단위 테스트")
class OrganizationCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    @Mock private IdGeneratorPort idGeneratorPort;

    private OrganizationCommandFactory sut;

    private static final Instant FIXED_TIME = OrganizationFixture.fixedTime();
    private static final String GENERATED_ID = "generated-uuid-v7";

    @BeforeEach
    void setUp() {
        sut = new OrganizationCommandFactory(timeProvider, idGeneratorPort);
    }

    @Nested
    @DisplayName("create 메서드 (Domain 객체 생성)")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 Organization 도메인 객체 생성")
        void shouldCreateOrganization_FromCommand() {
            // given
            CreateOrganizationCommand command = OrganizationCommandFixtures.createCommand();

            given(idGeneratorPort.generate()).willReturn(GENERATED_ID);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Organization result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.organizationIdValue()).isEqualTo(GENERATED_ID);
            assertThat(result.tenantIdValue()).isEqualTo(command.tenantId());
            assertThat(result.nameValue()).isEqualTo(command.name());
            assertThat(result.organizationIdValue()).isNotBlank();
            assertThat(result.createdAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("IdGeneratorPort를 통해 새 ID 생성")
        void shouldGenerateNewId_ThroughIdGeneratorPort() {
            // given
            CreateOrganizationCommand command = OrganizationCommandFixtures.createCommand();
            String expectedId = "unique-generated-id";

            given(idGeneratorPort.generate()).willReturn(expectedId);
            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Organization result = sut.create(command);

            // then
            assertThat(result.organizationIdValue()).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("TimeProvider를 통해 생성 시간 설정")
        void shouldSetCreatedAt_ThroughTimeProvider() {
            // given
            CreateOrganizationCommand command = OrganizationCommandFixtures.createCommand();
            Instant expectedTime = Instant.parse("2025-06-15T10:30:00Z");

            given(idGeneratorPort.generate()).willReturn(GENERATED_ID);
            given(timeProvider.now()).willReturn(expectedTime);

            // when
            Organization result = sut.create(command);

            // then
            assertThat(result.createdAt()).isEqualTo(expectedTime);
        }
    }

    @Nested
    @DisplayName("createNameUpdateContext 메서드")
    class CreateNameUpdateContext {

        @Test
        @DisplayName("성공: Command로부터 UpdateContext 생성")
        void shouldCreateUpdateContext_FromCommand() {
            // given
            UpdateOrganizationNameCommand command = OrganizationCommandFixtures.updateNameCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<OrganizationId, OrganizationName> result =
                    sut.createNameUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.organizationId());
            assertThat(result.updateData().value()).isEqualTo(command.name());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("TimeProvider를 통해 변경 시간 설정")
        void shouldSetChangedAt_ThroughTimeProvider() {
            // given
            UpdateOrganizationNameCommand command = OrganizationCommandFixtures.updateNameCommand();
            Instant expectedTime = Instant.parse("2025-07-20T15:45:00Z");

            given(timeProvider.now()).willReturn(expectedTime);

            // when
            UpdateContext<OrganizationId, OrganizationName> result =
                    sut.createNameUpdateContext(command);

            // then
            assertThat(result.changedAt()).isEqualTo(expectedTime);
        }
    }

    @Nested
    @DisplayName("createStatusChangeContext 메서드")
    class CreateStatusChangeContext {

        @Test
        @DisplayName("성공: Command로부터 StatusChangeContext 생성")
        void shouldCreateStatusChangeContext_FromCommand() {
            // given
            UpdateOrganizationStatusCommand command =
                    OrganizationCommandFixtures.deactivateCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            StatusChangeContext<OrganizationId> result = sut.createStatusChangeContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.organizationId());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("TimeProvider를 통해 변경 시간 설정")
        void shouldSetChangedAt_ThroughTimeProvider() {
            // given
            UpdateOrganizationStatusCommand command = OrganizationCommandFixtures.activateCommand();
            Instant expectedTime = Instant.parse("2025-08-25T09:00:00Z");

            given(timeProvider.now()).willReturn(expectedTime);

            // when
            StatusChangeContext<OrganizationId> result = sut.createStatusChangeContext(command);

            // then
            assertThat(result.changedAt()).isEqualTo(expectedTime);
        }
    }
}
