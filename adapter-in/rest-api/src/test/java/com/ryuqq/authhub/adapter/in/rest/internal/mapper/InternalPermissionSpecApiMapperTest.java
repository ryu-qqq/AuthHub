package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.adapter.in.rest.internal.fixture.InternalApiFixture;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * InternalPermissionSpecApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("InternalPermissionSpecApiMapper 단위 테스트")
class InternalPermissionSpecApiMapperTest {

    private InternalPermissionSpecApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InternalPermissionSpecApiMapper();
    }

    @Nested
    @DisplayName("toApiResponse 메서드는")
    class ToApiResponse {

        @Test
        @DisplayName("단일 엔드포인트를 가진 EndpointPermissionSpecListResult를 변환한다")
        void shouldConvertSingleEndpoint() {
            // Given
            String serviceName = InternalApiFixture.defaultServiceName();
            String pathPattern = InternalApiFixture.defaultPathPattern();
            String httpMethod = InternalApiFixture.defaultHttpMethod();
            String permissionKey = InternalApiFixture.defaultPermissionKey();
            String description = InternalApiFixture.defaultDescription();

            EndpointPermissionSpecResult specResult =
                    new EndpointPermissionSpecResult(
                            serviceName,
                            pathPattern,
                            httpMethod,
                            List.of(permissionKey),
                            List.of(),
                            false,
                            description);

            Instant updatedAt = Instant.parse("2025-01-01T00:00:00Z");
            String version = String.valueOf(updatedAt.toEpochMilli());
            EndpointPermissionSpecListResult result =
                    new EndpointPermissionSpecListResult(version, updatedAt, List.of(specResult));

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.version()).isEqualTo(version);
            assertThat(response.updatedAt()).isEqualTo(updatedAt);
            assertThat(response.endpoints()).hasSize(1);
            assertThat(response.endpoints().get(0).serviceName()).isEqualTo(serviceName);
            assertThat(response.endpoints().get(0).pathPattern()).isEqualTo(pathPattern);
            assertThat(response.endpoints().get(0).httpMethod()).isEqualTo(httpMethod);
            assertThat(response.endpoints().get(0).requiredPermissions())
                    .containsExactly(permissionKey);
        }

        @Test
        @DisplayName("다중 엔드포인트를 가진 EndpointPermissionSpecListResult를 변환한다")
        void shouldConvertMultipleEndpoints() {
            // Given
            EndpointPermissionSpecResult specResult1 =
                    new EndpointPermissionSpecResult(
                            "service1",
                            "/api/v1/products",
                            "GET",
                            List.of("product:read"),
                            List.of(),
                            false,
                            "상품 조회");

            EndpointPermissionSpecResult specResult2 =
                    new EndpointPermissionSpecResult(
                            "service1",
                            "/api/v1/products",
                            "POST",
                            List.of("product:create"),
                            List.of("ADMIN"),
                            false,
                            "상품 생성");

            Instant updatedAt = Instant.parse("2025-01-01T00:00:00Z");
            String version = String.valueOf(updatedAt.toEpochMilli());
            EndpointPermissionSpecListResult result =
                    new EndpointPermissionSpecListResult(
                            version, updatedAt, List.of(specResult1, specResult2));

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.version()).isEqualTo(version);
            assertThat(response.updatedAt()).isEqualTo(updatedAt);
            assertThat(response.endpoints()).hasSize(2);
            assertThat(response.endpoints().get(0).httpMethod()).isEqualTo("GET");
            assertThat(response.endpoints().get(1).httpMethod()).isEqualTo("POST");
        }

        @Test
        @DisplayName("빈 리스트를 가진 EndpointPermissionSpecListResult를 변환한다")
        void shouldConvertEmptyList() {
            // Given
            EndpointPermissionSpecListResult result = EndpointPermissionSpecListResult.empty();

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.version()).isEqualTo("0");
            assertThat(response.endpoints()).isEmpty();
        }

        @Test
        @DisplayName("version과 updatedAt 필드를 올바르게 변환한다")
        void shouldConvertVersionAndUpdatedAt() {
            // Given
            Instant updatedAt = Instant.parse("2025-12-31T23:59:59Z");
            String version = String.valueOf(updatedAt.toEpochMilli());
            EndpointPermissionSpecListResult result =
                    new EndpointPermissionSpecListResult(version, updatedAt, List.of());

            // When
            var response = mapper.toApiResponse(result);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.version()).isEqualTo(version);
            assertThat(response.updatedAt()).isEqualTo(updatedAt);
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
