package com.ryuqq.authhub.adapter.in.rest.common.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SliceApiResponse 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SliceApiResponse 단위 테스트")
class SliceApiResponseTest {

    @Nested
    @DisplayName("of() 팩토리 메서드는")
    class OfMethod {

        @Test
        @DisplayName("content, size, hasNext, nextCursor로 SliceApiResponse를 생성한다")
        void shouldCreateSliceApiResponse() {
            // Given
            List<String> content = List.of("a", "b", "c");
            int size = 3;
            boolean hasNext = true;
            String nextCursor = "cursor-123";

            // When
            SliceApiResponse<String> result =
                    SliceApiResponse.of(content, size, hasNext, nextCursor);

            // Then
            assertThat(result.content()).containsExactly("a", "b", "c");
            assertThat(result.size()).isEqualTo(3);
            assertThat(result.hasNext()).isTrue();
            assertThat(result.nextCursor()).isEqualTo("cursor-123");
        }

        @Test
        @DisplayName("null content는 빈 리스트로 방어적 복사한다")
        void shouldDefensivelyCopyNullContent() {
            // When
            SliceApiResponse<String> result = SliceApiResponse.of(null, 0, false, null);

            // Then
            assertThat(result.content()).isEmpty();
        }

        @Test
        @DisplayName("content는 불변 리스트로 반환된다")
        void shouldReturnImmutableContent() {
            // Given
            List<String> mutableContent = new java.util.ArrayList<>(List.of("a"));

            // When
            SliceApiResponse<String> result = SliceApiResponse.of(mutableContent, 1, false, null);

            // Then
            assertThat(result.content()).containsExactly("a");
            mutableContent.add("b");
            assertThat(result.content()).containsExactly("a");
        }
    }
}
