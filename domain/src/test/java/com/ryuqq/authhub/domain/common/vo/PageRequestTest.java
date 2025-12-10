package com.ryuqq.authhub.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PageRequest VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PageRequest 테스트")
class PageRequestTest {

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 페이지와 사이즈로 PageRequest를 생성한다")
        void shouldCreatePageRequestWithValidValues() {
            // given
            int page = 2;
            int size = 10;

            // when
            PageRequest pageRequest = new PageRequest(page, size);

            // then
            assertThat(pageRequest.page()).isEqualTo(2);
            assertThat(pageRequest.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("음수 페이지는 0으로 정규화된다")
        void shouldNormalizeNegativePageToZero() {
            // given
            int negativePage = -5;

            // when
            PageRequest pageRequest = new PageRequest(negativePage, 20);

            // then
            assertThat(pageRequest.page()).isZero();
        }

        @Test
        @DisplayName("0 이하의 사이즈는 기본값으로 정규화된다")
        void shouldNormalizeInvalidSizeToDefault() {
            // when
            PageRequest zeroSize = new PageRequest(0, 0);
            PageRequest negativeSize = new PageRequest(0, -10);

            // then
            assertThat(zeroSize.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
            assertThat(negativeSize.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("최대 사이즈를 초과하면 최대값으로 정규화된다")
        void shouldNormalizeExceedingSizeToMax() {
            // given
            int exceedingSize = 200;

            // when
            PageRequest pageRequest = new PageRequest(0, exceedingSize);

            // then
            assertThat(pageRequest.size()).isEqualTo(PageRequest.MAX_SIZE);
        }

        @Test
        @DisplayName("경계값 MAX_SIZE는 허용된다")
        void shouldAllowMaxSize() {
            // when
            PageRequest pageRequest = new PageRequest(0, PageRequest.MAX_SIZE);

            // then
            assertThat(pageRequest.size()).isEqualTo(PageRequest.MAX_SIZE);
        }
    }

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("정적 팩토리 메서드로 PageRequest를 생성한다")
        void shouldCreatePageRequestUsingOf() {
            // when
            PageRequest pageRequest = PageRequest.of(1, 30);

            // then
            assertThat(pageRequest.page()).isEqualTo(1);
            assertThat(pageRequest.size()).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("first 팩토리 메서드")
    class FirstTest {

        @Test
        @DisplayName("첫 페이지 요청을 생성한다")
        void shouldCreateFirstPageRequest() {
            // when
            PageRequest pageRequest = PageRequest.first(25);

            // then
            assertThat(pageRequest.page()).isZero();
            assertThat(pageRequest.size()).isEqualTo(25);
        }
    }

    @Nested
    @DisplayName("defaultPage 팩토리 메서드")
    class DefaultPageTest {

        @Test
        @DisplayName("기본 설정의 PageRequest를 생성한다")
        void shouldCreateDefaultPageRequest() {
            // when
            PageRequest pageRequest = PageRequest.defaultPage();

            // then
            assertThat(pageRequest.page()).isZero();
            assertThat(pageRequest.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }
    }

    @Nested
    @DisplayName("offset 메서드")
    class OffsetTest {

        @Test
        @DisplayName("오프셋을 올바르게 계산한다")
        void shouldCalculateOffset() {
            // given
            PageRequest pageRequest = PageRequest.of(3, 20);

            // when
            long offset = pageRequest.offset();

            // then
            assertThat(offset).isEqualTo(60L); // 3 * 20
        }

        @Test
        @DisplayName("첫 페이지의 오프셋은 0이다")
        void shouldReturnZeroOffsetForFirstPage() {
            // given
            PageRequest pageRequest = PageRequest.first(20);

            // when
            long offset = pageRequest.offset();

            // then
            assertThat(offset).isZero();
        }

        @Test
        @DisplayName("큰 페이지 번호에서도 오프셋이 오버플로우 없이 계산된다")
        void shouldCalculateOffsetWithoutOverflow() {
            // given - 큰 페이지 번호 테스트
            PageRequest pageRequest = PageRequest.of(Integer.MAX_VALUE / 100, 100);

            // when
            long offset = pageRequest.offset();

            // then
            assertThat(offset).isPositive();
        }
    }

    @Nested
    @DisplayName("next 메서드")
    class NextTest {

        @Test
        @DisplayName("다음 페이지 요청을 생성한다")
        void shouldCreateNextPageRequest() {
            // given
            PageRequest pageRequest = PageRequest.of(2, 20);

            // when
            PageRequest nextPage = pageRequest.next();

            // then
            assertThat(nextPage.page()).isEqualTo(3);
            assertThat(nextPage.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("previous 메서드")
    class PreviousTest {

        @Test
        @DisplayName("이전 페이지 요청을 생성한다")
        void shouldCreatePreviousPageRequest() {
            // given
            PageRequest pageRequest = PageRequest.of(3, 20);

            // when
            PageRequest previousPage = pageRequest.previous();

            // then
            assertThat(previousPage.page()).isEqualTo(2);
            assertThat(previousPage.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("첫 페이지에서 이전을 요청하면 그대로 반환한다")
        void shouldReturnSameWhenFirstPage() {
            // given
            PageRequest firstPage = PageRequest.first(20);

            // when
            PageRequest previousPage = firstPage.previous();

            // then
            assertThat(previousPage).isSameAs(firstPage);
            assertThat(previousPage.page()).isZero();
        }
    }

    @Nested
    @DisplayName("isFirst 메서드")
    class IsFirstTest {

        @Test
        @DisplayName("첫 페이지면 true를 반환한다")
        void shouldReturnTrueWhenFirstPage() {
            // given
            PageRequest pageRequest = PageRequest.first(20);

            // then
            assertThat(pageRequest.isFirst()).isTrue();
        }

        @Test
        @DisplayName("첫 페이지가 아니면 false를 반환한다")
        void shouldReturnFalseWhenNotFirstPage() {
            // given
            PageRequest pageRequest = PageRequest.of(1, 20);

            // then
            assertThat(pageRequest.isFirst()).isFalse();
        }
    }

    @Nested
    @DisplayName("totalPages 메서드")
    class TotalPagesTest {

        @Test
        @DisplayName("전체 페이지 수를 올바르게 계산한다")
        void shouldCalculateTotalPages() {
            // given
            PageRequest pageRequest = PageRequest.of(0, 20);

            // when & then
            assertThat(pageRequest.totalPages(100)).isEqualTo(5);
            assertThat(pageRequest.totalPages(101)).isEqualTo(6);
            assertThat(pageRequest.totalPages(99)).isEqualTo(5);
        }

        @Test
        @DisplayName("0개일 때 전체 페이지 수는 0이다")
        void shouldReturnZeroWhenNoElements() {
            // given
            PageRequest pageRequest = PageRequest.of(0, 20);

            // when
            int totalPages = pageRequest.totalPages(0);

            // then
            assertThat(totalPages).isZero();
        }

        @Test
        @DisplayName("1개일 때 전체 페이지 수는 1이다")
        void shouldReturnOneWhenSingleElement() {
            // given
            PageRequest pageRequest = PageRequest.of(0, 20);

            // when
            int totalPages = pageRequest.totalPages(1);

            // then
            assertThat(totalPages).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("isLast 메서드")
    class IsLastTest {

        @Test
        @DisplayName("마지막 페이지면 true를 반환한다")
        void shouldReturnTrueWhenLastPage() {
            // given
            PageRequest pageRequest = PageRequest.of(4, 20); // 마지막 페이지 (0-indexed, total 5 pages)

            // then
            assertThat(pageRequest.isLast(100)).isTrue();
        }

        @Test
        @DisplayName("마지막 페이지가 아니면 false를 반환한다")
        void shouldReturnFalseWhenNotLastPage() {
            // given
            PageRequest pageRequest = PageRequest.of(3, 20);

            // then
            assertThat(pageRequest.isLast(100)).isFalse();
        }

        @Test
        @DisplayName("마지막 페이지를 초과해도 true를 반환한다")
        void shouldReturnTrueWhenBeyondLastPage() {
            // given
            PageRequest pageRequest = PageRequest.of(10, 20); // 5페이지보다 큼

            // then
            assertThat(pageRequest.isLast(100)).isTrue();
        }

        @Test
        @DisplayName("데이터가 없어도 첫 페이지는 마지막 페이지다")
        void shouldReturnTrueForFirstPageWhenNoData() {
            // given
            PageRequest pageRequest = PageRequest.first(20);

            // then
            assertThat(pageRequest.isLast(0)).isTrue();
        }
    }

    @Nested
    @DisplayName("상수 검증")
    class ConstantsTest {

        @Test
        @DisplayName("기본 사이즈는 20이다")
        void shouldHaveDefaultSizeOf20() {
            assertThat(PageRequest.DEFAULT_SIZE).isEqualTo(20);
        }

        @Test
        @DisplayName("최대 사이즈는 100이다")
        void shouldHaveMaxSizeOf100() {
            assertThat(PageRequest.MAX_SIZE).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 페이지와 사이즈를 가진 PageRequest는 동일하다")
        void shouldBeEqualWhenSameValues() {
            // given
            PageRequest request1 = PageRequest.of(2, 20);
            PageRequest request2 = PageRequest.of(2, 20);

            // then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("다른 페이지를 가진 PageRequest는 다르다")
        void shouldNotBeEqualWhenDifferentPage() {
            // given
            PageRequest request1 = PageRequest.of(1, 20);
            PageRequest request2 = PageRequest.of(2, 20);

            // then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("다른 사이즈를 가진 PageRequest는 다르다")
        void shouldNotBeEqualWhenDifferentSize() {
            // given
            PageRequest request1 = PageRequest.of(1, 20);
            PageRequest request2 = PageRequest.of(1, 30);

            // then
            assertThat(request1).isNotEqualTo(request2);
        }
    }
}
