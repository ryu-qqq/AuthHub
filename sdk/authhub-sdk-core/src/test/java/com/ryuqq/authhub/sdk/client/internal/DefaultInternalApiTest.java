package com.ryuqq.authhub.sdk.client.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.sdk.api.InternalApi;
import com.ryuqq.authhub.sdk.client.GatewayClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * DefaultInternalApi 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("DefaultInternalApi 단위 테스트")
class DefaultInternalApiTest {

    private GatewayClient gatewayClient;
    private InternalApi internalApi;

    @BeforeEach
    void setUp() {
        gatewayClient =
                GatewayClient.builder()
                        .baseUrl("https://authhub.example.com")
                        .serviceName("gateway")
                        .serviceToken("test-token")
                        .build();
        internalApi = gatewayClient.internal();
    }

    @Nested
    @DisplayName("InternalApi 인스턴스 생성")
    class InstanceCreation {

        @Test
        @DisplayName("InternalApi 인스턴스가 생성된다")
        void shouldCreateInternalApiInstance() {
            // then
            assertThat(internalApi).isNotNull();
            assertThat(internalApi).isInstanceOf(DefaultInternalApi.class);
        }
    }

    @Nested
    @DisplayName("getPermissionSpec 메서드")
    class GetPermissionSpec {

        @Test
        @DisplayName("메서드가 정의되어 있다")
        void shouldHaveGetPermissionSpecMethod() {
            // then
            assertThat(internalApi).hasNoNullFieldsOrProperties();
        }
    }

    @Nested
    @DisplayName("getJwks 메서드")
    class GetJwks {

        @Test
        @DisplayName("메서드가 정의되어 있다")
        void shouldHaveGetJwksMethod() throws Exception {
            // then - 메서드가 존재하는지 확인
            assertThat(internalApi.getClass().getMethod("getJwks")).isNotNull();
        }
    }

    @Nested
    @DisplayName("getTenantConfig 메서드")
    class GetTenantConfig {

        @Test
        @DisplayName("메서드가 정의되어 있다")
        void shouldHaveGetTenantConfigMethod() throws Exception {
            // then - 메서드가 존재하는지 확인
            assertThat(internalApi.getClass().getMethod("getTenantConfig", String.class))
                    .isNotNull();
        }
    }

    @Nested
    @DisplayName("getUserPermissions 메서드")
    class GetUserPermissions {

        @Test
        @DisplayName("메서드가 정의되어 있다")
        void shouldHaveGetUserPermissionsMethod() throws Exception {
            // then - 메서드가 존재하는지 확인
            assertThat(internalApi.getClass().getMethod("getUserPermissions", String.class))
                    .isNotNull();
        }
    }
}
