package com.ryuqq.authhub.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Identifier Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("Identifier 테스트")
class IdentifierTest {

    @Nested
    @DisplayName("Identifier 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            Identifier identifier = Identifier.of("user@test.com");

            // then
            assertThat(identifier.value()).isEqualTo("user@test.com");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> Identifier.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> Identifier.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("3자 미만이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsLessThanMinLength() {
            // when & then
            assertThatThrownBy(() -> Identifier.of("abc"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최소 4자 이상");
        }

        @Test
        @DisplayName("4자면 정상 생성된다")
        void shouldCreateWithMinLength() {
            // when
            Identifier identifier = Identifier.of("abcd");

            // then
            assertThat(identifier.value()).isEqualTo("abcd");
        }

        @Test
        @DisplayName("100자면 정상 생성된다")
        void shouldCreateWithMaxLength() {
            // given
            String value = "a".repeat(100);

            // when
            Identifier identifier = Identifier.of(value);

            // then
            assertThat(identifier.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("101자 이상이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueExceedsMaxLength() {
            // given
            String value = "a".repeat(101);

            // when & then
            assertThatThrownBy(() -> Identifier.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 100자까지");
        }

        @Test
        @DisplayName("fromNullable()은 null이면 null을 반환한다")
        void fromNullableShouldReturnNullWhenValueIsNull() {
            // when
            Identifier result = Identifier.fromNullable(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("fromNullable()은 빈 문자열이면 null을 반환한다")
        void fromNullableShouldReturnNullWhenValueIsBlank() {
            // when
            Identifier result = Identifier.fromNullable("   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("fromNullable()은 유효한 값이면 Identifier를 반환한다")
        void fromNullableShouldReturnIdentifierWhenValueIsValid() {
            // when
            Identifier result = Identifier.fromNullable("user@test.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo("user@test.com");
        }
    }

    @Nested
    @DisplayName("Identifier equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 Identifier는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            Identifier identifier1 = Identifier.of("user@test.com");
            Identifier identifier2 = Identifier.of("user@test.com");

            // then
            assertThat(identifier1).isEqualTo(identifier2);
            assertThat(identifier1.hashCode()).isEqualTo(identifier2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 Identifier는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            Identifier identifier1 = Identifier.of("user1@test.com");
            Identifier identifier2 = Identifier.of("user2@test.com");

            // then
            assertThat(identifier1).isNotEqualTo(identifier2);
        }
    }
}
