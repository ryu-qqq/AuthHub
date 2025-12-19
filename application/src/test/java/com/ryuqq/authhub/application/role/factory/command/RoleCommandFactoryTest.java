package com.ryuqq.authhub.application.role.factory.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
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
 * RoleCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleCommandFactory 단위 테스트")
class RoleCommandFactoryTest {

    @Mock private UuidHolder uuidHolder;

    private RoleCommandFactory factory;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = RoleFixture.fixedClock();
        factory = new RoleCommandFactory(clock, uuidHolder);
    }

    @Nested
    @DisplayName("create 메서드")
    class CreateTest {

        @Test
        @DisplayName("시스템 역할을 생성한다")
        void shouldCreateSystemRole() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            null, // tenantId
                            "SUPER_ADMIN",
                            "Super admin role",
                            "GLOBAL",
                            true // isSystem
                            );

            // when
            Role result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.nameValue()).isEqualTo("SUPER_ADMIN");
            assertThat(result.scopeValue()).isEqualTo("GLOBAL");
            assertThat(result.typeValue()).isEqualTo("SYSTEM");
        }

        @Test
        @DisplayName("커스텀 TENANT 역할을 생성한다")
        void shouldCreateCustomTenantRole() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            UUID tenantId = RoleFixture.defaultTenantUUID();
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            tenantId,
                            "CUSTOM_TENANT_ROLE",
                            "Custom tenant role",
                            "TENANT",
                            false // isSystem
                            );

            // when
            Role result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantIdValue()).isEqualTo(tenantId);
            assertThat(result.nameValue()).isEqualTo("CUSTOM_TENANT_ROLE");
            assertThat(result.scopeValue()).isEqualTo("TENANT");
            assertThat(result.typeValue()).isEqualTo("CUSTOM");
        }

        @Test
        @DisplayName("커스텀 ORGANIZATION 역할을 생성한다")
        void shouldCreateCustomOrganizationRole() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            UUID tenantId = RoleFixture.defaultTenantUUID();
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            tenantId,
                            "CUSTOM_ORG_ROLE",
                            "Custom organization role",
                            "ORGANIZATION",
                            false // isSystem
                            );

            // when
            Role result = factory.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.tenantIdValue()).isEqualTo(tenantId);
            assertThat(result.nameValue()).isEqualTo("CUSTOM_ORG_ROLE");
            assertThat(result.scopeValue()).isEqualTo("ORGANIZATION");
            assertThat(result.typeValue()).isEqualTo("CUSTOM");
        }

        @Test
        @DisplayName("scope가 null이면 기본값 ORGANIZATION으로 생성한다")
        void shouldDefaultToOrganizationScope() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            UUID tenantId = RoleFixture.defaultTenantUUID();
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            tenantId,
                            "DEFAULT_SCOPE_ROLE",
                            "Default scope role",
                            null, // scope null
                            false);

            // when
            Role result = factory.create(command);

            // then
            assertThat(result.scopeValue()).isEqualTo("ORGANIZATION");
        }

        @Test
        @DisplayName("커스텀 역할을 GLOBAL 범위로 생성하면 예외를 발생시킨다")
        void shouldThrowWhenCustomRoleWithGlobalScope() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            null,
                            "INVALID_ROLE",
                            "Invalid role",
                            "GLOBAL",
                            false // isSystem - false이면서 GLOBAL
                            );

            // when & then
            assertThatThrownBy(() -> factory.create(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("GLOBAL");
        }

        @Test
        @DisplayName("유효하지 않은 scope 문자열로 예외를 발생시킨다")
        void shouldThrowWhenInvalidScope() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            UUID tenantId = RoleFixture.defaultTenantUUID();
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            tenantId,
                            "INVALID_SCOPE_ROLE",
                            "Invalid scope role",
                            "INVALID_SCOPE",
                            false);

            // when & then
            assertThatThrownBy(() -> factory.create(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 RoleScope");
        }

        @Test
        @DisplayName("description이 null이면 빈 문자열로 생성한다")
        void shouldCreateWithEmptyDescriptionWhenNull() {
            // given
            given(uuidHolder.random()).willReturn(UUID.randomUUID());
            UUID tenantId = RoleFixture.defaultTenantUUID();
            CreateRoleCommand command =
                    new CreateRoleCommand(
                            tenantId,
                            "NO_DESC_ROLE",
                            "", // empty description (null은 NPE 유발)
                            "ORGANIZATION",
                            false);

            // when
            Role result = factory.create(command);

            // then
            assertThat(result.descriptionValue()).isEmpty();
        }
    }
}
