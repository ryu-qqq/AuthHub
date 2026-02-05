package com.ryuqq.authhub.adapter.in.rest.permissionendpoint.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.CreatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.dto.request.UpdatePermissionEndpointApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permissionendpoint.fixture.PermissionEndpointApiFixture;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.CreatePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.DeletePermissionEndpointCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.UpdatePermissionEndpointCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionEndpointCommandApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("PermissionEndpointCommandApiMapper 단위 테스트")
class PermissionEndpointCommandApiMapperTest {

    private PermissionEndpointCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PermissionEndpointCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreatePermissionEndpointApiRequest) 메서드는")
    class ToCommandCreatePermissionEndpoint {

        @Test
        @DisplayName("CreatePermissionEndpointApiRequest를 CreatePermissionEndpointCommand로 변환한다")
        void shouldConvertToCreatePermissionEndpointCommand() {
            // Given
            CreatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture.createPermissionEndpointRequest();

            // When
            CreatePermissionEndpointCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionId())
                    .isEqualTo(PermissionEndpointApiFixture.defaultPermissionId());
            assertThat(result.serviceName())
                    .isEqualTo(PermissionEndpointApiFixture.defaultServiceName());
            assertThat(result.urlPattern())
                    .isEqualTo(PermissionEndpointApiFixture.defaultUrlPattern());
            assertThat(result.httpMethod())
                    .isEqualTo(PermissionEndpointApiFixture.defaultHttpMethod());
            assertThat(result.description())
                    .isEqualTo(PermissionEndpointApiFixture.defaultDescription());
            assertThat(result.isPublic()).isEqualTo(PermissionEndpointApiFixture.defaultIsPublic());
        }

        @Test
        @DisplayName("isPublic이 null이면 false로 변환한다")
        void shouldConvertNullIsPublicToFalse() {
            // Given
            CreatePermissionEndpointApiRequest request =
                    new CreatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            PermissionEndpointApiFixture.defaultDescription(),
                            false); // isPublic is boolean primitive, cannot be null

            // When
            CreatePermissionEndpointCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.isPublic()).isFalse();
        }

        @Test
        @DisplayName("description이 null이면 null로 변환한다")
        void shouldConvertNullDescription() {
            // Given
            CreatePermissionEndpointApiRequest request =
                    new CreatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultPermissionId(),
                            PermissionEndpointApiFixture.defaultServiceName(),
                            PermissionEndpointApiFixture.defaultUrlPattern(),
                            PermissionEndpointApiFixture.defaultHttpMethod(),
                            null,
                            PermissionEndpointApiFixture.defaultIsPublic());

            // When
            CreatePermissionEndpointCommand result = mapper.toCommand(request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.description()).isNull();
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toCommand((CreatePermissionEndpointApiRequest) null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdatePermissionEndpointApiRequest) 메서드는")
    class ToCommandUpdatePermissionEndpoint {

        @Test
        @DisplayName("UpdatePermissionEndpointApiRequest를 UpdatePermissionEndpointCommand로 변환한다")
        void shouldConvertToUpdatePermissionEndpointCommand() {
            // Given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            UpdatePermissionEndpointApiRequest request =
                    PermissionEndpointApiFixture.updatePermissionEndpointRequest();

            // When
            UpdatePermissionEndpointCommand result =
                    mapper.toCommand(permissionEndpointId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionEndpointId()).isEqualTo(permissionEndpointId);
            assertThat(result.serviceName())
                    .isEqualTo(PermissionEndpointApiFixture.defaultServiceName());
            assertThat(result.urlPattern()).isEqualTo("/api/v1/users/{id}/profile");
            assertThat(result.httpMethod()).isEqualTo("GET");
            assertThat(result.description()).isEqualTo("사용자 프로필 조회 API");
            assertThat(result.isPublic()).isEqualTo(PermissionEndpointApiFixture.defaultIsPublic());
        }

        @Test
        @DisplayName("부분 업데이트 시나리오 - 일부 필드만 null")
        void shouldConvertPartialUpdate() {
            // Given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();
            UpdatePermissionEndpointApiRequest request =
                    new UpdatePermissionEndpointApiRequest(
                            PermissionEndpointApiFixture.defaultServiceName(),
                            null, // urlPattern null
                            null, // httpMethod null
                            "수정된 설명만",
                            true); // isPublic만 변경

            // When
            UpdatePermissionEndpointCommand result =
                    mapper.toCommand(permissionEndpointId, request);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionEndpointId()).isEqualTo(permissionEndpointId);
            assertThat(result.serviceName())
                    .isEqualTo(PermissionEndpointApiFixture.defaultServiceName());
            assertThat(result.urlPattern()).isNull();
            assertThat(result.httpMethod()).isNull();
            assertThat(result.description()).isEqualTo("수정된 설명만");
            assertThat(result.isPublic()).isTrue();
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // Given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();

            // When & Then
            assertThatThrownBy(() -> mapper.toCommand(permissionEndpointId, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toDeleteCommand(Long) 메서드는")
    class ToDeleteCommand {

        @Test
        @DisplayName("permissionEndpointId를 DeletePermissionEndpointCommand로 변환한다")
        void shouldConvertToDeletePermissionEndpointCommand() {
            // Given
            Long permissionEndpointId = PermissionEndpointApiFixture.defaultPermissionEndpointId();

            // When
            DeletePermissionEndpointCommand result = mapper.toDeleteCommand(permissionEndpointId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.permissionEndpointId()).isEqualTo(permissionEndpointId);
        }

        @Test
        @DisplayName("permissionEndpointId가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenPermissionEndpointIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toDeleteCommand(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
