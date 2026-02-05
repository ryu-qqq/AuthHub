package com.ryuqq.authhub.domain.permissionendpoint.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * HttpMethod Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("HttpMethod Enum 테스트")
class HttpMethodTest {

    @Nested
    @DisplayName("HttpMethod from() 변환 테스트")
    class FromTests {

        @ParameterizedTest
        @DisplayName("대문자로 변환한다")
        @ValueSource(strings = {"GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"})
        void shouldConvertUppercase(String method) {
            // when
            HttpMethod httpMethod = HttpMethod.from(method);

            // then
            assertThat(httpMethod.name()).isEqualTo(method);
        }

        @ParameterizedTest
        @DisplayName("소문자로 변환한다")
        @CsvSource({
            "get, GET",
            "post, POST",
            "put, PUT",
            "patch, PATCH",
            "delete, DELETE",
            "head, HEAD",
            "options, OPTIONS"
        })
        void shouldConvertLowercase(String input, String expected) {
            // when
            HttpMethod httpMethod = HttpMethod.from(input);

            // then
            assertThat(httpMethod.name()).isEqualTo(expected);
        }

        @ParameterizedTest
        @DisplayName("대소문자 혼합으로 변환한다")
        @CsvSource({"Get, GET", "Post, POST", "Put, PUT", "Patch, PATCH", "Delete, DELETE"})
        void shouldConvertMixedCase(String input, String expected) {
            // when
            HttpMethod httpMethod = HttpMethod.from(input);

            // then
            assertThat(httpMethod.name()).isEqualTo(expected);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> HttpMethod.from(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> HttpMethod.from(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> HttpMethod.from("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @ParameterizedTest
        @DisplayName("유효하지 않은 값이면 예외가 발생한다")
        @ValueSource(strings = {"INVALID", "GETS", "POSTS", "UNKNOWN", "TEST"})
        void shouldThrowExceptionWhenInvalidValue(String invalidValue) {
            // when & then
            assertThatThrownBy(() -> HttpMethod.from(invalidValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 HttpMethod");
        }
    }

    @Nested
    @DisplayName("HttpMethod isReadOnly() 테스트")
    class IsReadOnlyTests {

        @ParameterizedTest
        @DisplayName("읽기 전용 메서드는 true를 반환한다")
        @ValueSource(strings = {"GET", "HEAD", "OPTIONS"})
        void shouldReturnTrueForReadOnlyMethods(String method) {
            // given
            HttpMethod httpMethod = HttpMethod.valueOf(method);

            // when & then
            assertThat(httpMethod.isReadOnly()).isTrue();
        }

        @ParameterizedTest
        @DisplayName("쓰기 메서드는 false를 반환한다")
        @ValueSource(strings = {"POST", "PUT", "PATCH", "DELETE"})
        void shouldReturnFalseForWriteMethods(String method) {
            // given
            HttpMethod httpMethod = HttpMethod.valueOf(method);

            // when & then
            assertThat(httpMethod.isReadOnly()).isFalse();
        }
    }

    @Nested
    @DisplayName("HttpMethod isWriteOperation() 테스트")
    class IsWriteOperationTests {

        @ParameterizedTest
        @DisplayName("쓰기 메서드는 true를 반환한다")
        @ValueSource(strings = {"POST", "PUT", "PATCH", "DELETE"})
        void shouldReturnTrueForWriteMethods(String method) {
            // given
            HttpMethod httpMethod = HttpMethod.valueOf(method);

            // when & then
            assertThat(httpMethod.isWriteOperation()).isTrue();
        }

        @ParameterizedTest
        @DisplayName("읽기 전용 메서드는 false를 반환한다")
        @ValueSource(strings = {"GET", "HEAD", "OPTIONS"})
        void shouldReturnFalseForReadOnlyMethods(String method) {
            // given
            HttpMethod httpMethod = HttpMethod.valueOf(method);

            // when & then
            assertThat(httpMethod.isWriteOperation()).isFalse();
        }
    }

    @Nested
    @DisplayName("HttpMethod Enum 값 테스트")
    class EnumValuesTests {

        @Test
        @DisplayName("모든 enum 값이 존재한다")
        void shouldHaveAllExpectedValues() {
            // when
            HttpMethod[] values = HttpMethod.values();

            // then
            assertThat(values).hasSize(7);
            assertThat(values)
                    .containsExactlyInAnyOrder(
                            HttpMethod.GET,
                            HttpMethod.POST,
                            HttpMethod.PUT,
                            HttpMethod.PATCH,
                            HttpMethod.DELETE,
                            HttpMethod.HEAD,
                            HttpMethod.OPTIONS);
        }
    }
}
