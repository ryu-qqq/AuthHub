package com.ryuqq.authhub.application.common.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SliceResponse 테스트")
class SliceResponseTest {

    @Nested
    @DisplayName("팩토리 메서드 테스트")
    class FactoryMethodTests {

        @Test
        @DisplayName("of - 커서 있는 SliceResponse 생성 성공")
        void of_withCursor_success() {
            // Given
            List<String> content = List.of("item1", "item2", "item3");
            int size = 3;
            boolean hasNext = true;
            String nextCursor = "cursor-123";

            // When
            SliceResponse<String> response = SliceResponse.of(content, size, hasNext, nextCursor);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEqualTo(content);
            assertThat(response.size()).isEqualTo(size);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo(nextCursor);
        }

        @Test
        @DisplayName("of - 커서 없는 SliceResponse 생성 성공")
        void of_withoutCursor_success() {
            // Given
            List<String> content = List.of("item1", "item2");
            int size = 2;
            boolean hasNext = false;

            // When
            SliceResponse<String> response = SliceResponse.of(content, size, hasNext);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEqualTo(content);
            assertThat(response.size()).isEqualTo(size);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }

        @Test
        @DisplayName("empty - 빈 SliceResponse 생성 성공")
        void empty_success() {
            // Given
            int size = 10;

            // When
            SliceResponse<String> response = SliceResponse.empty(size);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.size()).isEqualTo(size);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }
    }

    @Nested
    @DisplayName("다양한 타입 테스트")
    class GenericTypeTests {

        @Test
        @DisplayName("Integer 타입 SliceResponse 생성")
        void integerType_success() {
            // Given
            List<Integer> content = List.of(1, 2, 3, 4, 5);
            int size = 5;
            boolean hasNext = true;
            String nextCursor = "int-cursor";

            // When
            SliceResponse<Integer> response = SliceResponse.of(content, size, hasNext, nextCursor);

            // Then
            assertThat(response.content()).containsExactly(1, 2, 3, 4, 5);
            assertThat(response.size()).isEqualTo(5);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isEqualTo("int-cursor");
        }

        @Test
        @DisplayName("커스텀 객체 타입 SliceResponse 생성")
        void customObjectType_success() {
            // Given
            record TestDto(String name, int value) {}
            List<TestDto> content = List.of(new TestDto("test1", 100), new TestDto("test2", 200));

            // When
            SliceResponse<TestDto> response = SliceResponse.of(content, 2, false);

            // Then
            assertThat(response.content()).hasSize(2);
            assertThat(response.content().get(0).name()).isEqualTo("test1");
            assertThat(response.content().get(0).value()).isEqualTo(100);
            assertThat(response.content().get(1).name()).isEqualTo("test2");
            assertThat(response.content().get(1).value()).isEqualTo(200);
            assertThat(response.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryValueTests {

        @Test
        @DisplayName("빈 리스트로 SliceResponse 생성")
        void emptyList_success() {
            // Given
            List<String> emptyContent = List.of();

            // When
            SliceResponse<String> response = SliceResponse.of(emptyContent, 0, false);

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.size()).isZero();
            assertThat(response.hasNext()).isFalse();
        }

        @Test
        @DisplayName("size 0으로 SliceResponse 생성")
        void sizeZero_success() {
            // Given
            List<String> content = List.of();

            // When
            SliceResponse<String> response = SliceResponse.of(content, 0, false, "cursor");

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.size()).isZero();
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isEqualTo("cursor");
        }

        @Test
        @DisplayName("큰 size 값으로 SliceResponse 생성")
        void largeSize_success() {
            // Given
            List<String> content = List.of("item1");
            int largeSize = 1000;

            // When
            SliceResponse<String> response = SliceResponse.of(content, largeSize, true);

            // Then
            assertThat(response.content()).hasSize(1);
            assertThat(response.size()).isEqualTo(largeSize);
            assertThat(response.hasNext()).isTrue();
        }

        @Test
        @DisplayName("null cursor로 SliceResponse 생성")
        void nullCursor_success() {
            // Given
            List<String> content = List.of("item1", "item2");

            // When
            SliceResponse<String> response = SliceResponse.of(content, 2, true, null);

            // Then
            assertThat(response.content()).hasSize(2);
            assertThat(response.nextCursor()).isNull();
            assertThat(response.hasNext()).isTrue();
        }

        @Test
        @DisplayName("빈 문자열 cursor로 SliceResponse 생성")
        void emptyCursor_success() {
            // Given
            List<String> content = List.of("item1");
            String emptyCursor = "";

            // When
            SliceResponse<String> response = SliceResponse.of(content, 1, false, emptyCursor);

            // Then
            assertThat(response.nextCursor()).isEmpty();
            assertThat(response.hasNext()).isFalse();
        }
    }

    @Nested
    @DisplayName("Record 메서드 테스트")
    class RecordMethodTests {

        @Test
        @DisplayName("equals - 동일한 값들로 생성된 SliceResponse는 같음")
        void equals_sameValues_returnsTrue() {
            // Given
            List<String> content = List.of("item1", "item2");
            SliceResponse<String> response1 = SliceResponse.of(content, 2, true, "cursor");
            SliceResponse<String> response2 = SliceResponse.of(content, 2, true, "cursor");

            // When & Then
            assertThat(response1).isEqualTo(response2);
        }

        @Test
        @DisplayName("equals - 다른 값들로 생성된 SliceResponse는 다름")
        void equals_differentValues_returnsFalse() {
            // Given
            List<String> content1 = List.of("item1");
            List<String> content2 = List.of("item2");
            SliceResponse<String> response1 = SliceResponse.of(content1, 1, true, "cursor1");
            SliceResponse<String> response2 = SliceResponse.of(content2, 1, true, "cursor2");

            // When & Then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("hashCode - 동일한 값들로 생성된 SliceResponse는 같은 해시코드")
        void hashCode_sameValues_sameHashCode() {
            // Given
            List<String> content = List.of("item1", "item2");
            SliceResponse<String> response1 = SliceResponse.of(content, 2, true, "cursor");
            SliceResponse<String> response2 = SliceResponse.of(content, 2, true, "cursor");

            // When & Then
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("toString - 모든 필드 포함한 문자열 반환")
        void toString_containsAllFields() {
            // Given
            List<String> content = List.of("item1", "item2");
            SliceResponse<String> response = SliceResponse.of(content, 2, true, "test-cursor");

            // When
            String result = response.toString();

            // Then
            assertThat(result).contains("SliceResponse");
            assertThat(result).contains("content=");
            assertThat(result).contains("size=2");
            assertThat(result).contains("hasNext=true");
            assertThat(result).contains("nextCursor=test-cursor");
        }
    }

    @Nested
    @DisplayName("실제 사용 시나리오 테스트")
    class UsageScenarioTests {

        @Test
        @DisplayName("첫 번째 페이지 - 다음 페이지 있음")
        void firstPage_hasNext() {
            // Given
            List<String> firstPageContent = List.of("item1", "item2", "item3");

            // When
            SliceResponse<String> response =
                    SliceResponse.of(firstPageContent, 3, true, "page2-cursor");

            // Then
            assertThat(response.content()).hasSize(3);
            assertThat(response.hasNext()).isTrue();
            assertThat(response.nextCursor()).isNotNull();
        }

        @Test
        @DisplayName("마지막 페이지 - 다음 페이지 없음")
        void lastPage_noNext() {
            // Given
            List<String> lastPageContent = List.of("item8", "item9");

            // When
            SliceResponse<String> response = SliceResponse.of(lastPageContent, 2, false);

            // Then
            assertThat(response.content()).hasSize(2);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }

        @Test
        @DisplayName("데이터 없음 - 빈 결과")
        void noData_emptyResult() {
            // When
            SliceResponse<String> response = SliceResponse.empty(10);

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.hasNext()).isFalse();
            assertThat(response.nextCursor()).isNull();
        }

        @Test
        @DisplayName("무한 스크롤 시나리오 - 연속된 페이지")
        void infiniteScroll_continuousPages() {
            // Given - 첫 번째 페이지
            List<String> page1Content = List.of("item1", "item2", "item3");
            SliceResponse<String> page1 = SliceResponse.of(page1Content, 3, true, "cursor-page2");

            // Given - 두 번째 페이지
            List<String> page2Content = List.of("item4", "item5", "item6");
            SliceResponse<String> page2 = SliceResponse.of(page2Content, 3, true, "cursor-page3");

            // Given - 마지막 페이지
            List<String> page3Content = List.of("item7");
            SliceResponse<String> page3 = SliceResponse.of(page3Content, 1, false);

            // Then
            assertThat(page1.hasNext()).isTrue();
            assertThat(page1.nextCursor()).isEqualTo("cursor-page2");

            assertThat(page2.hasNext()).isTrue();
            assertThat(page2.nextCursor()).isEqualTo("cursor-page3");

            assertThat(page3.hasNext()).isFalse();
            assertThat(page3.nextCursor()).isNull();
        }
    }
}
