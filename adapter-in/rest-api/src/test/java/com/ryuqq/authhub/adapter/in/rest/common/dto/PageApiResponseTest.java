package com.ryuqq.authhub.adapter.in.rest.common.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PageApiResponse DTO 테스트")
class PageApiResponseTest {

    @Nested
    @DisplayName("from(PageResponse) 테스트")
    class FromPageResponseTest {

        @Test
        @DisplayName("[from] PageResponse를 PageApiResponse로 변환 성공")
        void from_shouldConvertPageResponseToPageApiResponse() {
            // Given
            List<String> content = List.of("item1", "item2", "item3");
            PageResponse<String> pageResponse =
                    PageResponse.of(content, 0, 10, 100L, 10, true, false);

            // When
            PageApiResponse<String> result = PageApiResponse.from(pageResponse);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(3);
            assertThat(result.content()).containsExactly("item1", "item2", "item3");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(100L);
            assertThat(result.totalPages()).isEqualTo(10);
            assertThat(result.first()).isTrue();
            assertThat(result.last()).isFalse();
        }

        @Test
        @DisplayName("[from] 빈 PageResponse 변환 성공")
        void from_shouldConvertEmptyPageResponse() {
            // Given
            PageResponse<String> emptyPageResponse = PageResponse.empty(0, 10);

            // When
            PageApiResponse<String> result = PageApiResponse.from(emptyPageResponse);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isZero();
            assertThat(result.totalPages()).isZero();
            assertThat(result.first()).isTrue();
            assertThat(result.last()).isTrue();
        }

        @Test
        @DisplayName("[from] 마지막 페이지 올바르게 변환")
        void from_shouldHandleLastPage() {
            // Given
            List<Integer> content = List.of(1, 2);
            PageResponse<Integer> pageResponse =
                    PageResponse.of(content, 9, 10, 92L, 10, false, true);

            // When
            PageApiResponse<Integer> result = PageApiResponse.from(pageResponse);

            // Then
            assertThat(result.page()).isEqualTo(9);
            assertThat(result.first()).isFalse();
            assertThat(result.last()).isTrue();
        }
    }

    @Nested
    @DisplayName("from(PageResponse, Function) 테스트")
    class FromPageResponseWithMapperTest {

        @Test
        @DisplayName("[from] 매퍼 함수를 적용하여 PageApiResponse 변환 성공")
        void from_shouldConvertWithMapperFunction() {
            // Given
            List<Integer> content = List.of(1, 2, 3);
            PageResponse<Integer> pageResponse =
                    PageResponse.of(content, 0, 10, 30L, 3, true, false);

            // When
            PageApiResponse<String> result =
                    PageApiResponse.from(pageResponse, num -> "item" + num);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(3);
            assertThat(result.content()).containsExactly("item1", "item2", "item3");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(30L);
            assertThat(result.totalPages()).isEqualTo(3);
        }

        @Test
        @DisplayName("[from] 빈 PageResponse에 매퍼 적용 시 빈 결과 반환")
        void from_shouldHandleEmptyPageWithMapper() {
            // Given
            PageResponse<Integer> emptyPageResponse = PageResponse.empty(0, 5);

            // When
            PageApiResponse<String> result =
                    PageApiResponse.from(emptyPageResponse, num -> "item" + num);

            // Then
            assertThat(result.content()).isEmpty();
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(5);
            assertThat(result.totalElements()).isZero();
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
            PageResponse<String> pageResponse = PageResponse.of(content, 0, 10, 2L, 1, true, true);
            PageApiResponse<String> result = PageApiResponse.from(pageResponse);

            // When & Then
            assertThat(result.content()).isUnmodifiable();
        }
    }
}
