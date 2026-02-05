package com.ryuqq.authhub.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PhoneNumber Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PhoneNumber 테스트")
class PhoneNumberTest {

    @Nested
    @DisplayName("PhoneNumber 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");

            // then
            assertThat(phoneNumber.value()).isEqualTo("010-1234-5678");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("숫자와 하이픈 외 문자가 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenInvalidPattern() {
            // when & then
            assertThatThrownBy(() -> PhoneNumber.of("010-1234-abcd"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("숫자와 하이픈만");
        }

        @Test
        @DisplayName("9자리 미만이면 예외가 발생한다")
        void shouldThrowExceptionWhenDigitsLessThanMinLength() {
            // when & then (010-123-456 = 9자리)
            assertThatThrownBy(() -> PhoneNumber.of("010-123-456"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최소 10자리");
        }

        @Test
        @DisplayName("10자리 숫자면 정상 생성된다")
        void shouldCreateWithMinLength() {
            // when
            PhoneNumber phoneNumber = PhoneNumber.of("0101234567");

            // then
            assertThat(phoneNumber.value()).isEqualTo("0101234567");
        }

        @Test
        @DisplayName("20자까지 정상 생성된다")
        void shouldCreateWithMaxLength() {
            // given - 정확히 20자 (숫자+하이픈)
            String value = "010-1234-5678-901234";

            // when
            PhoneNumber phoneNumber = PhoneNumber.of(value);

            // then
            assertThat(phoneNumber.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("21자면 예외가 발생한다")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given - 21자 (MAX_LENGTH=20 초과)
            String value = "010-1234-5678-9012345";

            // when & then
            assertThatThrownBy(() -> PhoneNumber.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 20자");
        }

        @Test
        @DisplayName("fromNullable()은 null이면 null을 반환한다")
        void fromNullableShouldReturnNullWhenValueIsNull() {
            // when
            PhoneNumber result = PhoneNumber.fromNullable(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("fromNullable()은 빈 문자열이면 null을 반환한다")
        void fromNullableShouldReturnNullWhenValueIsBlank() {
            // when
            PhoneNumber result = PhoneNumber.fromNullable("   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("fromNullable()은 유효한 값이면 PhoneNumber를 반환한다")
        void fromNullableShouldReturnPhoneNumberWhenValueIsValid() {
            // when
            PhoneNumber result = PhoneNumber.fromNullable("010-1234-5678");

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo("010-1234-5678");
        }
    }

    @Nested
    @DisplayName("PhoneNumber digitsOnly 테스트")
    class DigitsOnlyTests {

        @Test
        @DisplayName("digitsOnly()은 하이픈을 제거한 숫자만 반환한다")
        void digitsOnlyShouldReturnDigitsWithoutHyphen() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");

            // when
            String digits = phoneNumber.digitsOnly();

            // then
            assertThat(digits).isEqualTo("01012345678");
        }

        @Test
        @DisplayName("하이픈 없이 생성된 경우 digitsOnly()는 그대로 반환한다")
        void digitsOnlyShouldReturnSameWhenNoHyphen() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("01012345678");

            // when
            String digits = phoneNumber.digitsOnly();

            // then
            assertThat(digits).isEqualTo("01012345678");
        }
    }

    @Nested
    @DisplayName("PhoneNumber equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 PhoneNumber는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            PhoneNumber phone1 = PhoneNumber.of("010-1234-5678");
            PhoneNumber phone2 = PhoneNumber.of("010-1234-5678");

            // then
            assertThat(phone1).isEqualTo(phone2);
            assertThat(phone1.hashCode()).isEqualTo(phone2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 PhoneNumber는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            PhoneNumber phone1 = PhoneNumber.of("010-1234-5678");
            PhoneNumber phone2 = PhoneNumber.of("010-9999-8888");

            // then
            assertThat(phone1).isNotEqualTo(phone2);
        }
    }
}
