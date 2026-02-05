package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleSortKey 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleSortKey 테스트")
class RoleSortKeyTest {

    @Nested
    @DisplayName("RoleSortKey fieldName 테스트")
    class FieldNameTests {

        @Test
        @DisplayName("각 RoleSortKey는 올바른 fieldName을 반환한다")
        void shouldReturnCorrectFieldName() {
            // when & then
            assertThat(RoleSortKey.ROLE_ID.fieldName()).isEqualTo("roleId");
            assertThat(RoleSortKey.NAME.fieldName()).isEqualTo("name");
            assertThat(RoleSortKey.DISPLAY_NAME.fieldName()).isEqualTo("displayName");
            assertThat(RoleSortKey.CREATED_AT.fieldName()).isEqualTo("createdAt");
            assertThat(RoleSortKey.UPDATED_AT.fieldName()).isEqualTo("updatedAt");
        }
    }

    @Nested
    @DisplayName("RoleSortKey defaultKey 테스트")
    class DefaultKeyTests {

        @Test
        @DisplayName("defaultKey()는 CREATED_AT을 반환한다")
        void shouldReturnCreatedAt() {
            // when
            RoleSortKey result = RoleSortKey.defaultKey();

            // then
            assertThat(result).isEqualTo(RoleSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("RoleSortKey fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("유효한 문자열로 RoleSortKey를 파싱한다")
        void shouldParseValidString() {
            // when & then
            assertThat(RoleSortKey.fromString("ROLE_ID")).isEqualTo(RoleSortKey.ROLE_ID);
            assertThat(RoleSortKey.fromString("NAME")).isEqualTo(RoleSortKey.NAME);
            assertThat(RoleSortKey.fromString("DISPLAY_NAME")).isEqualTo(RoleSortKey.DISPLAY_NAME);
            assertThat(RoleSortKey.fromString("CREATED_AT")).isEqualTo(RoleSortKey.CREATED_AT);
            assertThat(RoleSortKey.fromString("UPDATED_AT")).isEqualTo(RoleSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("fieldName으로도 파싱할 수 있다")
        void shouldParseByFieldName() {
            // when & then
            assertThat(RoleSortKey.fromString("roleId")).isEqualTo(RoleSortKey.ROLE_ID);
            assertThat(RoleSortKey.fromString("name")).isEqualTo(RoleSortKey.NAME);
            assertThat(RoleSortKey.fromString("displayName")).isEqualTo(RoleSortKey.DISPLAY_NAME);
            assertThat(RoleSortKey.fromString("createdAt")).isEqualTo(RoleSortKey.CREATED_AT);
            assertThat(RoleSortKey.fromString("updatedAt")).isEqualTo(RoleSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("null 입력 시 기본값(CREATED_AT)을 반환한다")
        void shouldReturnDefaultWhenInputIsNull() {
            // when
            RoleSortKey result = RoleSortKey.fromString(null);

            // then
            assertThat(result).isEqualTo(RoleSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("빈 문자열 입력 시 기본값(CREATED_AT)을 반환한다")
        void shouldReturnDefaultWhenInputIsBlank() {
            // when
            RoleSortKey result = RoleSortKey.fromString("   ");

            // then
            assertThat(result).isEqualTo(RoleSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("유효하지 않은 값 입력 시 기본값(CREATED_AT)을 반환한다")
        void shouldReturnDefaultWhenInputIsInvalid() {
            // when
            RoleSortKey result = RoleSortKey.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(RoleSortKey.CREATED_AT);
        }

        @Test
        @DisplayName("하이픈이 포함된 문자열도 파싱할 수 있다")
        void shouldParseStringWithHyphen() {
            // when & then
            assertThat(RoleSortKey.fromString("CREATED-AT")).isEqualTo(RoleSortKey.CREATED_AT);
            assertThat(RoleSortKey.fromString("UPDATED-AT")).isEqualTo(RoleSortKey.UPDATED_AT);
        }

        @Test
        @DisplayName("대소문자 무관하게 파싱한다")
        void shouldParseCaseInsensitive() {
            // when & then
            assertThat(RoleSortKey.fromString("role_id")).isEqualTo(RoleSortKey.ROLE_ID);
            assertThat(RoleSortKey.fromString("name")).isEqualTo(RoleSortKey.NAME);
        }
    }
}
