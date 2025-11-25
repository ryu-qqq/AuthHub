package com.ryuqq.authhub.application.common.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PageResponse 테스트")
class PageResponseTest {

    @Nested
    @DisplayName("팩토리 메서드 테스트")
    class FactoryMethodTests {

        @Test
        @DisplayName("of - 모든 파라미터로 PageResponse 생성 성공")
        void of_allParameters_success() {
            // Given
            List<String> content = List.of("item1", "item2", "item3");
            int page = 0;
            int size = 3;
            long totalElements = 10L;
            int totalPages = 4;
            boolean first = true;
            boolean last = false;

            // When
            PageResponse<String> response =
                    PageResponse.of(content, page, size, totalElements, totalPages, first, last);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEqualTo(content);
            assertThat(response.page()).isEqualTo(page);
            assertThat(response.size()).isEqualTo(size);
            assertThat(response.totalElements()).isEqualTo(totalElements);
            assertThat(response.totalPages()).isEqualTo(totalPages);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isFalse();
        }

        @Test
        @DisplayName("empty - 빈 PageResponse 생성 성공")
        void empty_success() {
            // Given
            int page = 0;
            int size = 10;

            // When
            PageResponse<String> response = PageResponse.empty(page, size);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.content()).isEmpty();
            assertThat(response.page()).isEqualTo(page);
            assertThat(response.size()).isEqualTo(size);
            assertThat(response.totalElements()).isZero();
            assertThat(response.totalPages()).isZero();
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }
    }

    @Nested
    @DisplayName("다양한 타입 테스트")
    class GenericTypeTests {

        @Test
        @DisplayName("Integer 타입 PageResponse 생성")
        void integerType_success() {
            // Given
            List<Integer> content = List.of(1, 2, 3, 4, 5);

            // When
            PageResponse<Integer> response = PageResponse.of(content, 0, 5, 25L, 5, true, false);

            // Then
            assertThat(response.content()).containsExactly(1, 2, 3, 4, 5);
            assertThat(response.totalElements()).isEqualTo(25L);
            assertThat(response.totalPages()).isEqualTo(5);
        }

        @Test
        @DisplayName("커스텀 객체 타입 PageResponse 생성")
        void customObjectType_success() {
            // Given
            record TestDto(String name, int value) {}
            List<TestDto> content = List.of(new TestDto("test1", 100), new TestDto("test2", 200));

            // When
            PageResponse<TestDto> response = PageResponse.of(content, 1, 2, 6L, 3, false, false);

            // Then
            assertThat(response.content()).hasSize(2);
            assertThat(response.content().get(0).name()).isEqualTo("test1");
            assertThat(response.content().get(0).value()).isEqualTo(100);
            assertThat(response.content().get(1).name()).isEqualTo("test2");
            assertThat(response.content().get(1).value()).isEqualTo(200);
            assertThat(response.page()).isEqualTo(1);
            assertThat(response.totalElements()).isEqualTo(6L);
        }
    }

    @Nested
    @DisplayName("페이징 시나리오 테스트")
    class PagingScenarioTests {

        @Test
        @DisplayName("첫 번째 페이지 - first=true, last=false")
        void firstPage_scenario() {
            // Given
            List<String> content = List.of("item1", "item2", "item3");

            // When
            PageResponse<String> response = PageResponse.of(content, 0, 3, 10L, 4, true, false);

            // Then
            assertThat(response.page()).isZero();
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isFalse();
            assertThat(response.totalPages()).isEqualTo(4);
        }

        @Test
        @DisplayName("중간 페이지 - first=false, last=false")
        void middlePage_scenario() {
            // Given
            List<String> content = List.of("item4", "item5", "item6");

            // When
            PageResponse<String> response = PageResponse.of(content, 1, 3, 10L, 4, false, false);

            // Then
            assertThat(response.page()).isEqualTo(1);
            assertThat(response.first()).isFalse();
            assertThat(response.last()).isFalse();
            assertThat(response.content()).hasSize(3);
        }

        @Test
        @DisplayName("마지막 페이지 - first=false, last=true")
        void lastPage_scenario() {
            // Given
            List<String> content = List.of("item10");

            // When
            PageResponse<String> response = PageResponse.of(content, 3, 3, 10L, 4, false, true);

            // Then
            assertThat(response.page()).isEqualTo(3);
            assertThat(response.first()).isFalse();
            assertThat(response.last()).isTrue();
            assertThat(response.content()).hasSize(1);
        }

        @Test
        @DisplayName("단일 페이지 - first=true, last=true")
        void singlePage_scenario() {
            // Given
            List<String> content = List.of("item1", "item2");

            // When
            PageResponse<String> response = PageResponse.of(content, 0, 10, 2L, 1, true, true);

            // Then
            assertThat(response.page()).isZero();
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.totalElements()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("경계값 테스트")
    class BoundaryValueTests {

        @Test
        @DisplayName("빈 리스트로 PageResponse 생성")
        void emptyList_success() {
            // Given
            List<String> emptyContent = List.of();

            // When
            PageResponse<String> response = PageResponse.of(emptyContent, 0, 10, 0L, 0, true, true);

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
            assertThat(response.totalPages()).isZero();
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("page 0으로 PageResponse 생성")
        void pageZero_success() {
            // Given
            List<String> content = List.of("item1");

            // When
            PageResponse<String> response = PageResponse.of(content, 0, 1, 5L, 5, true, false);

            // Then
            assertThat(response.page()).isZero();
            assertThat(response.first()).isTrue();
        }

        @Test
        @DisplayName("큰 totalElements 값으로 PageResponse 생성")
        void largeTotalElements_success() {
            // Given
            List<String> content = List.of("item1", "item2");
            long largeTotalElements = 1_000_000L;

            // When
            PageResponse<String> response =
                    PageResponse.of(content, 0, 2, largeTotalElements, 500_000, true, false);

            // Then
            assertThat(response.totalElements()).isEqualTo(largeTotalElements);
            assertThat(response.totalPages()).isEqualTo(500_000);
        }

        @Test
        @DisplayName("size 1로 PageResponse 생성")
        void sizeOne_success() {
            // Given
            List<String> content = List.of("single-item");

            // When
            PageResponse<String> response = PageResponse.of(content, 2, 1, 5L, 5, false, false);

            // Then
            assertThat(response.size()).isEqualTo(1);
            assertThat(response.content()).hasSize(1);
            assertThat(response.page()).isEqualTo(2);
        }

        @Test
        @DisplayName("totalPages 1로 PageResponse 생성")
        void totalPagesOne_success() {
            // Given
            List<String> content = List.of("item1", "item2", "item3");

            // When
            PageResponse<String> response = PageResponse.of(content, 0, 10, 3L, 1, true, true);

            // Then
            assertThat(response.totalPages()).isEqualTo(1);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }
    }

    @Nested
    @DisplayName("Record 메서드 테스트")
    class RecordMethodTests {

        @Test
        @DisplayName("equals - 동일한 값들로 생성된 PageResponse는 같음")
        void equals_sameValues_returnsTrue() {
            // Given
            List<String> content = List.of("item1", "item2");
            PageResponse<String> response1 = PageResponse.of(content, 0, 2, 10L, 5, true, false);
            PageResponse<String> response2 = PageResponse.of(content, 0, 2, 10L, 5, true, false);

            // When & Then
            assertThat(response1).isEqualTo(response2);
        }

        @Test
        @DisplayName("equals - 다른 값들로 생성된 PageResponse는 다름")
        void equals_differentValues_returnsFalse() {
            // Given
            List<String> content1 = List.of("item1");
            List<String> content2 = List.of("item2");
            PageResponse<String> response1 = PageResponse.of(content1, 0, 1, 5L, 5, true, false);
            PageResponse<String> response2 = PageResponse.of(content2, 1, 1, 5L, 5, false, false);

            // When & Then
            assertThat(response1).isNotEqualTo(response2);
        }

        @Test
        @DisplayName("hashCode - 동일한 값들로 생성된 PageResponse는 같은 해시코드")
        void hashCode_sameValues_sameHashCode() {
            // Given
            List<String> content = List.of("item1", "item2");
            PageResponse<String> response1 = PageResponse.of(content, 1, 2, 10L, 5, false, false);
            PageResponse<String> response2 = PageResponse.of(content, 1, 2, 10L, 5, false, false);

            // When & Then
            assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
        }

        @Test
        @DisplayName("toString - 모든 필드 포함한 문자열 반환")
        void toString_containsAllFields() {
            // Given
            List<String> content = List.of("item1", "item2");
            PageResponse<String> response = PageResponse.of(content, 1, 2, 10L, 5, false, false);

            // When
            String result = response.toString();

            // Then
            assertThat(result).contains("PageResponse");
            assertThat(result).contains("content=");
            assertThat(result).contains("page=1");
            assertThat(result).contains("size=2");
            assertThat(result).contains("totalElements=10");
            assertThat(result).contains("totalPages=5");
            assertThat(result).contains("first=false");
            assertThat(result).contains("last=false");
        }
    }

    @Nested
    @DisplayName("실제 사용 시나리오 테스트")
    class UsageScenarioTests {

        @Test
        @DisplayName("게시판 페이징 - 첫 페이지")
        void boardPaging_firstPage() {
            // Given
            List<String> posts = List.of("post1", "post2", "post3", "post4", "post5");

            // When
            PageResponse<String> response = PageResponse.of(posts, 0, 5, 23L, 5, true, false);

            // Then
            assertThat(response.content()).hasSize(5);
            assertThat(response.page()).isZero();
            assertThat(response.totalElements()).isEqualTo(23L);
            assertThat(response.totalPages()).isEqualTo(5);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isFalse();
        }

        @Test
        @DisplayName("검색 결과 페이징 - 마지막 페이지")
        void searchResult_lastPage() {
            // Given
            List<String> searchResults = List.of("result1", "result2");

            // When
            PageResponse<String> response =
                    PageResponse.of(searchResults, 4, 5, 22L, 5, false, true);

            // Then
            assertThat(response.content()).hasSize(2);
            assertThat(response.page()).isEqualTo(4);
            assertThat(response.first()).isFalse();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("검색 결과 없음 - 빈 페이지")
        void noSearchResult_emptyPage() {
            // When
            PageResponse<String> response = PageResponse.empty(0, 10);

            // Then
            assertThat(response.content()).isEmpty();
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isZero();
            assertThat(response.totalPages()).isZero();
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("대용량 데이터 페이징 - 중간 페이지")
        void largeDataPaging_middlePage() {
            // Given
            List<String> data = List.of("data1", "data2", "data3");

            // When
            PageResponse<String> response =
                    PageResponse.of(data, 500, 3, 10_000L, 3334, false, false);

            // Then
            assertThat(response.page()).isEqualTo(500);
            assertThat(response.totalElements()).isEqualTo(10_000L);
            assertThat(response.totalPages()).isEqualTo(3334);
            assertThat(response.first()).isFalse();
            assertThat(response.last()).isFalse();
        }

        @Test
        @DisplayName("페이지 크기 변경 시나리오")
        void pageSizeChange_scenario() {
            // Given - 페이지 크기 10
            List<String> content10 = List.of("item1", "item2", "item3", "item4", "item5");
            PageResponse<String> response10 =
                    PageResponse.of(content10, 0, 10, 50L, 5, true, false);

            // Given - 페이지 크기 20
            List<String> content20 = List.of("item1", "item2", "item3", "item4", "item5");
            PageResponse<String> response20 =
                    PageResponse.of(content20, 0, 20, 50L, 3, true, false);

            // Then
            assertThat(response10.totalPages()).isEqualTo(5);
            assertThat(response20.totalPages()).isEqualTo(3);
            assertThat(response10.totalElements()).isEqualTo(response20.totalElements());
        }
    }
}
