package com.ryuqq.authhub.adapter.in.rest.permission.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.request.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.fixture.PermissionApiFixture;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("PermissionCommandApiMapper 단위 테스트")
class PermissionCommandApiMapperTest {

    private PermissionCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PermissionCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreatePermissionApiRequest) 메서드는")
    class ToCommandCreatePermission {

        @Test
        @DisplayName("CreatePermissionApiRequest를 CreatePermissionCommand로 변환한다")
        void shouldConvertToCreatePermissionCommand() {
            // Given
            CreatePermissionApiRequest request = PermissionApiFixture.createPermissionRequest();

            // When
            CreatePermissionCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.resource()).isEqualTo(PermissionApiFixture.defaultResource());
            assertThat(result.action()).isEqualTo(PermissionApiFixture.defaultAction());
            assertThat(result.description()).isEqualTo(PermissionApiFixture.defaultDescription());
            assertThat(result.isSystem()).isFalse(); // REST API를 통한 생성은 항상 CUSTOM
        }

        @Test
        @DisplayName("serviceId가 null인 CreatePermissionApiRequest를 변환한다")
        void shouldConvertWhenServiceIdIsNull() {
            // Given
            CreatePermissionApiRequest request =
                    PermissionApiFixture.createPermissionRequest(
                            PermissionApiFixture.defaultResource(),
                            PermissionApiFixture.defaultAction());

            // When
            CreatePermissionCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceId()).isNull();
            assertThat(result.isSystem()).isFalse();
        }

        @Test
        @DisplayName("serviceId가 있는 CreatePermissionApiRequest를 변환한다")
        void shouldConvertWhenServiceIdIsPresent() {
            // Given
            Long serviceId = 1L;
            CreatePermissionApiRequest request =
                    new CreatePermissionApiRequest(
                            serviceId,
                            PermissionApiFixture.defaultResource(),
                            PermissionApiFixture.defaultAction(),
                            PermissionApiFixture.defaultDescription());

            // When
            CreatePermissionCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.serviceId()).isEqualTo(serviceId);
            assertThat(result.isSystem()).isFalse();
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toCommand((CreatePermissionApiRequest) null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdatePermissionApiRequest) 메서드는")
    class ToCommandUpdatePermission {

        @Test
        @DisplayName("UpdatePermissionApiRequest를 UpdatePermissionCommand로 변환한다")
        void shouldConvertToUpdatePermissionCommand() {
            // Given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            UpdatePermissionApiRequest request = PermissionApiFixture.updatePermissionRequest();

            // When
            UpdatePermissionCommand result = mapper.toCommand(permissionId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionId()).isEqualTo(permissionId);
            assertThat(result.description()).isEqualTo("수정된 권한 설명");
        }

        @Test
        @DisplayName("description이 null인 UpdatePermissionApiRequest를 변환한다")
        void shouldConvertWhenDescriptionIsNull() {
            // Given
            Long permissionId = PermissionApiFixture.defaultPermissionId();
            UpdatePermissionApiRequest request = new UpdatePermissionApiRequest(null);

            // When
            UpdatePermissionCommand result = mapper.toCommand(permissionId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionId()).isEqualTo(permissionId);
            assertThat(result.description()).isNull();
        }

        // Note: Long permissionId can be null - validation happens at Application/Domain layer

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // Given
            Long permissionId = PermissionApiFixture.defaultPermissionId();

            // When & Then
            assertThatThrownBy(() -> mapper.toCommand(permissionId, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toDeleteCommand(Long) 메서드는")
    class ToDeleteCommand {

        @Test
        @DisplayName("permissionId를 DeletePermissionCommand로 변환한다")
        void shouldConvertToDeletePermissionCommand() {
            // Given
            Long permissionId = PermissionApiFixture.defaultPermissionId();

            // When
            DeletePermissionCommand result = mapper.toDeleteCommand(permissionId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionId()).isEqualTo(permissionId);
        }

        // Note: Long permissionId can be null - validation happens at Application/Domain layer
    }
}
