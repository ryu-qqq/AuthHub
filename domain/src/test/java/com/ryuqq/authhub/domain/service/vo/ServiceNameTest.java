package com.ryuqq.authhub.domain.service.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceName Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceName 테스트")
class ServiceNameTest {

    @Nested
    @DisplayName("ServiceName 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            ServiceName serviceName = ServiceName.of("자사몰");

            // then
            assertThat(serviceName.value()).isEqualTo("자사몰");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            ServiceName serviceName = new ServiceName("B2B 서비스");

            // then
            assertThat(serviceName.value()).isEqualTo("B2B 서비스");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("100자 초과이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueExceedsMaxLength() {
            // given
            String longValue = "A".repeat(101);

            // when & then
            assertThatThrownBy(() -> ServiceName.of(longValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1자 이상 100자 이하여야 합니다");
        }

        @Test
        @DisplayName("1자로 생성할 수 있다")
        void shouldCreateWithMinLength() {
            // when
            ServiceName serviceName = ServiceName.of("A");

            // then
            assertThat(serviceName.value()).isEqualTo("A");
        }

        @Test
        @DisplayName("100자로 생성할 수 있다")
        void shouldCreateWithMaxLength() {
            // given
            String maxLengthValue = "A".repeat(100);

            // when
            ServiceName serviceName = ServiceName.of(maxLengthValue);

            // then
            assertThat(serviceName.value()).isEqualTo(maxLengthValue);
        }

        @Test
        @DisplayName("앞뒤 공백은 제거된다")
        void shouldTrimWhitespace() {
            // when
            ServiceName serviceName = ServiceName.of("  자사몰  ");

            // then
            assertThat(serviceName.value()).isEqualTo("자사몰");
        }

        @Test
        @DisplayName("한글 이름으로 생성할 수 있다")
        void shouldCreateWithKoreanName() {
            // when
            ServiceName serviceName = ServiceName.of("자사몰 서비스");

            // then
            assertThat(serviceName.value()).isEqualTo("자사몰 서비스");
        }

        @Test
        @DisplayName("영문 이름으로 생성할 수 있다")
        void shouldCreateWithEnglishName() {
            // when
            ServiceName serviceName = ServiceName.of("B2B Service");

            // then
            assertThat(serviceName.value()).isEqualTo("B2B Service");
        }
    }

    @Nested
    @DisplayName("ServiceName equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 ServiceName은 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            ServiceName serviceName1 = ServiceName.of("자사몰");
            ServiceName serviceName2 = ServiceName.of("자사몰");

            // then
            assertThat(serviceName1).isEqualTo(serviceName2);
            assertThat(serviceName1.hashCode()).isEqualTo(serviceName2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ServiceName은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            ServiceName serviceName1 = ServiceName.of("자사몰");
            ServiceName serviceName2 = ServiceName.of("B2B 서비스");

            // then
            assertThat(serviceName1).isNotEqualTo(serviceName2);
        }

        @Test
        @DisplayName("공백이 제거된 후 동등성을 비교한다")
        void shouldBeEqualAfterTrim() {
            // given
            ServiceName serviceName1 = ServiceName.of("자사몰");
            ServiceName serviceName2 = ServiceName.of("  자사몰  ");

            // then
            assertThat(serviceName1).isEqualTo(serviceName2);
            assertThat(serviceName1.hashCode()).isEqualTo(serviceName2.hashCode());
        }
    }
}
