package com.ryuqq.authhub.application.permissionendpoint.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.factory.PermissionEndpointCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.fixture.PermissionEndpointCommandFixtures;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.validator.PermissionEndpointValidator;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpointUpdateData;
import com.ryuqq.authhub.domain.permissionendpoint.exception.DuplicatePermissionEndpointException;
import com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
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
 * UpdatePermissionEndpointService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdatePermissionEndpointService 단위 테스트")
class UpdatePermissionEndpointServiceTest {

    private static final Instant FIXED_TIME = PermissionEndpointFixture.fixedTime();

    @Mock private PermissionEndpointValidator validator;
    @Mock private PermissionEndpointCommandFactory commandFactory;
    @Mock private PermissionEndpointCommandManager commandManager;

    private UpdatePermissionEndpointService sut;

    @BeforeEach
    void setUp() {
        sut = new UpdatePermissionEndpointService(validator, commandFactory, commandManager);
    }

    @Nested
    @DisplayName("update 메서드")
    class Update {

        @Test
        @DisplayName(
                "성공: Factory → Validator → validateNoDuplicateExcludeSelf → Domain update → Manager"
                        + " persist")
        void shouldUpdate_WhenNoDuplicate() {
            UpdatePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.updateCommand();
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            PermissionEndpoint permissionEndpoint = PermissionEndpointFixture.create();
            PermissionEndpointUpdateData updateData =
                    PermissionEndpointUpdateData.of(
                            command.serviceName(),
                            command.urlPattern(),
                            command.httpMethod(),
                            command.description(),
                            command.isPublic());
            UpdateContext<PermissionEndpointId, PermissionEndpointUpdateData> context =
                    new UpdateContext<>(id, updateData, FIXED_TIME);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(id)).willReturn(permissionEndpoint);

            sut.update(command);

            then(validator).should().findExistingOrThrow(id);
            then(validator).should().validateNoDuplicateExcludeSelf(any(), any(), any());
            then(commandManager).should().persist(permissionEndpoint);
        }

        @Test
        @DisplayName("실패: 엔드포인트가 존재하지 않으면 PermissionEndpointNotFoundException 발생")
        void shouldThrow_WhenEndpointNotExists() {
            UpdatePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.updateCommand();
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            UpdateContext<PermissionEndpointId, PermissionEndpointUpdateData> context =
                    new UpdateContext<>(id, null, FIXED_TIME);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(id))
                    .willThrow(new PermissionEndpointNotFoundException(id));

            assertThatThrownBy(() -> sut.update(command))
                    .isInstanceOf(PermissionEndpointNotFoundException.class);
            then(commandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("실패: 다른 엔드포인트와 URL+메서드 중복이면 DuplicatePermissionEndpointException 발생")
        void shouldThrow_WhenDuplicateExcludeSelf() {
            UpdatePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.updateCommand();
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            PermissionEndpoint permissionEndpoint = PermissionEndpointFixture.create();
            PermissionEndpointUpdateData updateData =
                    PermissionEndpointUpdateData.of(
                            command.serviceName(),
                            command.urlPattern(),
                            command.httpMethod(),
                            command.description(),
                            command.isPublic());
            UpdateContext<PermissionEndpointId, PermissionEndpointUpdateData> context =
                    new UpdateContext<>(id, updateData, FIXED_TIME);
            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(id)).willReturn(permissionEndpoint);
            doThrow(
                            new DuplicatePermissionEndpointException(
                                    command.urlPattern(), command.httpMethod()))
                    .when(validator)
                    .validateNoDuplicateExcludeSelf(
                            eq(id), any(String.class), any(HttpMethod.class));

            assertThatThrownBy(() -> sut.update(command))
                    .isInstanceOf(DuplicatePermissionEndpointException.class);
            then(commandManager).should(never()).persist(any());
        }
    }
}
