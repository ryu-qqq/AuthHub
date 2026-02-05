package com.ryuqq.authhub.domain.organization.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationSearchField 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("OrganizationSearchField 테스트")
class OrganizationSearchFieldTest {

    @Nested
    @DisplayName("OrganizationSearchField fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("NAME의 fieldName은 'name'이다")
        void nameFieldNameShouldBeName() {
            // then
            assertThat(OrganizationSearchField.NAME.fieldName()).isEqualTo("name");
        }
    }

    @Nested
    @DisplayName("OrganizationSearchField defaultField 테스트")
    class DefaultFieldTests {

        @Test
        @DisplayName("defaultField()는 NAME을 반환한다")
        void defaultFieldShouldReturnName() {
            // then
            assertThat(OrganizationSearchField.defaultField())
                    .isEqualTo(OrganizationSearchField.NAME);
        }
    }

    @Nested
    @DisplayName("OrganizationSearchField fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("'NAME' 문자열로 NAME을 파싱한다")
        void shouldParseNameFromString() {
            // when
            OrganizationSearchField result = OrganizationSearchField.fromString("NAME");

            // then
            assertThat(result).isEqualTo(OrganizationSearchField.NAME);
        }

        @Test
        @DisplayName("소문자 'name' 문자열로 NAME을 파싱한다")
        void shouldParseNameFromLowerCaseString() {
            // when
            OrganizationSearchField result = OrganizationSearchField.fromString("name");

            // then
            assertThat(result).isEqualTo(OrganizationSearchField.NAME);
        }

        @Test
        @DisplayName("null 문자열이면 기본값을 반환한다")
        void shouldReturnDefaultWhenNull() {
            // when
            OrganizationSearchField result = OrganizationSearchField.fromString(null);

            // then
            assertThat(result).isEqualTo(OrganizationSearchField.defaultField());
        }

        @Test
        @DisplayName("빈 문자열이면 기본값을 반환한다")
        void shouldReturnDefaultWhenBlank() {
            // when
            OrganizationSearchField result = OrganizationSearchField.fromString("   ");

            // then
            assertThat(result).isEqualTo(OrganizationSearchField.defaultField());
        }

        @Test
        @DisplayName("유효하지 않은 값이면 기본값을 반환한다")
        void shouldReturnDefaultWhenInvalid() {
            // when
            OrganizationSearchField result = OrganizationSearchField.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(OrganizationSearchField.defaultField());
        }
    }
}
