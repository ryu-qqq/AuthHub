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

    @Nested
    @DisplayName("PageResponse")
    class PageResponseTest {

        @Test
        @DisplayName("페이지 응답 생성")
        void shouldCreatePageResponse() {
            List<String> content = List.of("item1", "item2", "item3");
            PageResponse<String> response = new PageResponse<>(content, 0, 10, 100, 10, true);

            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(100);
            assertThat(response.totalPages()).isEqualTo(10);
            assertThat(response.hasNext()).isTrue();
        }

        @Test
        @DisplayName("빈 페이지 응답 생성")
        void shouldCreateEmptyPageResponse() {
            PageResponse<String> response = new PageResponse<>(List.of(), 0, 10, 0, 0, false);

            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
            assertThat(response.totalPages()).isZero();
            assertThat(response.hasNext()).isFalse();
        }

        @Test
        @DisplayName("마지막 페이지 응답 생성")
        void shouldCreateLastPageResponse() {
            List<String> content = List.of("item1", "item2");
            PageResponse<String> response = new PageResponse<>(content, 9, 10, 92, 10, false);

            assertThat(response.page()).isEqualTo(9);
            assertThat(response.hasNext()).isFalse();
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            List<String> content = List.of("item1");
            PageResponse<String> response1 = new PageResponse<>(content, 0, 10, 1, 1, false);
            PageResponse<String> response2 = new PageResponse<>(content, 0, 10, 1, 1, false);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }

    @Nested
    @DisplayName("SliceResponse")
    class SliceResponseTest {

        @Test
        @DisplayName("슬라이스 응답 생성")
        void shouldCreateSliceResponse() {
            List<String> content = List.of("item1", "item2", "item3");
            SliceResponse<String> response = new SliceResponse<>(content, 10, true, "cursor-abc");

            assertThat(response.content()).hasSize(3);
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("cursor-abc");
        }

        @Test
        @DisplayName("마지막 슬라이스 응답 생성")
        void shouldCreateLastSliceResponse() {
            List<String> content = List.of("item1");
            SliceResponse<String> response = new SliceResponse<>(content, 10, false, null);

            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }

        @Test
        @DisplayName("빈 슬라이스 응답 생성")
        void shouldCreateEmptySliceResponse() {
            SliceResponse<String> response = new SliceResponse<>(List.of(), 10, false, null);

            assertThat(response.content()).isEmpty();
            assertThat(response.hasNext()).isFalse();
        }

        @Test
        @DisplayName("equals와 hashCode가 올바르게 동작")
        void shouldImplementEqualsAndHashCode() {
            List<String> content = List.of("item1");
            SliceResponse<String> response1 = new SliceResponse<>(content, 10, false, null);
            SliceResponse<String> response2 = new SliceResponse<>(content, 10, false, null);

            assertThat(response1).isEqualTo(response2);
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }
    }
}
