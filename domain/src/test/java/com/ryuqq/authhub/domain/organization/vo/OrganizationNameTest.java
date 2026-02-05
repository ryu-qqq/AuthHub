package com.ryuqq.authhub.domain.organization.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationName 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("OrganizationName 테스트")
class OrganizationNameTest {

    @Nested
    @DisplayName("OrganizationName 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of()로 OrganizationName을 생성한다")
        void shouldCreateWithOf() {
            // given
            String value = "Test Organization";

            // when
            OrganizationName name = OrganizationName.of(value);

            // then
            assertThat(name.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("of()에 null을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenOfWithNull() {
            // when & then
            assertThatThrownBy(() -> OrganizationName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("of()에 빈 값을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenOfWithBlank() {
            // when & then
            assertThatThrownBy(() -> OrganizationName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 null이거나 빈 문자열일 수 없습니다");

            assertThatThrownBy(() -> OrganizationName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("of()에 공백만 입력하면 예외가 발생한다 (trim 후 빈값)")
        void shouldThrowExceptionWhenOfWithOnlyWhitespace() {
            // when & then
            assertThatThrownBy(() -> OrganizationName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("OrganizationName 경계값 테스트")
    class BoundaryTests {

        @Test
        @DisplayName("1자 길이의 OrganizationName을 생성할 수 있다")
        void shouldCreateWithMinLength() {
            // given
            String value = "A";

            // when
            OrganizationName name = OrganizationName.of(value);

            // then
            assertThat(name.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("100자 길이의 OrganizationName을 생성할 수 있다")
        void shouldCreateWithMaxLength() {
            // given
            String value = "A".repeat(100);

            // when
            OrganizationName name = OrganizationName.of(value);

            // then
            assertThat(name.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("101자 길이의 OrganizationName은 예외가 발생한다")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given
            String value = "A".repeat(101);

            // when & then
            assertThatThrownBy(() -> OrganizationName.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 1자 이상 100자 이하여야 합니다");
        }
    }

    @Nested
    @DisplayName("OrganizationName 공백 처리 테스트")
    class WhitespaceTests {

        @Test
        @DisplayName("앞뒤 공백이 있는 값은 trim되어 저장된다")
        void shouldTrimWhitespace() {
            // given
            String value = "  Test Organization  ";

            // when
            OrganizationName name = OrganizationName.of(value);

            // then
            assertThat(name.value()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("trim 후 빈 값이 되면 예외가 발생한다")
        void shouldThrowExceptionWhenTrimmedValueIsEmpty() {
            // when & then
            assertThatThrownBy(() -> OrganizationName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("OrganizationName 동등성 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 OrganizationName은 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            String value = "Test Organization";
            OrganizationName name1 = OrganizationName.of(value);
            OrganizationName name2 = OrganizationName.of(value);

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 OrganizationName은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            OrganizationName name1 = OrganizationName.of("Test Organization 1");
            OrganizationName name2 = OrganizationName.of("Test Organization 2");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }

        @Test
        @DisplayName("null과 비교하면 동등하지 않다")
        void shouldNotBeEqualWhenComparedWithNull() {
            // given
            OrganizationName name = OrganizationName.of("Test Organization");

            // then
            assertThat(name).isNotEqualTo(null);
        }
    }
}
