package com.ryuqq.authhub.domain.permissionendpoint.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * ServiceName Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceName Value Object 테스트")
class ServiceNameTest {

    @Nested
    @DisplayName("ServiceName 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("정상적인 서비스 이름으로 생성한다")
        void shouldCreateWithValidName() {
            // when
            ServiceName serviceName = ServiceName.of("authhub");

            // then
            assertThat(serviceName.value()).isEqualTo("authhub");
        }

        @Test
        @DisplayName("하이픈을 포함한 서비스 이름으로 생성한다")
        void shouldCreateWithHyphen() {
            // when
            ServiceName serviceName = ServiceName.of("product-service");

            // then
            assertThat(serviceName.value()).isEqualTo("product-service");
        }

        @Test
        @DisplayName("숫자를 포함한 서비스 이름으로 생성한다")
        void shouldCreateWithNumbers() {
            // when
            ServiceName serviceName = ServiceName.of("api-v2");

            // then
            assertThat(serviceName.value()).isEqualTo("api-v2");
        }

        @Test
        @DisplayName("단일 문자로 생성한다")
        void shouldCreateWithSingleCharacter() {
            // when
            ServiceName serviceName = ServiceName.of("a");

            // then
            assertThat(serviceName.value()).isEqualTo("a");
        }

        @Test
        @DisplayName("단일 숫자로 생성한다")
        void shouldCreateWithSingleNumber() {
            // when
            ServiceName serviceName = ServiceName.of("1");

            // then
            assertThat(serviceName.value()).isEqualTo("1");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("100자를 초과하면 예외가 발생한다")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given
            String longName = "a".repeat(101);

            // when & then
            assertThatThrownBy(() -> ServiceName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100자를 초과할 수 없습니다");
        }

        @Test
        @DisplayName("정확히 100자이면 생성된다")
        void shouldCreateWithExactly100Characters() {
            // given
            String name100 = "a".repeat(100);

            // when
            ServiceName serviceName = ServiceName.of(name100);

            // then
            assertThat(serviceName.value()).isEqualTo(name100);
            assertThat(serviceName.value().length()).isEqualTo(100);
        }

        @Test
        @DisplayName("대문자가 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsUppercase() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of("AuthHub"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 허용됩니다");
        }

        @Test
        @DisplayName("하이픈으로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithHyphen() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of("-authhub"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 허용됩니다");
        }

        @Test
        @DisplayName("하이픈으로 끝나면 예외가 발생한다")
        void shouldThrowExceptionWhenEndsWithHyphen() {
            // when & then
            assertThatThrownBy(() -> ServiceName.of("authhub-"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 허용됩니다");
        }

        @ParameterizedTest
        @DisplayName("특수문자가 포함되면 예외가 발생한다")
        @ValueSource(strings = {"auth_hub", "auth.hub", "auth hub", "auth@hub", "auth#hub"})
        void shouldThrowExceptionWhenContainsSpecialChars(String invalidName) {
            // when & then
            assertThatThrownBy(() -> ServiceName.of(invalidName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 소문자, 숫자, 하이픈만 허용됩니다");
        }
    }

    @Nested
    @DisplayName("ServiceName 동등성 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 서비스 이름은 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            ServiceName name1 = ServiceName.of("authhub");
            ServiceName name2 = ServiceName.of("authhub");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 서비스 이름은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            ServiceName name1 = ServiceName.of("authhub");
            ServiceName name2 = ServiceName.of("product-service");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }
    }
}
