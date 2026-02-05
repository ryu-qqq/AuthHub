package com.ryuqq.authhub.domain.service.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceSearchField 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceSearchField 테스트")
class ServiceSearchFieldTest {

    @Nested
    @DisplayName("ServiceSearchField fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("각 ServiceSearchField는 올바른 fieldName을 반환한다")
        void shouldReturnCorrectFieldName() {
            // when & then
            assertThat(ServiceSearchField.SERVICE_CODE.fieldName()).isEqualTo("serviceCode");
            assertThat(ServiceSearchField.NAME.fieldName()).isEqualTo("name");
        }
    }

    @Nested
    @DisplayName("ServiceSearchField defaultField 테스트")
    class DefaultFieldTests {

        @Test
        @DisplayName("defaultField()는 NAME을 반환한다")
        void shouldReturnName() {
            // when
            ServiceSearchField result = ServiceSearchField.defaultField();

            // then
            assertThat(result).isEqualTo(ServiceSearchField.NAME);
        }
    }

    @Nested
    @DisplayName("ServiceSearchField fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("유효한 문자열로 ServiceSearchField를 파싱한다")
        void shouldParseValidString() {
            // when & then
            assertThat(ServiceSearchField.fromString("SERVICE_CODE"))
                    .isEqualTo(ServiceSearchField.SERVICE_CODE);
            assertThat(ServiceSearchField.fromString("NAME")).isEqualTo(ServiceSearchField.NAME);
        }

        @Test
        @DisplayName("null 입력 시 기본값(NAME)을 반환한다")
        void shouldReturnDefaultWhenInputIsNull() {
            // when
            ServiceSearchField result = ServiceSearchField.fromString(null);

            // then
            assertThat(result).isEqualTo(ServiceSearchField.NAME);
        }

        @Test
        @DisplayName("빈 문자열 입력 시 기본값(NAME)을 반환한다")
        void shouldReturnDefaultWhenInputIsBlank() {
            // when
            ServiceSearchField result = ServiceSearchField.fromString("   ");

            // then
            assertThat(result).isEqualTo(ServiceSearchField.NAME);
        }

        @Test
        @DisplayName("유효하지 않은 값 입력 시 기본값(NAME)을 반환한다")
        void shouldReturnDefaultWhenInputIsInvalid() {
            // when
            ServiceSearchField result = ServiceSearchField.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(ServiceSearchField.NAME);
        }

        @Test
        @DisplayName("대소문자 무관하게 파싱한다")
        void shouldParseCaseInsensitive() {
            // when & then
            assertThat(ServiceSearchField.fromString("service_code"))
                    .isEqualTo(ServiceSearchField.SERVICE_CODE);
            assertThat(ServiceSearchField.fromString("name")).isEqualTo(ServiceSearchField.NAME);
        }
    }
}
