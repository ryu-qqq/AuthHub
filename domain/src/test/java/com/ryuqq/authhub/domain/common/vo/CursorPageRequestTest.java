package com.ryuqq.authhub.domain.common.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CursorPageRequest VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CursorPageRequest 테스트")
class CursorPageRequestTest {

    @Nested
    @DisplayName("생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("유효한 커서와 사이즈로 CursorPageRequest를 생성한다")
        void shouldCreateCursorPageRequestWithValidValues() {
            // given
            String cursor = "abc123";
            int size = 10;

            // when
            CursorPageRequest request = new CursorPageRequest(cursor, size);

            // then
            assertThat(request.cursor()).isEqualTo("abc123");
            assertThat(request.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("null 커서로 생성할 수 있다")
        void shouldCreateWithNullCursor() {
            // when
            CursorPageRequest request = new CursorPageRequest(null, 20);

            // then
            assertThat(request.cursor()).isNull();
            assertThat(request.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("빈 문자열 커서는 null로 정규화된다")
        void shouldNormalizeEmptyCursorToNull() {
            // when
            CursorPageRequest emptyRequest = new CursorPageRequest("", 20);
            CursorPageRequest blankRequest = new CursorPageRequest("   ", 20);

            // then
            assertThat(emptyRequest.cursor()).isNull();
            assertThat(blankRequest.cursor()).isNull();
        }

        @Test
        @DisplayName("0 이하의 사이즈는 기본값으로 정규화된다")
        void shouldNormalizeInvalidSizeToDefault() {
            // when
            CursorPageRequest zeroSize = new CursorPageRequest("cursor", 0);
            CursorPageRequest negativeSize = new CursorPageRequest("cursor", -10);

            // then
            assertThat(zeroSize.size()).isEqualTo(CursorPageRequest.DEFAULT_SIZE);
            assertThat(negativeSize.size()).isEqualTo(CursorPageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("최대 사이즈를 초과하면 최대값으로 정규화된다")
        void shouldNormalizeExceedingSizeToMax() {
            // given
            int exceedingSize = 200;

            // when
            CursorPageRequest request = new CursorPageRequest("cursor", exceedingSize);

            // then
            assertThat(request.size()).isEqualTo(CursorPageRequest.MAX_SIZE);
        }

        @Test
        @DisplayName("경계값 MAX_SIZE는 허용된다")
        void shouldAllowMaxSize() {
            // when
            CursorPageRequest request = new CursorPageRequest("cursor", CursorPageRequest.MAX_SIZE);

            // then
            assertThat(request.size()).isEqualTo(CursorPageRequest.MAX_SIZE);
        }
    }

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("정적 팩토리 메서드로 CursorPageRequest를 생성한다")
        void shouldCreateCursorPageRequestUsingOf() {
            // when
            CursorPageRequest request = CursorPageRequest.of("cursor123", 30);

            // then
            assertThat(request.cursor()).isEqualTo("cursor123");
            assertThat(request.size()).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("first 팩토리 메서드")
    class FirstTest {

        @Test
        @DisplayName("첫 페이지 요청을 생성한다")
        void shouldCreateFirstPageRequest() {
            // when
            CursorPageRequest request = CursorPageRequest.first(25);

            // then
            assertThat(request.cursor()).isNull();
            assertThat(request.size()).isEqualTo(25);
        }
    }

    @Nested
    @DisplayName("defaultPage 팩토리 메서드")
    class DefaultPageTest {

        @Test
        @DisplayName("기본 설정의 CursorPageRequest를 생성한다")
        void shouldCreateDefaultPageRequest() {
            // when
            CursorPageRequest request = CursorPageRequest.defaultPage();

            // then
            assertThat(request.cursor()).isNull();
            assertThat(request.size()).isEqualTo(CursorPageRequest.DEFAULT_SIZE);
        }
    }

    @Nested
    @DisplayName("afterId 팩토리 메서드")
    class AfterIdTest {

        @Test
        @DisplayName("ID 기반 커서 요청을 생성한다")
        void shouldCreateAfterIdRequest() {
            // given
            Long lastId = 12345L;

            // when
            CursorPageRequest request = CursorPageRequest.afterId(lastId, 20);

            // then
            assertThat(request.cursor()).isEqualTo("12345");
            assertThat(request.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null ID로 첫 페이지 요청을 생성한다")
        void shouldCreateFirstPageWhenIdIsNull() {
            // when
            CursorPageRequest request = CursorPageRequest.afterId(null, 20);

            // then
            assertThat(request.cursor()).isNull();
            assertThat(request.isFirstPage()).isTrue();
        }
    }

    @Nested
    @DisplayName("isFirstPage 메서드")
    class IsFirstPageTest {

        @Test
        @DisplayName("커서가 null이면 첫 페이지다")
        void shouldReturnTrueWhenCursorIsNull() {
            // given
            CursorPageRequest request = CursorPageRequest.first(20);

            // then
            assertThat(request.isFirstPage()).isTrue();
        }

        @Test
        @DisplayName("커서가 있으면 첫 페이지가 아니다")
        void shouldReturnFalseWhenCursorExists() {
            // given
            CursorPageRequest request = CursorPageRequest.of("cursor", 20);

            // then
            assertThat(request.isFirstPage()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasCursor 메서드")
    class HasCursorTest {

        @Test
        @DisplayName("커서가 있으면 true를 반환한다")
        void shouldReturnTrueWhenHasCursor() {
            // given
            CursorPageRequest request = CursorPageRequest.of("cursor", 20);

            // then
            assertThat(request.hasCursor()).isTrue();
        }

        @Test
        @DisplayName("커서가 없으면 false를 반환한다")
        void shouldReturnFalseWhenNoCursor() {
            // given
            CursorPageRequest request = CursorPageRequest.first(20);

            // then
            assertThat(request.hasCursor()).isFalse();
        }
    }

    @Nested
    @DisplayName("cursorAsLong 메서드")
    class CursorAsLongTest {

        @Test
        @DisplayName("숫자 커서를 Long으로 파싱한다")
        void shouldParseCursorAsLong() {
            // given
            CursorPageRequest request = CursorPageRequest.of("12345", 20);

            // when
            Long cursorLong = request.cursorAsLong();

            // then
            assertThat(cursorLong).isEqualTo(12345L);
        }

        @Test
        @DisplayName("커서가 null이면 null을 반환한다")
        void shouldReturnNullWhenCursorIsNull() {
            // given
            CursorPageRequest request = CursorPageRequest.first(20);

            // when
            Long cursorLong = request.cursorAsLong();

            // then
            assertThat(cursorLong).isNull();
        }

        @Test
        @DisplayName("숫자가 아닌 커서는 null을 반환한다")
        void shouldReturnNullWhenCursorIsNotNumeric() {
            // given
            CursorPageRequest request = CursorPageRequest.of("abc123", 20);

            // when
            Long cursorLong = request.cursorAsLong();

            // then
            assertThat(cursorLong).isNull();
        }

        @Test
        @DisplayName("음수 커서도 파싱된다")
        void shouldParseNegativeCursor() {
            // given
            CursorPageRequest request = CursorPageRequest.of("-100", 20);

            // when
            Long cursorLong = request.cursorAsLong();

            // then
            assertThat(cursorLong).isEqualTo(-100L);
        }
    }

    @Nested
    @DisplayName("next 메서드")
    class NextTest {

        @Test
        @DisplayName("다음 페이지 요청을 생성한다")
        void shouldCreateNextPageRequest() {
            // given
            CursorPageRequest request = CursorPageRequest.first(20);
            String nextCursor = "newCursor";

            // when
            CursorPageRequest nextPage = request.next(nextCursor);

            // then
            assertThat(nextPage.cursor()).isEqualTo("newCursor");
            assertThat(nextPage.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null 커서로 다음 페이지를 생성할 수 있다")
        void shouldCreateNextWithNullCursor() {
            // given
            CursorPageRequest request = CursorPageRequest.of("cursor", 20);

            // when
            CursorPageRequest nextPage = request.next(null);

            // then
            assertThat(nextPage.cursor()).isNull();
            assertThat(nextPage.isFirstPage()).isTrue();
        }
    }

    @Nested
    @DisplayName("fetchSize 메서드")
    class FetchSizeTest {

        @Test
        @DisplayName("실제 조회 크기는 size + 1이다")
        void shouldReturnSizePlusOne() {
            // given
            CursorPageRequest request = CursorPageRequest.of("cursor", 20);

            // when
            int fetchSize = request.fetchSize();

            // then
            assertThat(fetchSize).isEqualTo(21);
        }

        @Test
        @DisplayName("기본 사이즈일 때 fetchSize를 반환한다")
        void shouldReturnFetchSizeForDefaultSize() {
            // given
            CursorPageRequest request = CursorPageRequest.defaultPage();

            // when
            int fetchSize = request.fetchSize();

            // then
            assertThat(fetchSize).isEqualTo(CursorPageRequest.DEFAULT_SIZE + 1);
        }
    }

    @Nested
    @DisplayName("상수 검증")
    class ConstantsTest {

        @Test
        @DisplayName("기본 사이즈는 20이다")
        void shouldHaveDefaultSizeOf20() {
            assertThat(CursorPageRequest.DEFAULT_SIZE).isEqualTo(20);
        }

        @Test
        @DisplayName("최대 사이즈는 100이다")
        void shouldHaveMaxSizeOf100() {
            assertThat(CursorPageRequest.MAX_SIZE).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 커서와 사이즈를 가진 CursorPageRequest는 동일하다")
        void shouldBeEqualWhenSameValues() {
            // given
            CursorPageRequest request1 = CursorPageRequest.of("cursor", 20);
            CursorPageRequest request2 = CursorPageRequest.of("cursor", 20);

            // then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }

        @Test
        @DisplayName("다른 커서를 가진 CursorPageRequest는 다르다")
        void shouldNotBeEqualWhenDifferentCursor() {
            // given
            CursorPageRequest request1 = CursorPageRequest.of("cursor1", 20);
            CursorPageRequest request2 = CursorPageRequest.of("cursor2", 20);

            // then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("다른 사이즈를 가진 CursorPageRequest는 다르다")
        void shouldNotBeEqualWhenDifferentSize() {
            // given
            CursorPageRequest request1 = CursorPageRequest.of("cursor", 20);
            CursorPageRequest request2 = CursorPageRequest.of("cursor", 30);

            // then
            assertThat(request1).isNotEqualTo(request2);
        }

        @Test
        @DisplayName("둘 다 null 커서면 동일하다")
        void shouldBeEqualWhenBothCursorsAreNull() {
            // given
            CursorPageRequest request1 = CursorPageRequest.first(20);
            CursorPageRequest request2 = CursorPageRequest.first(20);

            // then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }
    }
}
