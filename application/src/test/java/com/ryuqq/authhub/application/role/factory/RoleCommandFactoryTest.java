package com.ryuqq.authhub.application.role.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.fixture.RoleCommandFixtures;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.aggregate.RoleUpdateData;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.id.RoleId;
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
 * RoleCommandFactory 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Factory는 Command → Domain 변환 담당
 *   <li>시간 생성 로직이 올바르게 적용되는지 검증
 *   <li>Domain 객체의 상태가 올바르게 초기화되는지 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleCommandFactory 단위 테스트")
class RoleCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private RoleCommandFactory sut;

    private static final Instant FIXED_TIME = RoleFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new RoleCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드 (Domain 객체 생성)")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 Role 도메인 객체 생성")
        void shouldCreateRole_FromCommand() {
            // given
            CreateRoleCommand command = RoleCommandFixtures.createCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Role result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.nameValue()).isEqualTo(command.name());
            assertThat(result.displayNameValue()).isEqualTo(command.displayName());
            assertThat(result.descriptionValue()).isEqualTo(command.description());
            assertThat(result.createdAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("시스템 역할 Command로부터 SYSTEM 타입 Role 생성")
        void shouldCreateSystemRole_WhenIsSystemTrue() {
            // given
            CreateRoleCommand command = RoleCommandFixtures.createSystemCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            Role result = sut.create(command);

            // then
            assertThat(result.typeValue()).isEqualTo("SYSTEM");
        }

        @Test
        @DisplayName("TimeProvider를 통해 생성 시간 설정")
        void shouldSetCreatedAt_ThroughTimeProvider() {
            // given
            CreateRoleCommand command = RoleCommandFixtures.createCommand();
            Instant expectedTime = Instant.parse("2025-06-15T10:30:00Z");

            given(timeProvider.now()).willReturn(expectedTime);

            // when
            Role result = sut.create(command);

            // then
            assertThat(result.createdAt()).isEqualTo(expectedTime);
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공: Command로부터 UpdateContext 생성")
        void shouldCreateUpdateContext_FromCommand() {
            // given
            UpdateRoleCommand command = RoleCommandFixtures.updateCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<RoleId, RoleUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.roleId());
            assertThat(result.updateData().displayName()).isEqualTo(command.displayName());
            assertThat(result.updateData().description()).isEqualTo(command.description());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }

    @Nested
    @DisplayName("createDeleteContext 메서드")
    class CreateDeleteContext {

        @Test
        @DisplayName("성공: Command로부터 StatusChangeContext 생성")
        void shouldCreateDeleteContext_FromCommand() {
            // given
            DeleteRoleCommand command = RoleCommandFixtures.deleteCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            StatusChangeContext<RoleId> result = sut.createDeleteContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.roleId());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
