package com.ryuqq.authhub.sdk.model.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Common Model Tests")
class CommonModelTest {

    @Nested
    @DisplayName("ApiResponse")
    class ApiResponseTest {

        @Test
        @DisplayName("성공 응답 생성")
        void shouldCreateSuccessResponse() {
            LocalDateTime now = LocalDateTime.now();
            ApiResponse<String> response = new ApiResponse<>(true, "test-data", now, "req-123");

            assertThat(response.success()).isTrue();
            assertThat(response.data()).isEqualTo("test-data");
            assertThat(response.timestamp()).isEqualTo(now);
            assertThat(response.requestId()).isEqualTo("req-123");
        }

        @Test
        @DisplayName("실패 응답 생성")
        void shouldCreateFailureResponse() {
            LocalDateTime now = LocalDateTime.now();
            ApiResponse<String> response = new ApiResponse<>(false, null, now, "req-456");

            assertThat(response.success()).isFalse();
            assertThat(response.data()).isNull();
            assertThat(response.requestId()).isEqualTo("req-456");
        }

        @Test
        @DisplayName("제네릭 타입 지원")
        void shouldSupportGenericTypes() {
            LocalDateTime now = LocalDateTime.now();
            List<String> data = List.of("item1", "item2");
            ApiResponse<List<String>> response = new ApiResponse<>(true, data, now, "req-789");

            assertThat(response.data()).hasSize(2);
            assertThat(response.data()).containsExactly("item1", "item2");
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            LocalDateTime now = LocalDateTime.now();
            ApiResponse<String> response1 = new ApiResponse<>(true, "data", now, "req-1");
            ApiResponse<String> response2 = new ApiResponse<>(true, "data", now, "req-1");

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }
}
