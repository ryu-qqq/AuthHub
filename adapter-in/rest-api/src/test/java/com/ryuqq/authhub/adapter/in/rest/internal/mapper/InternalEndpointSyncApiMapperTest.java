package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.EndpointSyncApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InternalEndpointSyncApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("InternalEndpointSyncApiMapper 단위 테스트")
class InternalEndpointSyncApiMapperTest {

    private InternalEndpointSyncApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InternalEndpointSyncApiMapper();
    }

    @Nested
    @DisplayName("toCommand 메서드는")
    class ToCommand {

        @Test
        @DisplayName("EndpointSyncApiRequest를 SyncEndpointsCommand로 정상 변환한다")
        void shouldConvertToSyncEndpointsCommand() {
            // Given
            EndpointSyncApiRequest request = InternalApiFixture.endpointSyncRequest();

            // When
            SyncEndpointsCommand command = mapper.toCommand(request);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.serviceName()).isEqualTo(InternalApiFixture.defaultServiceName());
            assertThat(command.endpoints()).hasSize(1);
            assertThat(command.endpoints().get(0).httpMethod())
                    .isEqualTo(InternalApiFixture.defaultHttpMethod());
            assertThat(command.endpoints().get(0).pathPattern())
                    .isEqualTo(InternalApiFixture.defaultPathPattern());
            assertThat(command.endpoints().get(0).permissionKey())
                    .isEqualTo(InternalApiFixture.defaultPermissionKey());
            assertThat(command.endpoints().get(0).description())
                    .isEqualTo(InternalApiFixture.defaultDescription());
        }

        @Test
        @DisplayName("빈 엔드포인트 리스트를 가진 요청을 변환한다")
        void shouldConvertEmptyEndpoints() {
            // Given
            EndpointSyncApiRequest request =
                    InternalApiFixture.endpointSyncRequestWithEmptyEndpoints(
                            InternalApiFixture.defaultServiceName());

            // When
            SyncEndpointsCommand command = mapper.toCommand(request);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.serviceName()).isEqualTo(InternalApiFixture.defaultServiceName());
            assertThat(command.endpoints()).isEmpty();
        }

        @Test
        @DisplayName("여러 엔드포인트를 가진 요청을 변환한다")
        void shouldConvertMultipleEndpoints() {
            // Given
            EndpointSyncApiRequest request =
                    InternalApiFixture.endpointSyncRequestWithMultipleEndpoints();

            // When
            SyncEndpointsCommand command = mapper.toCommand(request);

            // Then
            assertThat(command).isNotNull();
            assertThat(command.serviceName()).isEqualTo(InternalApiFixture.defaultServiceName());
            assertThat(command.endpoints()).hasSize(4);
            assertThat(command.endpoints().get(0).httpMethod()).isEqualTo("GET");
            assertThat(command.endpoints().get(1).httpMethod()).isEqualTo("POST");
            assertThat(command.endpoints().get(2).httpMethod()).isEqualTo("PUT");
            assertThat(command.endpoints().get(3).httpMethod()).isEqualTo("DELETE");
        }

        @Test
        @DisplayName("request가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenRequestIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toCommand(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("toApiResponse 메서드는")
    class ToApiResponse {

        @Test
        @DisplayName("SyncEndpointsResult를 EndpointSyncResultApiResponse로 정상 변환한다")
        void shouldConvertToEndpointSyncResultApiResponse() {
            // Given
            String serviceName = InternalApiFixture.defaultServiceName();
            int totalEndpoints = 4;
            int createdPermissions = 2;
            int createdEndpoints = 3;
            int skippedEndpoints = 1;
            SyncEndpointsResult result =
                    SyncEndpointsResult.of(
                            serviceName,
                            totalEndpoints,
                            createdPermissions,
                            createdEndpoints,
                            skippedEndpoints);

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.serviceName()).isEqualTo(serviceName);
            assertThat(response.totalEndpoints()).isEqualTo(totalEndpoints);
            assertThat(response.createdPermissions()).isEqualTo(createdPermissions);
            assertThat(response.createdEndpoints()).isEqualTo(createdEndpoints);
            assertThat(response.skippedEndpoints()).isEqualTo(skippedEndpoints);
        }

        @Test
        @DisplayName("통계 필드가 모두 0인 결과를 변환한다")
        void shouldConvertZeroStatistics() {
            // Given
            SyncEndpointsResult result = SyncEndpointsResult.of("test-service", 0, 0, 0, 0);

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.totalEndpoints()).isZero();
            assertThat(response.createdPermissions()).isZero();
            assertThat(response.createdEndpoints()).isZero();
            assertThat(response.skippedEndpoints()).isZero();
        }

        @Test
        @DisplayName("result가 null이면 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenResultIsNull() {
            // When & Then
            assertThatThrownBy(() -> mapper.toApiResponse(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
