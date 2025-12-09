package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Action 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Action 테스트")
class ActionTest {

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("유효한 액션 이름을 생성한다")
        void shouldCreateValidAction() {
            // when
            Action action = Action.of("read");

            // then
            assertThat(action).isNotNull();
            assertThat(action.value()).isEqualTo("read");
        }

        @Test
        @DisplayName("대문자를 소문자로 변환한다")
        void shouldConvertToLowerCase() {
            // when
            Action action = Action.of("READ");

            // then
            assertThat(action.value()).isEqualTo("read");
        }

        @Test
        @DisplayName("언더스코어가 포함된 이름을 허용한다")
        void shouldAllowUnderscore() {
            // when
            Action action = Action.of("read_all");

            // then
            assertThat(action.value()).isEqualTo("read_all");
        }

        @Test
        @DisplayName("하이픈이 포함된 이름을 허용한다")
        void shouldAllowHyphen() {
            // when
            Action action = Action.of("read-only");

            // then
            assertThat(action.value()).isEqualTo("read-only");
        }

        @Test
        @DisplayName("숫자가 포함된 이름을 허용한다")
        void shouldAllowNumbers() {
            // when
            Action action = Action.of("read2");

            // then
            assertThat(action.value()).isEqualTo("read2");
        }

        @Test
        @DisplayName("공백을 트림한다")
        void shouldTrimWhitespace() {
            // when
            Action action = Action.of("  read  ");

            // then
            assertThat(action.value()).isEqualTo("read");
        }

        @Test
        @DisplayName("null이면 예외 발생")
        void shouldThrowExceptionWhenNull() {
            assertThatThrownBy(() -> Action.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열");
        }

        @Test
        @DisplayName("빈 문자열이면 예외 발생")
        void shouldThrowExceptionWhenEmpty() {
            assertThatThrownBy(() -> Action.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열");
        }

        @Test
        @DisplayName("30자 초과하면 예외 발생")
        void shouldThrowExceptionWhenTooLong() {
            String longName = "a".repeat(31);
            assertThatThrownBy(() -> Action.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("30자 이하");
        }

        @Test
        @DisplayName("숫자로 시작하면 예외 발생")
        void shouldThrowExceptionWhenStartsWithNumber() {
            assertThatThrownBy(() -> Action.of("1read"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("소문자로 시작");
        }

        @Test
        @DisplayName("특수문자가 포함되면 예외 발생")
        void shouldThrowExceptionWhenContainsSpecialCharacters() {
            assertThatThrownBy(() -> Action.of("read@all"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("소문자, 숫자, 언더스코어, 하이픈");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 Action은 동일하다")
        void shouldBeEqualWhenSameValue() {
            // given
            Action action1 = Action.of("read");
            Action action2 = Action.of("read");

            // then
            assertThat(action1).isEqualTo(action2);
            assertThat(action1.hashCode()).isEqualTo(action2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 Action은 다르다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            Action action1 = Action.of("read");
            Action action2 = Action.of("write");

            // then
            assertThat(action1).isNotEqualTo(action2);
        }

        @Test
        @DisplayName("대소문자 구분 없이 같으면 동일하다")
        void shouldBeEqualWhenSameValueIgnoreCase() {
            // given
            Action action1 = Action.of("read");
            Action action2 = Action.of("READ");

            // then
            assertThat(action1).isEqualTo(action2);
        }
    }
}
