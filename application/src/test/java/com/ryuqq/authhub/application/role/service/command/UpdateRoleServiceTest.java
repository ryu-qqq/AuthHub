package com.ryuqq.authhub.application.role.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.factory.RoleCommandFactory;
import com.ryuqq.authhub.application.role.fixture.RoleCommandFixtures;
import com.ryuqq.authhub.application.role.manager.RoleCommandManager;
import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.aggregate.RoleUpdateData;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
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
 * UpdateRoleService 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Service는 오케스트레이션만 담당 → 협력 객체 호출 순서/조건 검증
 *   <li>비즈니스 로직은 Domain/Validator에서 테스트
 *   <li>BDDMockito 스타일로 Given-When-Then 구조 명확화
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateRoleService 단위 테스트")
class UpdateRoleServiceTest {

    @Mock private RoleValidator validator;
    @Mock private RoleCommandFactory commandFactory;
    @Mock private RoleCommandManager commandManager;

    private UpdateRoleService sut;

    @BeforeEach
    void setUp() {
        sut = new UpdateRoleService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Domain.update → Manager 순서로 호출")
        void shouldOrchestrate_FactoryThenValidatorThenDomainThenManager() {
            // given
            UpdateRoleCommand command = RoleCommandFixtures.updateCommand();
            Instant changedAt = RoleFixture.fixedTime();
            RoleId roleId = RoleFixture.defaultId();
            RoleUpdateData updateData =
                    RoleUpdateData.of(command.displayName(), command.description());
            UpdateContext<RoleId, RoleUpdateData> context =
                    new UpdateContext<>(roleId, updateData, changedAt);
            Role role = RoleFixture.create();

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(roleId)).willReturn(role);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(roleId);
            then(commandManager).should().persist(role);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 역할일 경우 RoleNotFoundException 발생")
        void shouldThrowException_WhenRoleNotFound() {
            // given
            UpdateRoleCommand command = RoleCommandFixtures.updateCommand();
            RoleId roleId = RoleFixture.defaultId();
            RoleUpdateData updateData =
                    RoleUpdateData.of(command.displayName(), command.description());
            UpdateContext<RoleId, RoleUpdateData> context =
                    new UpdateContext<>(roleId, updateData, RoleFixture.fixedTime());

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(roleId))
                    .willThrow(new RoleNotFoundException(roleId));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(RoleNotFoundException.class);

            then(commandManager).should(never()).persist(any());
        }
    }
}
