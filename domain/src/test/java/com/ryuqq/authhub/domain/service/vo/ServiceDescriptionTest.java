package com.ryuqq.authhub.domain.service.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceDescription Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceDescription 테스트")
class ServiceDescriptionTest {

    @Nested
    @DisplayName("ServiceDescription 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            ServiceDescription description = ServiceDescription.of("서비스 설명");

            // then
            assertThat(description.value()).isEqualTo("서비스 설명");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            ServiceDescription description = new ServiceDescription("설명");

            // then
            assertThat(description.value()).isEqualTo("설명");
        }

        @Test
        @DisplayName("null 값으로 생성할 수 있다")
        void shouldCreateWithNull() {
            // when
            ServiceDescription description = ServiceDescription.of(null);

            // then
            assertThat(description.value()).isNull();
        }

        @Test
        @DisplayName("빈 문자열은 null로 변환된다")
        void shouldConvertEmptyStringToNull() {
            // when
            ServiceDescription description = ServiceDescription.of("");

            // then
            assertThat(description.value()).isNull();
        }

        @Test
        @DisplayName("공백만 있으면 null로 변환된다")
        void shouldConvertWhitespaceToNull() {
            // when
            ServiceDescription description = ServiceDescription.of("   ");

            // then
            assertThat(description.value()).isNull();
        }

        @Test
        @DisplayName("500자 초과이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueExceedsMaxLength() {
            // given
            String longValue = "A".repeat(501);

            // when & then
            assertThatThrownBy(() -> ServiceDescription.of(longValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("500자 이하여야 합니다");
        }

        @Test
        @DisplayName("500자까지는 생성할 수 있다")
        void shouldCreateWithMaxLength() {
            // given
            String maxLengthValue = "A".repeat(500);

            // when
            ServiceDescription description = ServiceDescription.of(maxLengthValue);

            // then
            assertThat(description.value()).isEqualTo(maxLengthValue);
        }

        @Test
        @DisplayName("앞뒤 공백은 제거된다")
        void shouldTrimWhitespace() {
            // when
            ServiceDescription description = ServiceDescription.of("  설명  ");

            // then
            assertThat(description.value()).isEqualTo("설명");
        }
    }

    @Nested
    @DisplayName("ServiceDescription empty 테스트")
    class EmptyTests {

        @Test
        @DisplayName("empty()는 null 값을 가진 ServiceDescription을 반환한다")
        void shouldReturnNullValue() {
            // when
            ServiceDescription description = ServiceDescription.empty();

            // then
            assertThat(description.value()).isNull();
        }
    }

    @Nested
    @DisplayName("ServiceDescription hasValue 테스트")
    class HasValueTests {

        @Test
        @DisplayName("값이 있으면 true를 반환한다")
        void shouldReturnTrueWhenValueExists() {
            // given
            ServiceDescription description = ServiceDescription.of("설명");

            // when & then
            assertThat(description.hasValue()).isTrue();
        }

        @Test
        @DisplayName("null이면 false를 반환한다")
        void shouldReturnFalseWhenValueIsNull() {
            // given
            ServiceDescription description = ServiceDescription.of(null);

            // when & then
            assertThat(description.hasValue()).isFalse();
        }

        @Test
        @DisplayName("빈 문자열이면 false를 반환한다")
        void shouldReturnFalseWhenValueIsEmpty() {
            // given
            ServiceDescription description = ServiceDescription.of("");

            // when & then
            assertThat(description.hasValue()).isFalse();
        }
    }

    @Nested
    @DisplayName("ServiceDescription equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 ServiceDescription은 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            ServiceDescription description1 = ServiceDescription.of("설명");
            ServiceDescription description2 = ServiceDescription.of("설명");

            // then
            assertThat(description1).isEqualTo(description2);
            assertThat(description1.hashCode()).isEqualTo(description2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ServiceDescription은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            ServiceDescription description1 = ServiceDescription.of("설명1");
            ServiceDescription description2 = ServiceDescription.of("설명2");

            // then
            assertThat(description1).isNotEqualTo(description2);
        }

        @Test
        @DisplayName("둘 다 null이면 동등하다")
        void shouldBeEqualWhenBothAreNull() {
            // given
            ServiceDescription description1 = ServiceDescription.of(null);
            ServiceDescription description2 = ServiceDescription.empty();

            // then
            assertThat(description1).isEqualTo(description2);
            assertThat(description1.hashCode()).isEqualTo(description2.hashCode());
        }
    }
}
