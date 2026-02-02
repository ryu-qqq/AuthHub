package com.ryuqq.authhub.sdk.client.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.internal.EndpointPermissionSpecList;
import com.ryuqq.authhub.sdk.model.internal.PublicKeys;
import com.ryuqq.authhub.sdk.model.internal.TenantConfig;
import com.ryuqq.authhub.sdk.model.internal.UserPermissions;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * DefaultInternalApi 단위 테스트
 *
 * <p>ServiceTokenHttpClientSupport를 Mock으로 모의하여 각 메서드가 올바른 경로와 인자로 호출되는지 검증합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultInternalApi 단위 테스트")
class DefaultInternalApiTest {

    @Mock private ServiceTokenHttpClientSupport httpClient;

    private DefaultInternalApi sut;

    @BeforeEach
    void setUp() {
        sut = new DefaultInternalApi(httpClient);
    }

    @Nested
    @DisplayName("getPermissionSpec 메서드")
    class GetPermissionSpec {

        @Test
        @DisplayName("올바른 경로로 GET 요청을 보낸다")
        @SuppressWarnings("unchecked")
        void shouldCallGetWithCorrectPath() {
            // given
            String expectedPath = "/api/v1/internal/endpoint-permissions/spec";
            EndpointPermissionSpecList specList =
                    new EndpointPermissionSpecList("v1", Instant.now(), List.of());
            ApiResponse<EndpointPermissionSpecList> mockResponse =
                    new ApiResponse<>(true, specList, null, null);
            given(httpClient.get(eq(expectedPath), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            ApiResponse<EndpointPermissionSpecList> result = sut.getPermissionSpec();

            // then
            then(httpClient).should().get(eq(expectedPath), any(TypeReference.class));
            assertThat(result).isNotNull();
            assertThat(result.success()).isTrue();
        }
    }

    @Nested
    @DisplayName("getJwks 메서드")
    class GetJwks {

        @Test
        @DisplayName("올바른 경로로 GET 요청을 보낸다")
        @SuppressWarnings("unchecked")
        void shouldCallGetWithCorrectPath() {
            // given
            String expectedPath = "/api/v1/auth/jwks";
            PublicKeys mockResponse = new PublicKeys(List.of());
            given(httpClient.get(eq(expectedPath), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            PublicKeys result = sut.getJwks();

            // then
            then(httpClient).should().get(eq(expectedPath), any(TypeReference.class));
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("getTenantConfig 메서드")
    class GetTenantConfig {

        @Test
        @DisplayName("올바른 경로로 GET 요청을 보낸다")
        @SuppressWarnings("unchecked")
        void shouldCallGetWithCorrectPath() {
            // given
            String tenantId = "test-tenant-id";
            String expectedPath = String.format("/api/v1/internal/tenants/%s/config", tenantId);
            TenantConfig tenantConfig = new TenantConfig(tenantId, "Test Tenant", "ACTIVE", true);
            ApiResponse<TenantConfig> mockResponse =
                    new ApiResponse<>(true, tenantConfig, null, null);
            given(httpClient.get(eq(expectedPath), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            ApiResponse<TenantConfig> result = sut.getTenantConfig(tenantId);

            // then
            then(httpClient).should().get(eq(expectedPath), any(TypeReference.class));
            assertThat(result).isNotNull();
            assertThat(result.success()).isTrue();
            assertThat(result.data().tenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("다른 tenantId로 호출하면 다른 경로가 사용된다")
        @SuppressWarnings("unchecked")
        void shouldUseDifferentPathForDifferentTenantId() {
            // given
            String tenantId = "another-tenant-123";
            String expectedPath = String.format("/api/v1/internal/tenants/%s/config", tenantId);
            TenantConfig tenantConfig =
                    new TenantConfig(tenantId, "Another Tenant", "ACTIVE", true);
            ApiResponse<TenantConfig> mockResponse =
                    new ApiResponse<>(true, tenantConfig, null, null);
            given(httpClient.get(eq(expectedPath), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            sut.getTenantConfig(tenantId);

            // then
            then(httpClient).should().get(eq(expectedPath), any(TypeReference.class));
        }
    }

    @Nested
    @DisplayName("getUserPermissions 메서드")
    class GetUserPermissions {

        @Test
        @DisplayName("올바른 경로로 GET 요청을 보낸다")
        @SuppressWarnings("unchecked")
        void shouldCallGetWithCorrectPath() {
            // given
            String userId = "test-user-id";
            String expectedPath = String.format("/api/v1/internal/users/%s/permissions", userId);
            UserPermissions userPermissions =
                    new UserPermissions(userId, Set.of("ADMIN", "USER"), Set.of("read", "write"));
            ApiResponse<UserPermissions> mockResponse =
                    new ApiResponse<>(true, userPermissions, null, null);
            given(httpClient.get(eq(expectedPath), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            ApiResponse<UserPermissions> result = sut.getUserPermissions(userId);

            // then
            then(httpClient).should().get(eq(expectedPath), any(TypeReference.class));
            assertThat(result).isNotNull();
            assertThat(result.success()).isTrue();
            assertThat(result.data().userId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("다른 userId로 호출하면 다른 경로가 사용된다")
        @SuppressWarnings("unchecked")
        void shouldUseDifferentPathForDifferentUserId() {
            // given
            String userId = "another-user-456";
            String expectedPath = String.format("/api/v1/internal/users/%s/permissions", userId);
            UserPermissions userPermissions =
                    new UserPermissions(userId, Set.of("VIEWER"), Set.of("read"));
            ApiResponse<UserPermissions> mockResponse =
                    new ApiResponse<>(true, userPermissions, null, null);
            given(httpClient.get(eq(expectedPath), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            sut.getUserPermissions(userId);

            // then
            then(httpClient).should().get(eq(expectedPath), any(TypeReference.class));
        }
    }
}
