package com.ryuqq.authhub.domain.permissionendpoint.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * PermissionEndpointSearchField Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointSearchField Enum 테스트")
class PermissionEndpointSearchFieldTest {

    @Nested
    @DisplayName("PermissionEndpointSearchField from() 변환 테스트")
    class FromTests {

        @ParameterizedTest
        @DisplayName("대문자로 변환한다")
        @ValueSource(strings = {"URL_PATTERN", "HTTP_METHOD", "DESCRIPTION"})
        void shouldConvertUppercase(String field) {
            // when
            PermissionEndpointSearchField searchField = PermissionEndpointSearchField.from(field);

            // then
            assertThat(searchField.name()).isEqualTo(field);
        }

        @ParameterizedTest
        @DisplayName("소문자로 변환한다")
        @CsvSource({
            "url_pattern, URL_PATTERN",
            "http_method, HTTP_METHOD",
            "description, DESCRIPTION"
        })
        void shouldConvertLowercase(String input, String expected) {
            // when
            PermissionEndpointSearchField searchField = PermissionEndpointSearchField.from(input);

            // then
            assertThat(searchField.name()).isEqualTo(expected);
        }

        @ParameterizedTest
        @DisplayName("fieldName으로 변환한다")
        @CsvSource({
            "urlPattern, URL_PATTERN",
            "httpMethod, HTTP_METHOD",
            "description, DESCRIPTION"
        })
        void shouldConvertFromFieldName(String input, String expected) {
            // when
            PermissionEndpointSearchField searchField = PermissionEndpointSearchField.from(input);

            // then
            assertThat(searchField.name()).isEqualTo(expected);
        }

        @Test
        @DisplayName("null 값이면 기본 필드를 반환한다")
        void shouldReturnDefaultFieldWhenNull() {
            // when
            PermissionEndpointSearchField searchField = PermissionEndpointSearchField.from(null);

            // then
            assertThat(searchField).isEqualTo(PermissionEndpointSearchField.defaultField());
        }

        @Test
        @DisplayName("빈 값이면 기본 필드를 반환한다")
        void shouldReturnDefaultFieldWhenBlank() {
            // when
            PermissionEndpointSearchField searchField = PermissionEndpointSearchField.from("");

            // then
            assertThat(searchField).isEqualTo(PermissionEndpointSearchField.defaultField());
        }

        @Test
        @DisplayName("공백만 있으면 기본 필드를 반환한다")
        void shouldReturnDefaultFieldWhenWhitespace() {
            // when
            PermissionEndpointSearchField searchField = PermissionEndpointSearchField.from("   ");

            // then
            assertThat(searchField).isEqualTo(PermissionEndpointSearchField.defaultField());
        }

        @Test
        @DisplayName("유효하지 않은 값이면 기본 필드를 반환한다")
        void shouldReturnDefaultFieldWhenInvalid() {
            // when
            PermissionEndpointSearchField searchField =
                    PermissionEndpointSearchField.from("INVALID");

            // then
            assertThat(searchField).isEqualTo(PermissionEndpointSearchField.defaultField());
        }
    }

    @Nested
    @DisplayName("PermissionEndpointSearchField fieldName() 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("URL_PATTERN의 fieldName은 urlPattern이다")
        void urlPatternShouldReturnCorrectFieldName() {
            // when & then
            assertThat(PermissionEndpointSearchField.URL_PATTERN.fieldName())
                    .isEqualTo("urlPattern");
        }

        @Test
        @DisplayName("HTTP_METHOD의 fieldName은 httpMethod이다")
        void httpMethodShouldReturnCorrectFieldName() {
            // when & then
            assertThat(PermissionEndpointSearchField.HTTP_METHOD.fieldName())
                    .isEqualTo("httpMethod");
        }

        @Test
        @DisplayName("DESCRIPTION의 fieldName은 description이다")
        void descriptionShouldReturnCorrectFieldName() {
            // when & then
            assertThat(PermissionEndpointSearchField.DESCRIPTION.fieldName())
                    .isEqualTo("description");
        }
    }

    @Nested
    @DisplayName("PermissionEndpointSearchField defaultField() 테스트")
    class DefaultFieldTests {

        @Test
        @DisplayName("defaultField는 URL_PATTERN을 반환한다")
        void defaultFieldShouldReturnUrlPattern() {
            // when
            PermissionEndpointSearchField defaultField =
                    PermissionEndpointSearchField.defaultField();

            // then
            assertThat(defaultField).isEqualTo(PermissionEndpointSearchField.URL_PATTERN);
        }
    }

    @Nested
    @DisplayName("PermissionEndpointSearchField Enum 값 테스트")
    class EnumValuesTests {

        @Test
        @DisplayName("모든 enum 값이 존재한다")
        void shouldHaveAllExpectedValues() {
            // when
            PermissionEndpointSearchField[] values = PermissionEndpointSearchField.values();

            // then
            assertThat(values).hasSize(3);
            assertThat(values)
                    .containsExactlyInAnyOrder(
                            PermissionEndpointSearchField.URL_PATTERN,
                            PermissionEndpointSearchField.HTTP_METHOD,
                            PermissionEndpointSearchField.DESCRIPTION);
        }
    }
}
