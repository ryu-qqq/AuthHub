package com.ryuqq.authhub.adapter.in.rest.common.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.common.dto.response.SliceResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SliceApiResponse DTO 테스트")
class SliceApiResponseTest {

    @Nested
    @DisplayName("from(SliceResponse) 테스트")
    class FromSliceResponseTest {

        @Test
        @DisplayName("[from] SliceResponse를 SliceApiResponse로 변환 성공")
        void from_shouldConvertSliceResponseToSliceApiResponse() {
            // Given
            List<String> content = List.of("item1", "item2", "item3");
            SliceResponse<String> sliceResponse = SliceResponse.of(content, 10, true, "cursor123");

            // When
            SliceApiResponse<String> result = SliceApiResponse.from(sliceResponse);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(3);
            assertThat(result.content()).containsExactly("item1", "item2", "item3");
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.hasNext()).isTrue();
            assertThat(result.nextCursor()).isEqualTo("cursor123");
        }

        @Test
        @DisplayName("[from] 빈 SliceResponse 변환 성공")
        void from_shouldConvertEmptySliceResponse() {
            // Given
            SliceResponse<String> emptySliceResponse = SliceResponse.empty(10);

            // When
            SliceApiResponse<String> result = SliceApiResponse.from(emptySliceResponse);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.hasNext()).isFalse();
            assertThat(result.nextCursor()).isNull();
        }

        @Test
        @DisplayName("[from] hasNext가 false인 경우 올바르게 변환")
        void from_shouldHandleHasNextFalse() {
            // Given
            List<Integer> content = List.of(1, 2);
            SliceResponse<Integer> sliceResponse = SliceResponse.of(content, 5, false);

            // When
            SliceApiResponse<Integer> result = SliceApiResponse.from(sliceResponse);

            // Then
            assertThat(result.hasNext()).isFalse();
            assertThat(result.nextCursor()).isNull();
        }
    }

    @Nested
    @DisplayName("from(SliceResponse, Function) 테스트")
    class FromSliceResponseWithMapperTest {

        @Test
        @DisplayName("[from] 매퍼 함수를 적용하여 SliceApiResponse 변환 성공")
        void from_shouldConvertWithMapperFunction() {
            // Given
            List<Integer> content = List.of(1, 2, 3);
            SliceResponse<Integer> sliceResponse = SliceResponse.of(content, 10, true, "cursor");

            // When
            SliceApiResponse<String> result =
                    SliceApiResponse.from(sliceResponse, num -> "item" + num);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(3);
            assertThat(result.content()).containsExactly("item1", "item2", "item3");
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.hasNext()).isTrue();
            assertThat(result.nextCursor()).isEqualTo("cursor");
        }

        @Test
        @DisplayName("[from] 빈 SliceResponse에 매퍼 적용 시 빈 결과 반환")
        void from_shouldHandleEmptySliceWithMapper() {
            // Given
            SliceResponse<Integer> emptySliceResponse = SliceResponse.empty(5);

            // When
            SliceApiResponse<String> result =
                    SliceApiResponse.from(emptySliceResponse, num -> "item" + num);

            // Then
            assertThat(result.content()).isEmpty();
            assertThat(result.size()).isEqualTo(5);
            assertThat(result.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("Immutability 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("[content] 반환된 content는 불변 리스트")
        void content_shouldBeImmutable() {
            // Given
            List<String> content = List.of("item1", "item2");
            SliceResponse<String> sliceResponse = SliceResponse.of(content, 10, false);
            SliceApiResponse<String> result = SliceApiResponse.from(sliceResponse);

            // When & Then
            assertThat(result.content()).isUnmodifiable();
        }
    }
}
