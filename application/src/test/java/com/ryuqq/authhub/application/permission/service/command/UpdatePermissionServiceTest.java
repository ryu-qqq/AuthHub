package com.ryuqq.authhub.application.permission.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.factory.command.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.manager.command.PermissionTransactionManager;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotModifiableException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
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
 * UpdatePermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdatePermissionService 단위 테스트")
class UpdatePermissionServiceTest {

    @Mock private PermissionReadManager readManager;

    @Mock private PermissionCommandFactory commandFactory;

    @Mock private PermissionTransactionManager transactionManager;

    @Mock private PermissionAssembler assembler;

    private UpdatePermissionService service;

    @BeforeEach
    void setUp() {
        service =
                new UpdatePermissionService(
                        readManager, commandFactory, transactionManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("권한 설명을 성공적으로 변경한다")
        void shouldUpdateDescriptionSuccessfully() {
            // given
            Permission existingPermission = PermissionFixture.createReconstituted();
            UUID permissionId = existingPermission.permissionIdValue();
            UpdatePermissionCommand command = new UpdatePermissionCommand(permissionId, "변경된 설명");
            PermissionResponse expectedResponse =
                    new PermissionResponse(
                            permissionId,
                            existingPermission.keyValue(),
                            existingPermission.resourceValue(),
                            existingPermission.actionValue(),
                            "변경된 설명",
                            existingPermission.getType().name(),
                            existingPermission.createdAt(),
                            existingPermission.updatedAt());

            given(readManager.getById(PermissionId.of(permissionId)))
                    .willReturn(existingPermission);
            given(
                            commandFactory.applyUpdate(
                                    any(Permission.class), any(UpdatePermissionCommand.class)))
                    .willReturn(existingPermission);
            given(transactionManager.persist(any(Permission.class))).willReturn(existingPermission);
            given(assembler.toResponse(any(Permission.class))).willReturn(expectedResponse);

            // when
            PermissionResponse response = service.execute(command);

            // then
            assertThat(response.description()).isEqualTo("변경된 설명");
            verify(readManager).getById(PermissionId.of(permissionId));
            verify(commandFactory)
                    .applyUpdate(any(Permission.class), any(UpdatePermissionCommand.class));
            verify(transactionManager).persist(any(Permission.class));
        }

        @Test
        @DisplayName("SYSTEM 권한 수정 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenModifyingSystemPermission() {
            // given
            UUID permissionId = UUID.randomUUID();
            Permission systemPermission =
                    PermissionFixture.createReconstitutedSystem(permissionId, "system:admin");
            UpdatePermissionCommand command = new UpdatePermissionCommand(permissionId, "새 설명");

            given(readManager.getById(PermissionId.of(permissionId))).willReturn(systemPermission);
            given(
                            commandFactory.applyUpdate(
                                    any(Permission.class), any(UpdatePermissionCommand.class)))
                    .willThrow(
                            new SystemPermissionNotModifiableException(
                                    systemPermission.keyValue()));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(SystemPermissionNotModifiableException.class);

            verify(transactionManager, never()).persist(any());
        }
    }
}
