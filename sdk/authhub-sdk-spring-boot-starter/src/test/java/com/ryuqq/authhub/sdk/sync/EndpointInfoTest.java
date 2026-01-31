package com.ryuqq.authhub.sdk.sync;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("EndpointInfo")
class EndpointInfoTest {

    @Nested
    @DisplayName("of")
    class Of {

        @Test
        @DisplayName("모든 필드로 EndpointInfo 생성")
        void createsEndpointInfoWithAllFields() {
            EndpointInfo info = EndpointInfo.of("GET", "/api/v1/users/{id}", "user:read", "사용자 조회");

            assertThat(info.httpMethod()).isEqualTo("GET");
            assertThat(info.pathPattern()).isEqualTo("/api/v1/users/{id}");
            assertThat(info.permissionKey()).isEqualTo("user:read");
            assertThat(info.description()).isEqualTo("사용자 조회");
        }

        @Test
        @DisplayName("빈 description 허용")
        void allowsEmptyDescription() {
            EndpointInfo info = EndpointInfo.of("POST", "/api/v1/users", "user:create", "");

            assertThat(info.description()).isEmpty();
        }
    }

    @Nested
    @DisplayName("record 동작")
    class RecordBehavior {

        @Test
        @DisplayName("동일한 값이면 equals true")
        void equalsWhenSameValues() {
            EndpointInfo a = EndpointInfo.of("GET", "/api/v1/users", "user:read", "desc");
            EndpointInfo b = EndpointInfo.of("GET", "/api/v1/users", "user:read", "desc");

            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 equals false")
        void notEqualsWhenDifferentValues() {
            EndpointInfo a = EndpointInfo.of("GET", "/api/v1/users", "user:read", "desc");
            EndpointInfo b = EndpointInfo.of("POST", "/api/v1/users", "user:create", "desc");

            assertThat(a).isNotEqualTo(b);
        }
    }
}
