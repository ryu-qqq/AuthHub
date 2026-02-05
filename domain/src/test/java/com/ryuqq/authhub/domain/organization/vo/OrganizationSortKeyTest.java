package com.ryuqq.authhub.domain.organization.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationSortKey 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("OrganizationSortKey 테스트")
class OrganizationSortKeyTest {

    @Nested
    @DisplayName("OrganizationSortKey fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("각 SortKey의 fieldName을 반환한다")
        void fieldNameShouldReturnCorrectValue() {
            // then
            assertThat(OrganizationSortKey.NAME.fieldName()).isEqualTo("name");
            assertThat(OrganizationSortKey.STATUS.fieldName()).isEqualTo("status");
            assertThat(OrganizationSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
            assertThat(OrganizationSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }
    }

    @Nested
    @DisplayName("OrganizationSortKey defaultKey 테스트")
    class DefaultKeyTests {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void defaultKeyShouldReturnCreatedAt() {
            // then
            assertThat(OrganizationSortKey.defaultKey()).isEqualTo(OrganizationSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("OrganizationSortKey fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("'NAME' 문자열로 NAME을 파싱한다")
        void shouldParseNameFromString() {
            // when
            OrganizationSortKey result = OrganizationSortKey.fromString("NAME");

            // then
            assertThat(result).isEqualTo(OrganizationSortKey.NAME);
        }

        @Test
        @DisplayName("소문자 'name' 문자열로 NAME을 파싱한다")
        void shouldParseNameFromLowerCaseString() {
            // when
            OrganizationSortKey result = OrganizationSortKey.fromString("name");

            // then
            assertThat(result).isEqualTo(OrganizationSortKey.NAME);
        }

        @Test
        @DisplayName("하이픈이 포함된 문자열을 파싱한다")
        void shouldParseWithHyphen() {
            // when
            OrganizationSortKey result = OrganizationSortKey.fromString("CREATED-AT");

            // then
            assertThat(result).isEqualTo(OrganizationSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("null 문자열이면 기본값을 반환한다")
        void shouldReturnDefaultWhenNull() {
            // when
            OrganizationSortKey result = OrganizationSortKey.fromString(null);

            // then
            assertThat(result).isEqualTo(OrganizationSortKey.defaultKey());
        }

        @Test
        @DisplayName("빈 문자열이면 기본값을 반환한다")
        void shouldReturnDefaultWhenBlank() {
            // when
            OrganizationSortKey result = OrganizationSortKey.fromString("   ");

            // then
            assertThat(result).isEqualTo(OrganizationSortKey.defaultKey());
        }

        @Test
        @DisplayName("유효하지 않은 값이면 기본값을 반환한다")
        void shouldReturnDefaultWhenInvalid() {
            // when
            OrganizationSortKey result = OrganizationSortKey.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(OrganizationSortKey.defaultKey());
        }
    }
}
