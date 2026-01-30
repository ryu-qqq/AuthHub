package com.ryuqq.authhub.sdk.sync;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("EndpointSyncRequest")
class EndpointSyncRequestTest {

    @Nested
    @DisplayName("of")
    class Of {

        @Test
        @DisplayName("서비스명과 엔드포인트 목록으로 생성")
        void createsWithServiceNameAndEndpoints() {
            List<EndpointInfo> endpoints =
                    List.of(
                            EndpointInfo.of("GET", "/api/v1/users", "user:read", ""),
                            EndpointInfo.of("POST", "/api/v1/users", "user:create", ""));

            EndpointSyncRequest request = EndpointSyncRequest.of("authhub", endpoints);

            assertThat(request.serviceName()).isEqualTo("authhub");
            assertThat(request.endpoints()).hasSize(2);
            assertThat(request.endpoints()).containsExactlyInAnyOrderElementsOf(endpoints);
        }

        @Test
        @DisplayName("빈 엔드포인트 목록 허용")
        void allowsEmptyEndpoints() {
            EndpointSyncRequest request = EndpointSyncRequest.of("my-service", List.of());

            assertThat(request.serviceName()).isEqualTo("my-service");
            assertThat(request.endpoints()).isEmpty();
        }
    }
}
