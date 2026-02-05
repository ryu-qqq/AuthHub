package com.ryuqq.authhub.application.permission.service.command;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.factory.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.fixture.PermissionCommandFixtures;
import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUpdateData;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
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
 * UpdatePermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdatePermissionService 단위 테스트")
class UpdatePermissionServiceTest {

    @Mock private PermissionValidator validator;
    @Mock private PermissionCommandFactory commandFactory;
    @Mock private PermissionCommandManager commandManager;

    private UpdatePermissionService sut;

    @BeforeEach
    void setUp() {
        sut = new UpdatePermissionService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("execute 메서드")
    class Execute {

        @Test
        @DisplayName("성공: Factory → Validator → Domain update → Manager 순서로 호출")
        void shouldSucceed_WhenPermissionIsCustom() {
            // given
            UpdatePermissionCommand command = PermissionCommandFixtures.updateCommand();
            Permission permission = PermissionFixture.create();
            PermissionId permissionId = PermissionFixture.defaultId();
            Instant changedAt = PermissionFixture.fixedTime();
            PermissionUpdateData updateData = PermissionUpdateData.of(command.description());
            UpdateContext<PermissionId, PermissionUpdateData> context =
                    new UpdateContext<>(permissionId, updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(permissionId)).willReturn(permission);

            // when
            assertThatCode(() -> sut.execute(command)).doesNotThrowAnyException();

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(permissionId);
            then(commandManager).should().persist(permission);
        }

        @Test
        @DisplayName("실패: 권한이 존재하지 않으면 PermissionNotFoundException 발생, Manager 미호출")
        void shouldThrowPermissionNotFoundException_WhenPermissionNotExists() {
            // given
            UpdatePermissionCommand command = PermissionCommandFixtures.updateCommand();
            PermissionId permissionId = PermissionFixture.defaultId();
            PermissionUpdateData updateData = PermissionUpdateData.of(command.description());
            UpdateContext<PermissionId, PermissionUpdateData> context =
                    new UpdateContext<>(permissionId, updateData, PermissionFixture.fixedTime());

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(permissionId))
                    .willThrow(new PermissionNotFoundException(permissionId));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(PermissionNotFoundException.class);

            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("실패: SYSTEM 권한 수정 시 SystemPermissionNotModifiableException 발생, Manager 미호출")
        void shouldThrowSystemPermissionNotModifiableException_WhenPermissionIsSystem() {
            // given
            UpdatePermissionCommand command = PermissionCommandFixtures.updateCommand();
            Permission systemPermission = PermissionFixture.createSystemPermission();
            PermissionId permissionId = PermissionFixture.defaultId();
            PermissionUpdateData updateData = PermissionUpdateData.of(command.description());
            UpdateContext<PermissionId, PermissionUpdateData> context =
                    new UpdateContext<>(permissionId, updateData, PermissionFixture.fixedTime());

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(permissionId)).willReturn(systemPermission);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(SystemPermissionNotModifiableException.class);

            then(commandManager).should(never()).persist(any());
        }
    }
}
