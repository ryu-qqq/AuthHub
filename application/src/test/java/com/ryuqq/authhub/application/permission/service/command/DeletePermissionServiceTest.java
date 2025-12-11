package com.ryuqq.authhub.application.permission.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.manager.command.PermissionTransactionManager;
import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.SystemPermissionNotDeletableException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
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
 * DeletePermissionService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeletePermissionService 단위 테스트")
class DeletePermissionServiceTest {

    @Mock private PermissionTransactionManager transactionManager;

    @Mock private PermissionReadManager readManager;

    private Clock clock;
    private DeletePermissionService service;

    @BeforeEach
    void setUp() {
        clock = PermissionFixture.fixedClock();
        service = new DeletePermissionService(transactionManager, readManager, clock);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("권한을 성공적으로 삭제한다")
        void shouldDeletePermissionSuccessfully() {
            // given
            Permission existingPermission = PermissionFixture.createReconstituted();
            UUID permissionId = existingPermission.permissionIdValue();
            DeletePermissionCommand command = new DeletePermissionCommand(permissionId);

            given(readManager.getById(PermissionId.of(permissionId)))
                    .willReturn(existingPermission);
            given(transactionManager.persist(any(Permission.class))).willReturn(existingPermission);

            // when
            service.execute(command);

            // then
            verify(readManager).getById(PermissionId.of(permissionId));
            verify(transactionManager).persist(any(Permission.class));
        }

        @Test
        @DisplayName("SYSTEM 권한 삭제 시 예외를 발생시킨다")
        void shouldThrowExceptionWhenDeletingSystemPermission() {
            // given
            UUID permissionId = UUID.randomUUID();
            Permission systemPermission =
                    PermissionFixture.createReconstitutedSystem(permissionId, "system:admin");
            DeletePermissionCommand command = new DeletePermissionCommand(permissionId);

            given(readManager.getById(PermissionId.of(permissionId))).willReturn(systemPermission);

            // when & then
            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(SystemPermissionNotDeletableException.class);

            verify(transactionManager, never()).persist(any());
        }
    }
}
