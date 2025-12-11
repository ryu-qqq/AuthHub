package com.ryuqq.authhub.application.organization.factory.command;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import java.time.Clock;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationCommandFactory 단위 테스트")
class OrganizationCommandFactoryTest {

    private OrganizationCommandFactory factory;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = OrganizationFixture.fixedClock();
        factory = new OrganizationCommandFactory(clock);
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("CreateOrganizationCommand로 Organization을 생성한다")
        void shouldCreateOrganizationFromCommand() {
            // given
            UUID tenantId = OrganizationFixture.defaultTenantUUID();
            CreateOrganizationCommand command = new CreateOrganizationCommand(tenantId, "New Org");

            // when
            Organization result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantIdValue()).isEqualTo(tenantId);
            assertThat(result.nameValue()).isEqualTo("New Org");
            assertThat(result.statusValue()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("생성된 조직의 상태는 ACTIVE이다")
        void shouldCreateOrganizationWithActiveStatus() {
            // given
            CreateOrganizationCommand command =
                    new CreateOrganizationCommand(
                            OrganizationFixture.defaultTenantUUID(), "Active Org");

            // when
            Organization result = factory.create(command);

            // then
            assertThat(result.statusValue()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("다양한 이름으로 조직을 생성한다")
        void shouldCreateOrganizationWithVariousNames() {
            // given
            UUID tenantId = OrganizationFixture.defaultTenantUUID();
            CreateOrganizationCommand command1 = new CreateOrganizationCommand(tenantId, "개발팀");
            CreateOrganizationCommand command2 = new CreateOrganizationCommand(tenantId, "DevOps");
            CreateOrganizationCommand command3 =
                    new CreateOrganizationCommand(tenantId, "Marketing Team");

            // when
            Organization result1 = factory.create(command1);
            Organization result2 = factory.create(command2);
            Organization result3 = factory.create(command3);

            // then
            assertThat(result1.nameValue()).isEqualTo("개발팀");
            assertThat(result2.nameValue()).isEqualTo("DevOps");
            assertThat(result3.nameValue()).isEqualTo("Marketing Team");
        }

        @Test
        @DisplayName("다른 테넌트로 조직을 생성한다")
        void shouldCreateOrganizationWithDifferentTenant() {
            // given
            UUID tenantId1 = UUID.randomUUID();
            UUID tenantId2 = UUID.randomUUID();
            CreateOrganizationCommand command1 = new CreateOrganizationCommand(tenantId1, "Org A");
            CreateOrganizationCommand command2 = new CreateOrganizationCommand(tenantId2, "Org B");

            // when
            Organization result1 = factory.create(command1);
            Organization result2 = factory.create(command2);

            // then
            assertThat(result1.tenantIdValue()).isEqualTo(tenantId1);
            assertThat(result2.tenantIdValue()).isEqualTo(tenantId2);
        }
    }
}
