package com.ryuqq.authhub.domain.permissionendpoint.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.vo.SortKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * PermissionEndpointSortKey Enum 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointSortKey Enum 테스트")
class PermissionEndpointSortKeyTest {

    @Nested
    @DisplayName("PermissionEndpointSortKey from() 변환 테스트")
    class FromTests {

        @ParameterizedTest
        @DisplayName("대문자로 변환한다")
        @ValueSource(strings = {"CREATED_AT", "UPDATED_AT", "URL_PATTERN", "HTTP_METHOD"})
        void shouldConvertUppercase(String key) {
            // when
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.from(key);

            // then
            assertThat(sortKey.name()).isEqualTo(key);
        }

        @ParameterizedTest
        @DisplayName("소문자로 변환한다")
        @CsvSource({
            "created_at, CREATED_AT",
            "updated_at, UPDATED_AT",
            "url_pattern, URL_PATTERN",
            "http_method, HTTP_METHOD"
        })
        void shouldConvertLowercase(String input, String expected) {
            // when
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.from(input);

            // then
            assertThat(sortKey.name()).isEqualTo(expected);
        }

        @ParameterizedTest
        @DisplayName("fieldName으로 변환한다")
        @CsvSource({
            "createdAt, CREATED_AT",
            "updatedAt, UPDATED_AT",
            "urlPattern, URL_PATTERN",
            "httpMethod, HTTP_METHOD"
        })
        void shouldConvertFromFieldName(String input, String expected) {
            // when
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.from(input);

            // then
            assertThat(sortKey.name()).isEqualTo(expected);
        }

        @Test
        @DisplayName("null 값이면 기본 키를 반환한다")
        void shouldReturnDefaultKeyWhenNull() {
            // when
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.from(null);

            // then
            assertThat(sortKey).isEqualTo(PermissionEndpointSortKey.defaultKey());
        }

        @Test
        @DisplayName("빈 값이면 기본 키를 반환한다")
        void shouldReturnDefaultKeyWhenBlank() {
            // when
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.from("");

            // then
            assertThat(sortKey).isEqualTo(PermissionEndpointSortKey.defaultKey());
        }

        @Test
        @DisplayName("공백만 있으면 기본 키를 반환한다")
        void shouldReturnDefaultKeyWhenWhitespace() {
            // when
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.from("   ");

            // then
            assertThat(sortKey).isEqualTo(PermissionEndpointSortKey.defaultKey());
        }

        @Test
        @DisplayName("유효하지 않은 값이면 기본 키를 반환한다")
        void shouldReturnDefaultKeyWhenInvalid() {
            // when
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.from("INVALID");

            // then
            assertThat(sortKey).isEqualTo(PermissionEndpointSortKey.defaultKey());
        }
    }

    @Nested
    @DisplayName("PermissionEndpointSortKey fieldName() 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("CREATED_AT의 fieldName은 createdAt이다")
        void createdAtShouldReturnCorrectFieldName() {
            // when & then
            assertThat(PermissionEndpointSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("UPDATED_AT의 fieldName은 updatedAt이다")
        void updatedAtShouldReturnCorrectFieldName() {
            // when & then
            assertThat(PermissionEndpointSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }

        @Test
        @DisplayName("URL_PATTERN의 fieldName은 urlPattern이다")
        void urlPatternShouldReturnCorrectFieldName() {
            // when & then
            assertThat(PermissionEndpointSortKey.URL_PATTERN.fieldName()).isEqualTo("urlPattern");
        }

        @Test
        @DisplayName("HTTP_METHOD의 fieldName은 httpMethod이다")
        void httpMethodShouldReturnCorrectFieldName() {
            // when & then
            assertThat(PermissionEndpointSortKey.HTTP_METHOD.fieldName()).isEqualTo("httpMethod");
        }
    }

    @Nested
    @DisplayName("PermissionEndpointSortKey SortKey 인터페이스 구현 테스트")
    class SortKeyInterfaceTests {

        @Test
        @DisplayName("SortKey 인터페이스를 구현한다")
        void shouldImplementSortKeyInterface() {
            // when
            PermissionEndpointSortKey sortKey = PermissionEndpointSortKey.CREATED_AT;

            // then
            assertThat(sortKey).isInstanceOf(SortKey.class);
        }
    }

    @Nested
    @DisplayName("PermissionEndpointSortKey defaultKey() 테스트")
    class DefaultKeyTests {

        @Test
        @DisplayName("defaultKey는 CREATED_AT을 반환한다")
        void defaultKeyShouldReturnCreatedAt() {
            // when
            PermissionEndpointSortKey defaultKey = PermissionEndpointSortKey.defaultKey();

            // then
            assertThat(defaultKey).isEqualTo(PermissionEndpointSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("PermissionEndpointSortKey Enum 값 테스트")
    class EnumValuesTests {

        @Test
        @DisplayName("모든 enum 값이 존재한다")
        void shouldHaveAllExpectedValues() {
            // when
            PermissionEndpointSortKey[] values = PermissionEndpointSortKey.values();

            // then
            assertThat(values).hasSize(4);
            assertThat(values)
                    .containsExactlyInAnyOrder(
                            PermissionEndpointSortKey.CREATED_AT,
                            PermissionEndpointSortKey.UPDATED_AT,
                            PermissionEndpointSortKey.URL_PATTERN,
                            PermissionEndpointSortKey.HTTP_METHOD);
        }
    }
}
