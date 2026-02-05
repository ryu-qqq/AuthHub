package com.ryuqq.authhub.domain.permission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Action Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Action 테스트")
class ActionTest {

    @Nested
    @DisplayName("Action 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            Action action = Action.of("read");

            // then
            assertThat(action.value()).isEqualTo("read");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            Action action = new Action("read");

            // then
            assertThat(action.value()).isEqualTo("read");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> Action.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> Action.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> Action.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("최대 길이(50자)로 생성할 수 있다")
        void shouldCreateWithMaxLength() {
            // given
            String maxLengthValue = "a".repeat(50);

            // when
            Action action = Action.of(maxLengthValue);

            // then
            assertThat(action.value()).isEqualTo(maxLengthValue);
        }

        @Test
        @DisplayName("최대 길이를 초과하면 예외가 발생한다")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given
            String tooLongValue = "a".repeat(51);

            // when & then
            assertThatThrownBy(() -> Action.of(tooLongValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("영문 소문자로 시작하는 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPattern() {
            // when
            Action action1 = Action.of("read");
            Action action2 = Action.of("create");
            Action action3 = Action.of("update");
            Action action4 = Action.of("delete");
            Action action5 = Action.of("manage");

            // then
            assertThat(action1.value()).isEqualTo("read");
            assertThat(action2.value()).isEqualTo("create");
            assertThat(action3.value()).isEqualTo("update");
            assertThat(action4.value()).isEqualTo("delete");
            assertThat(action5.value()).isEqualTo("manage");
        }

        @Test
        @DisplayName("숫자가 포함된 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPatternIncludingNumbers() {
            // when
            Action action = Action.of("read123");

            // then
            assertThat(action.value()).isEqualTo("read123");
        }

        @Test
        @DisplayName("하이픈이 포함된 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPatternIncludingHyphen() {
            // when
            Action action = Action.of("read-data");

            // then
            assertThat(action.value()).isEqualTo("read-data");
        }

        @Test
        @DisplayName("대문자로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithUpperCase() {
            // when & then
            assertThatThrownBy(() -> Action.of("Read"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자로 시작");
        }

        @Test
        @DisplayName("숫자로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithNumber() {
            // when & then
            assertThatThrownBy(() -> Action.of("1read"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자로 시작");
        }

        @Test
        @DisplayName("하이픈으로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithHyphen() {
            // when & then
            assertThatThrownBy(() -> Action.of("-read"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자로 시작");
        }

        @Test
        @DisplayName("특수문자가 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsSpecialCharacters() {
            // when & then
            assertThatThrownBy(() -> Action.of("read_data"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 허용됩니다");
        }

        @Test
        @DisplayName("공백이 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsWhitespace() {
            // when & then
            assertThatThrownBy(() -> Action.of("read data"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 허용됩니다");
        }
    }

    @Nested
    @DisplayName("Action equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 Action은 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            Action action1 = Action.of("read");
            Action action2 = Action.of("read");

            // then
            assertThat(action1).isEqualTo(action2);
            assertThat(action1.hashCode()).isEqualTo(action2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 Action은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            Action action1 = Action.of("read");
            Action action2 = Action.of("write");

            // then
            assertThat(action1).isNotEqualTo(action2);
        }
    }
}
