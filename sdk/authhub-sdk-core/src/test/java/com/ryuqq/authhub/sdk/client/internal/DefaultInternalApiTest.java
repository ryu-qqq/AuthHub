package com.ryuqq.authhub.sdk.client.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ryuqq.authhub.sdk.model.auth.ChangePasswordRequest;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.internal.EndpointPermissionSpecList;
import com.ryuqq.authhub.sdk.model.internal.PublicKeys;
import com.ryuqq.authhub.sdk.model.internal.TenantConfig;
import com.ryuqq.authhub.sdk.model.internal.UserContext;
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
                    new UserPermissions(
                            userId,
                            Set.of("ADMIN", "USER"),
                            Set.of("read", "write"),
                            "hash123",
                            Instant.now());
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
                    new UserPermissions(
                            userId, Set.of("VIEWER"), Set.of("read"), "hash456", Instant.now());
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

    @Nested
    @DisplayName("getUserContext 메서드")
    class GetUserContext {

        @Test
        @DisplayName("올바른 경로로 GET 요청을 보낸다")
        @SuppressWarnings("unchecked")
        void shouldCallGetWithCorrectPath() {
            // given
            String userId = "test-user-id";
            String expectedPath = String.format("/api/v1/internal/users/%s/context", userId);
            UserContext userContext =
                    new UserContext(
                            userId,
                            "test@example.com",
                            "테스트 사용자",
                            "010-1234-5678",
                            new UserContext.TenantInfo("tenant-001", "테스트 테넌트"),
                            new UserContext.OrganizationInfo("org-001", "테스트 조직"),
                            List.of(new UserContext.RoleInfo("role-001", "ADMIN")),
                            List.of("user:read", "user:write"));
            ApiResponse<UserContext> mockResponse =
                    new ApiResponse<>(true, userContext, null, null);
            given(httpClient.get(eq(expectedPath), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            ApiResponse<UserContext> result = sut.getUserContext(userId);

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
            String userId = "another-user-789";
            String expectedPath = String.format("/api/v1/internal/users/%s/context", userId);
            UserContext userContext =
                    new UserContext(
                            userId,
                            "another@example.com",
                            "다른 사용자",
                            null,
                            new UserContext.TenantInfo("tenant-002", "다른 테넌트"),
                            new UserContext.OrganizationInfo("org-002", "다른 조직"),
                            List.of(),
                            List.of());
            ApiResponse<UserContext> mockResponse =
                    new ApiResponse<>(true, userContext, null, null);
            given(httpClient.get(eq(expectedPath), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            sut.getUserContext(userId);

            // then
            then(httpClient).should().get(eq(expectedPath), any(TypeReference.class));
        }
    }

    @Nested
    @DisplayName("changePassword 메서드")
    class ChangePassword {

        @Test
        @DisplayName("올바른 경로로 PUT 요청을 보낸다")
        @SuppressWarnings("unchecked")
        void shouldCallPutWithCorrectPath() {
            // given
            String userId = "test-user-id";
            String expectedPath = String.format("/api/v1/internal/users/%s/password", userId);
            ChangePasswordRequest request = new ChangePasswordRequest("oldPass123", "newPass456");
            ApiResponse<Void> mockResponse = new ApiResponse<>(true, null, null, null);
            given(httpClient.put(eq(expectedPath), eq(request), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            sut.changePassword(userId, request);

            // then
            then(httpClient).should().put(eq(expectedPath), eq(request), any(TypeReference.class));
        }

        @Test
        @DisplayName("다른 userId로 호출하면 다른 경로가 사용된다")
        @SuppressWarnings("unchecked")
        void shouldUseDifferentPathForDifferentUserId() {
            // given
            String userId = "another-user-999";
            String expectedPath = String.format("/api/v1/internal/users/%s/password", userId);
            ChangePasswordRequest request = new ChangePasswordRequest("currentPw", "newPw123");
            ApiResponse<Void> mockResponse = new ApiResponse<>(true, null, null, null);
            given(httpClient.put(eq(expectedPath), eq(request), any(TypeReference.class)))
                    .willReturn(mockResponse);

            // when
            sut.changePassword(userId, request);

            // then
            then(httpClient).should().put(eq(expectedPath), eq(request), any(TypeReference.class));
        }
    }
}
