package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleSearchField 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleSearchField 테스트")
class RoleSearchFieldTest {

    @Nested
    @DisplayName("RoleSearchField defaultField 테스트")
    class DefaultFieldTests {

        @Test
        @DisplayName("defaultField()는 NAME을 반환한다")
        void shouldReturnName() {
            // when
            RoleSearchField result = RoleSearchField.defaultField();

            // then
            assertThat(result).isEqualTo(RoleSearchField.NAME);
        }
    }

    @Nested
    @DisplayName("RoleSearchField fromString 테스트")
    class FromStringTests {

        @Test
        @DisplayName("유효한 문자열로 RoleSearchField를 파싱한다")
        void shouldParseValidString() {
            // when & then
            assertThat(RoleSearchField.fromString("NAME")).isEqualTo(RoleSearchField.NAME);
            assertThat(RoleSearchField.fromString("DISPLAY_NAME"))
                    .isEqualTo(RoleSearchField.DISPLAY_NAME);
            assertThat(RoleSearchField.fromString("DESCRIPTION"))
                    .isEqualTo(RoleSearchField.DESCRIPTION);
        }

        @Test
        @DisplayName("null 입력 시 기본값(NAME)을 반환한다")
        void shouldReturnDefaultWhenInputIsNull() {
            // when
            RoleSearchField result = RoleSearchField.fromString(null);

            // then
            assertThat(result).isEqualTo(RoleSearchField.NAME);
        }

        @Test
        @DisplayName("빈 문자열 입력 시 기본값(NAME)을 반환한다")
        void shouldReturnDefaultWhenInputIsBlank() {
            // when
            RoleSearchField result = RoleSearchField.fromString("   ");

            // then
            assertThat(result).isEqualTo(RoleSearchField.NAME);
        }

        @Test
        @DisplayName("유효하지 않은 값 입력 시 기본값(NAME)을 반환한다")
        void shouldReturnDefaultWhenInputIsInvalid() {
            // when
            RoleSearchField result = RoleSearchField.fromString("INVALID");

            // then
            assertThat(result).isEqualTo(RoleSearchField.NAME);
        }

        @Test
        @DisplayName("대소문자 무관하게 파싱한다")
        void shouldParseCaseInsensitive() {
            // when & then
            assertThat(RoleSearchField.fromString("name")).isEqualTo(RoleSearchField.NAME);
            assertThat(RoleSearchField.fromString("display_name"))
                    .isEqualTo(RoleSearchField.DISPLAY_NAME);
            assertThat(RoleSearchField.fromString("description"))
                    .isEqualTo(RoleSearchField.DESCRIPTION);
        }
    }

    @Nested
    @DisplayName("RoleSearchField isName 테스트")
    class IsNameTests {

        @Test
        @DisplayName("NAME은 isName()이 true를 반환한다")
        void shouldReturnTrueForName() {
            // when & then
            assertThat(RoleSearchField.NAME.isName()).isTrue();
        }

        @Test
        @DisplayName("DISPLAY_NAME은 isName()이 false를 반환한다")
        void shouldReturnFalseForDisplayName() {
            // when & then
            assertThat(RoleSearchField.DISPLAY_NAME.isName()).isFalse();
        }

        @Test
        @DisplayName("DESCRIPTION은 isName()이 false를 반환한다")
        void shouldReturnFalseForDescription() {
            // when & then
            assertThat(RoleSearchField.DESCRIPTION.isName()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleSearchField isDisplayName 테스트")
    class IsDisplayNameTests {

        @Test
        @DisplayName("DISPLAY_NAME은 isDisplayName()이 true를 반환한다")
        void shouldReturnTrueForDisplayName() {
            // when & then
            assertThat(RoleSearchField.DISPLAY_NAME.isDisplayName()).isTrue();
        }

        @Test
        @DisplayName("NAME은 isDisplayName()이 false를 반환한다")
        void shouldReturnFalseForName() {
            // when & then
            assertThat(RoleSearchField.NAME.isDisplayName()).isFalse();
        }

        @Test
        @DisplayName("DESCRIPTION은 isDisplayName()이 false를 반환한다")
        void shouldReturnFalseForDescription() {
            // when & then
            assertThat(RoleSearchField.DESCRIPTION.isDisplayName()).isFalse();
        }
    }

    @Nested
    @DisplayName("RoleSearchField isDescription 테스트")
    class IsDescriptionTests {

        @Test
        @DisplayName("DESCRIPTION은 isDescription()이 true를 반환한다")
        void shouldReturnTrueForDescription() {
            // when & then
            assertThat(RoleSearchField.DESCRIPTION.isDescription()).isTrue();
        }

        @Test
        @DisplayName("NAME은 isDescription()이 false를 반환한다")
        void shouldReturnFalseForName() {
            // when & then
            assertThat(RoleSearchField.NAME.isDescription()).isFalse();
        }

        @Test
        @DisplayName("DISPLAY_NAME은 isDescription()이 false를 반환한다")
        void shouldReturnFalseForDisplayName() {
            // when & then
            assertThat(RoleSearchField.DISPLAY_NAME.isDescription()).isFalse();
        }
    }
}
