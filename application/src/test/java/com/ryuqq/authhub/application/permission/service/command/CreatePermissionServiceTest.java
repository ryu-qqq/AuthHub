package com.ryuqq.authhub.application.permission.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.permission.assembler.PermissionAssembler;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.permission.factory.command.PermissionCommandFactory;
import com.ryuqq.authhub.application.permission.manager.command.PermissionTransactionManager;
import com.ryuqq.authhub.application.permission.validator.PermissionValidator;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.DuplicatePermissionKeyException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CreatePermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePermissionService 단위 테스트")
class CreatePermissionServiceTest {

    @Mock private PermissionValidator validator;

    @Mock private PermissionCommandFactory commandFactory;

    @Mock private PermissionTransactionManager transactionManager;

    @Mock private PermissionAssembler assembler;

    private CreatePermissionService service;

    @BeforeEach
    void setUp() {
        service =
                new CreatePermissionService(
                        validator, commandFactory, transactionManager, assembler);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("권한을 성공적으로 생성한다")
        void shouldCreatePermissionSuccessfully() {
            // given
            Permission permission = PermissionFixture.create();
            CreatePermissionCommand command =
                    new CreatePermissionCommand("user", "read", "사용자 조회 권한", false);
            PermissionResponse expectedResponse =
                    new PermissionResponse(
                            permission.permissionIdValue(),
                            permission.keyValue(),
                            permission.resourceValue(),
                            permission.actionValue(),
                            permission.descriptionValue(),
                            permission.getType().name(),
                            permission.createdAt(),
                            permission.updatedAt());

            // validator는 예외를 던지지 않으면 통과 (doNothing 기본 동작)
            given(commandFactory.create(command)).willReturn(permission);
            given(transactionManager.persist(permission)).willReturn(permission);
            given(assembler.toResponse(permission)).willReturn(expectedResponse);

            // when
            PermissionResponse response = service.execute(command);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            assertThat(response.key()).isEqualTo("user:read");
            verify(validator).validateKeyNotDuplicated(any(PermissionKey.class));
            verify(commandFactory).create(command);
            verify(transactionManager).persist(permission);
            verify(assembler).toResponse(permission);
        }

        @Test
        @DisplayName("중복 권한 키 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDuplicateKey() {
            // given
            CreatePermissionCommand command =
                    new CreatePermissionCommand("user", "read", "설명", false);

            Mockito.doThrow(new DuplicatePermissionKeyException("user:read"))
                    .when(validator)
                    .validateKeyNotDuplicated(any(PermissionKey.class));

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(DuplicatePermissionKeyException.class);

            verify(commandFactory, never()).create(any());
            verify(transactionManager, never()).persist(any());
        }

        @Test
        @DisplayName("시스템 권한을 성공적으로 생성한다")
        void shouldCreateSystemPermissionSuccessfully() {
            // given
            Permission systemPermission = PermissionFixture.createSystem();
            CreatePermissionCommand command =
                    new CreatePermissionCommand("user", "read", "시스템 권한", true);
            PermissionResponse expectedResponse =
                    new PermissionResponse(
                            systemPermission.permissionIdValue(),
                            systemPermission.keyValue(),
                            systemPermission.resourceValue(),
                            systemPermission.actionValue(),
                            systemPermission.descriptionValue(),
                            systemPermission.getType().name(),
                            systemPermission.createdAt(),
                            systemPermission.updatedAt());

            // validator는 예외를 던지지 않으면 통과 (doNothing 기본 동작)
            given(commandFactory.create(command)).willReturn(systemPermission);
            given(transactionManager.persist(systemPermission)).willReturn(systemPermission);
            given(assembler.toResponse(systemPermission)).willReturn(expectedResponse);

            // when
            PermissionResponse response = service.execute(command);

            // then
            assertThat(response.type()).isEqualTo("SYSTEM");
            verify(validator).validateKeyNotDuplicated(any(PermissionKey.class));
            verify(transactionManager).persist(systemPermission);
        }
    }
}
