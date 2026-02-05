package com.ryuqq.authhub.application.permission.internal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.factory.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.fixture.PermissionCommandFixtures;
import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.PermissionInUseException;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
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
 * PermissionDeleteCoordinator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionDeleteCoordinator 단위 테스트")
class PermissionDeleteCoordinatorTest {

    @Mock private PermissionValidator permissionValidator;
    @Mock private PermissionUsageChecker permissionUsageChecker;
    @Mock private PermissionCommandFactory commandFactory;
    @Mock private PermissionCommandManager commandManager;

    private PermissionDeleteCoordinator sutWithChecker;
    private PermissionDeleteCoordinator sutWithoutChecker;

    @BeforeEach
    void setUp() {
        sutWithChecker =
                new PermissionDeleteCoordinator(
                        permissionValidator,
                        Optional.of(permissionUsageChecker),
                        commandFactory,
                        commandManager);
        sutWithoutChecker =
                new PermissionDeleteCoordinator(
                        permissionValidator, Optional.empty(), commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드 (UsageChecker 주입됨)")
    class ExecuteWithChecker {

        @Test
        @DisplayName("성공: Factory → Validator → UsageChecker → Domain delete → Manager 순서로 호출")
        void shouldSucceed_WhenAllValidationsPass() {
            // given
            DeletePermissionCommand command = PermissionCommandFixtures.deleteCommand();
            Permission permission = PermissionFixture.createNewCustomPermission();
            PermissionId permissionId = PermissionFixture.defaultId();
            Instant changedAt = PermissionFixture.fixedTime();
            StatusChangeContext<PermissionId> context =
                    new StatusChangeContext<>(permissionId, changedAt);

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(permissionValidator.findExistingOrThrow(permissionId)).willReturn(permission);

            // when
            assertThatCode(() -> sutWithChecker.execute(command)).doesNotThrowAnyException();

            // then
            then(commandFactory).should().createDeleteContext(command);
            then(permissionValidator).should().findExistingOrThrow(permissionId);
            then(permissionUsageChecker).should().validateNotInUse(permissionId);
            then(commandManager).should().persist(permission);
        }

        @Test
        @DisplayName("실패: 권한이 존재하지 않으면 PermissionNotFoundException 발생, Manager 미호출")
        void shouldThrowPermissionNotFoundException_WhenPermissionNotExists() {
            // given
            DeletePermissionCommand command = PermissionCommandFixtures.deleteCommand();
            PermissionId permissionId = PermissionFixture.defaultId();
            StatusChangeContext<PermissionId> context =
                    new StatusChangeContext<>(permissionId, PermissionFixture.fixedTime());

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(permissionValidator.findExistingOrThrow(permissionId))
                    .willThrow(new PermissionNotFoundException(permissionId));

            // when & then
            assertThatThrownBy(() -> sutWithChecker.execute(command))
                    .isInstanceOf(PermissionNotFoundException.class);

            then(permissionUsageChecker).shouldHaveNoInteractions();
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("실패: 권한이 사용 중이면 PermissionInUseException 발생, Manager 미호출")
        void shouldThrowPermissionInUseException_WhenPermissionInUse() {
            // given
            DeletePermissionCommand command = PermissionCommandFixtures.deleteCommand();
            Permission permission = PermissionFixture.createNewCustomPermission();
            PermissionId permissionId = PermissionFixture.defaultId();
            StatusChangeContext<PermissionId> context =
                    new StatusChangeContext<>(permissionId, PermissionFixture.fixedTime());

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(permissionValidator.findExistingOrThrow(permissionId)).willReturn(permission);
            willThrow(new PermissionInUseException(permissionId))
                    .given(permissionUsageChecker)
                    .validateNotInUse(permissionId);

            // when & then
            assertThatThrownBy(() -> sutWithChecker.execute(command))
                    .isInstanceOf(PermissionInUseException.class);

            then(commandManager).should(never()).persist(any());
        }
    }

    @Nested
    @DisplayName("execute 메서드 (UsageChecker 비주입)")
    class ExecuteWithoutChecker {

        @Test
        @DisplayName("성공: UsageChecker 없이 Validator → Domain delete → Manager만 호출")
        void shouldSucceed_WhenUsageCheckerNotPresent() {
            // given
            DeletePermissionCommand command = PermissionCommandFixtures.deleteCommand();
            Permission permission = PermissionFixture.createNewCustomPermission();
            PermissionId permissionId = PermissionFixture.defaultId();
            StatusChangeContext<PermissionId> context =
                    new StatusChangeContext<>(permissionId, PermissionFixture.fixedTime());

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(permissionValidator.findExistingOrThrow(permissionId)).willReturn(permission);

            // when
            assertThatCode(() -> sutWithoutChecker.execute(command)).doesNotThrowAnyException();

            // then
            then(commandFactory).should().createDeleteContext(command);
            then(permissionValidator).should().findExistingOrThrow(permissionId);
            then(commandManager).should().persist(permission);
        }
    }
}
