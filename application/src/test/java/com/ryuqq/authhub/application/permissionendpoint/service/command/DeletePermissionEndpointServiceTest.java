package com.ryuqq.authhub.application.permissionendpoint.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.factory.PermissionEndpointCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.fixture.PermissionEndpointCommandFixtures;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.validator.PermissionEndpointValidator;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
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
 * DeletePermissionEndpointService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DeletePermissionEndpointService 단위 테스트")
class DeletePermissionEndpointServiceTest {

    private static final Instant FIXED_TIME = PermissionEndpointFixture.fixedTime();

    @Mock private PermissionEndpointValidator validator;
    @Mock private PermissionEndpointCommandFactory commandFactory;
    @Mock private PermissionEndpointCommandManager commandManager;

    private DeletePermissionEndpointService sut;

    @BeforeEach
    void setUp() {
        sut = new DeletePermissionEndpointService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("성공: Factory → Validator → Domain delete → Manager persist 순서로 호출")
        void shouldDelete_WhenEndpointExists() {
            DeletePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.deleteCommand();
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            StatusChangeContext<PermissionEndpointId> context =
                    new StatusChangeContext<>(id, FIXED_TIME);
            PermissionEndpoint permissionEndpoint = PermissionEndpointFixture.create();

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(id)).willReturn(permissionEndpoint);

            sut.delete(command);

            then(commandFactory).should().createDeleteContext(command);
            then(validator).should().findExistingOrThrow(id);
            then(commandManager).should().persist(permissionEndpoint);
        }

        @Test
        @DisplayName("실패: 엔드포인트가 존재하지 않으면 PermissionEndpointNotFoundException 발생")
        void shouldThrow_WhenEndpointNotExists() {
            DeletePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.deleteCommand();
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            StatusChangeContext<PermissionEndpointId> context =
                    new StatusChangeContext<>(id, FIXED_TIME);

            given(commandFactory.createDeleteContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(id))
                    .willThrow(new PermissionEndpointNotFoundException(id));

            org.junit.jupiter.api.Assertions.assertThrows(
                    PermissionEndpointNotFoundException.class, () -> sut.delete(command));
            then(commandManager).should(never()).persist(org.mockito.ArgumentMatchers.any());
        }
    }
}
