package com.ryuqq.authhub.application.permissionendpoint.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.common.dto.command.StatusChangeContext;
import com.ryuqq.authhub.application.common.dto.command.UpdateContext;
import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.fixture.PermissionEndpointCommandFixtures;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpointUpdateData;
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
 * PermissionEndpointCommandFactory 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionEndpointCommandFactory 단위 테스트")
class PermissionEndpointCommandFactoryTest {

    @Mock private TimeProvider timeProvider;

    private PermissionEndpointCommandFactory sut;

    private static final Instant FIXED_TIME = PermissionEndpointFixture.fixedTime();

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointCommandFactory(timeProvider);
    }

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("성공: Command로부터 PermissionEndpoint 도메인 객체 생성")
        void shouldCreatePermissionEndpoint_FromCommand() {
            // given
            CreatePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.createCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            PermissionEndpoint result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.permissionIdValue()).isEqualTo(command.permissionId());
            assertThat(result.urlPatternValue()).isEqualTo(command.urlPattern());
            assertThat(result.httpMethodValue()).isEqualTo(command.httpMethod());
            assertThat(result.createdAt()).isEqualTo(FIXED_TIME);
        }
    }

    @Nested
    @DisplayName("createUpdateContext 메서드")
    class CreateUpdateContext {

        @Test
        @DisplayName("성공: Command로부터 UpdateContext 생성")
        void shouldCreateUpdateContext_FromCommand() {
            // given
            UpdatePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.updateCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            UpdateContext<PermissionEndpointId, PermissionEndpointUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.permissionEndpointId());
            assertThat(result.updateData().urlPattern()).isEqualTo(command.urlPattern());
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
            DeletePermissionEndpointCommand command =
                    PermissionEndpointCommandFixtures.deleteCommand();

            given(timeProvider.now()).willReturn(FIXED_TIME);

            // when
            StatusChangeContext<PermissionEndpointId> result = sut.createDeleteContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(command.permissionEndpointId());
            assertThat(result.changedAt()).isEqualTo(FIXED_TIME);
        }
    }
}
