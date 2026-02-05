package com.ryuqq.authhub.domain.tenant.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantSearchField 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantSearchField 테스트")
class TenantSearchFieldTest {

    @Nested
    @DisplayName("TenantSearchField fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("NAME의 fieldName은 'name'이다")
        void nameFieldNameShouldBeName() {
            // then
            assertThat(TenantSearchField.NAME.fieldName()).isEqualTo("name");
        }
    }

    @Nested
    @DisplayName("TenantSearchField defaultField 테스트")
    class DefaultFieldTests {

        @Test
        @DisplayName("defaultField()는 NAME을 반환한다")
        void defaultFieldShouldReturnName() {
            // then
            assertThat(TenantSearchField.defaultField()).isEqualTo(TenantSearchField.NAME);
        }
    }

    @Nested
    @DisplayName("TenantSearchField fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("'NAME' 문자열로 NAME을 파싱한다")
        void shouldParseNameFromString() {
            // when
            TenantSearchField result = TenantSearchField.fromString("NAME");

            // then
            assertThat(result).isEqualTo(TenantSearchField.NAME);
        }

        @Test
        @DisplayName("소문자 'name' 문자열로 NAME을 파싱한다")
        void shouldParseNameFromLowerCaseString() {
            // when
            TenantSearchField result = TenantSearchField.fromString("name");

            // then
            assertThat(result).isEqualTo(TenantSearchField.NAME);
        }

        @Test
        @DisplayName("null 문자열이면 기본값을 반환한다")
        void shouldReturnDefaultWhenNull() {
            // when
            TenantSearchField result = TenantSearchField.fromString(null);

            // then
            assertThat(result).isEqualTo(TenantSearchField.defaultField());
        }

        @Test
        @DisplayName("빈 문자열이면 기본값을 반환한다")
        void shouldReturnDefaultWhenBlank() {
            // when
            TenantSearchField result = TenantSearchField.fromString("   ");

            // then
            assertThat(result).isEqualTo(TenantSearchField.defaultField());
        }

        @Test
        @DisplayName("유효하지 않은 값이면 기본값을 반환한다")
        void shouldReturnDefaultWhenInvalid() {
            // when
            TenantSearchField result = TenantSearchField.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(TenantSearchField.defaultField());
        }
    }
}
