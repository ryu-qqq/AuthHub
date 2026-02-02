package com.ryuqq.authhub.sdk.client.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.sdk.api.InternalApi;
import com.ryuqq.authhub.sdk.client.GatewayClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DefaultInternalApi")
class DefaultInternalApiTest {

    @Nested
    @DisplayName("getPermissionSpec 메서드")
    class GetPermissionSpec {

        @Test
        @DisplayName("InternalApi 인스턴스가 생성된다")
        void shouldCreateInternalApiInstance() {
            // given
            GatewayClient client =
                    GatewayClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceName("gateway")
                            .serviceToken("test-token")
                            .build();

            // when
            InternalApi internalApi = client.internal();

            // then
            assertThat(internalApi).isNotNull();
            assertThat(internalApi).isInstanceOf(DefaultInternalApi.class);
        }
    }
}
