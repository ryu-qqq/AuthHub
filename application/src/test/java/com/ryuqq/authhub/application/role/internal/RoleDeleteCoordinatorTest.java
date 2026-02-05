package com.ryuqq.authhub.application.role.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.factory.RoleCommandFactory;
import com.ryuqq.authhub.application.role.fixture.RoleCommandFixtures;
import com.ryuqq.authhub.application.role.manager.RoleCommandManager;
import com.ryuqq.authhub.application.role.validator.RoleValidator;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.RoleInUseException;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.exception.SystemRoleNotDeletableException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.id.RoleId;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RoleDeleteCoordinator 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Coordinator 오케스트레이션 순서 검증 (Factory → Validator → UsageChecker → Domain → Manager)
 *   <li>실패 시 후속 단계 미호출 검증
 *   <li>RoleUsageChecker 빈 Optional 시 삭제 성공
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleDeleteCoordinator 단위 테스트")
class RoleDeleteCoordinatorTest {

    @Mock private RoleValidator roleValidator;
    @Mock private RoleUsageChecker roleUsageChecker;
    @Mock private RoleCommandFactory commandFactory;
    @Mock private RoleCommandManager commandManager;

    private RoleDeleteCoordinator sut;

    @BeforeEach
    void setUp() {
        sut =
                new RoleDeleteCoordinator(
                        roleValidator,
                        Optional.empty(), // UsageChecker 없음 → validateNotInUse 미호출
                        commandFactory,
                        commandManager);
    }

    @Nested
    @DisplayName("execute 메서드 (UsageChecker 없음)")
    class ExecuteWithoutUsageChecker {

        @Test
        @DisplayName("성공: Factory → Validator → Domain.delete → Manager 순서로 호출")
        void shouldOrchestrate_FactoryThenValidatorThenDomainThenManager() {
            // given
            DeleteRoleCommand command = RoleCommandFixtures.deleteCommand();
            RoleId roleId = RoleFixture.defaultId();
            Instant changedAt = Instant.now();
            StatusChangeContext<RoleId> context = new StatusChangeContext<>(roleId, changedAt);
            Role role = RoleFixture.create();

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(roleValidator.findExistingOrThrow(roleId)).willReturn(role);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createDeleteContext(command);
            then(roleValidator).should().findExistingOrThrow(roleId);
            then(commandManager).should().persist(role);
        }

        @Test
        @DisplayName("실패: 역할이 존재하지 않으면 RoleNotFoundException 발생, Manager 미호출")
        void shouldThrowException_WhenRoleNotFound() {
            // given
            DeleteRoleCommand command = RoleCommandFixtures.deleteCommand();
            RoleId roleId = RoleFixture.defaultId();
            StatusChangeContext<RoleId> context = new StatusChangeContext<>(roleId, Instant.now());

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(roleValidator.findExistingOrThrow(roleId))
                    .willThrow(new RoleNotFoundException(roleId));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(RoleNotFoundException.class);

            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("실패: SYSTEM 역할이면 SystemRoleNotDeletableException 발생, Manager 미호출")
        void shouldThrowException_WhenSystemRole() {
            // given
            DeleteRoleCommand command = RoleCommandFixtures.deleteCommand();
            RoleId roleId = RoleFixture.defaultId();
            Instant changedAt = Instant.now();
            StatusChangeContext<RoleId> context = new StatusChangeContext<>(roleId, changedAt);
            Role systemRole = RoleFixture.createSystemRole();

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(roleValidator.findExistingOrThrow(roleId)).willReturn(systemRole);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(SystemRoleNotDeletableException.class);

            then(commandManager).should(never()).persist(any());
        }
    }

    @Nested
    @DisplayName("execute 메서드 (UsageChecker 있음)")
    class ExecuteWithUsageChecker {

        @BeforeEach
        void setUpWithChecker() {
            sut =
                    new RoleDeleteCoordinator(
                            roleValidator,
                            Optional.of(roleUsageChecker),
                            commandFactory,
                            commandManager);
        }

        @Test
        @DisplayName("성공: Validator → UsageChecker → Domain.delete → Manager 순서로 호출")
        void shouldCallUsageChecker_ThenDelete_WhenNotInUse() {
            // given
            DeleteRoleCommand command = RoleCommandFixtures.deleteCommand();
            RoleId roleId = RoleFixture.defaultId();
            Instant changedAt = Instant.now();
            StatusChangeContext<RoleId> context = new StatusChangeContext<>(roleId, changedAt);
            Role role = RoleFixture.create();

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(roleValidator.findExistingOrThrow(roleId)).willReturn(role);

            // when
            sut.execute(command);

            // then
            then(roleUsageChecker).should().validateNotInUse(roleId);
            then(commandManager).should().persist(role);
        }

        @Test
        @DisplayName("실패: 역할이 사용 중이면 RoleInUseException 발생, delete·Manager 미호출")
        void shouldThrowException_WhenRoleInUse() {
            // given
            DeleteRoleCommand command = RoleCommandFixtures.deleteCommand();
            RoleId roleId = RoleFixture.defaultId();
            Instant changedAt = Instant.now();
            StatusChangeContext<RoleId> context = new StatusChangeContext<>(roleId, changedAt);
            Role role = RoleFixture.create();

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(roleValidator.findExistingOrThrow(roleId)).willReturn(role);
            doThrow(new RoleInUseException(roleId))
                    .when(roleUsageChecker)
                    .validateNotInUse(eq(roleId));

            // when & then
            assertThatThrownBy(() -> sut.execute(command)).isInstanceOf(RoleInUseException.class);

            then(commandManager).should(never()).persist(any());
        }
    }
}
