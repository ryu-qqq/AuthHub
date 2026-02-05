package com.ryuqq.authhub.domain.service.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceCode Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceCode 테스트")
class ServiceCodeTest {

    @Nested
    @DisplayName("ServiceCode 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            ServiceCode serviceCode = ServiceCode.of("SVC_STORE");

            // then
            assertThat(serviceCode.value()).isEqualTo("SVC_STORE");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            ServiceCode serviceCode = new ServiceCode("SVC_B2B");

            // then
            assertThat(serviceCode.value()).isEqualTo("SVC_B2B");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> ServiceCode.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> ServiceCode.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> ServiceCode.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("50자 초과이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueExceedsMaxLength() {
            // given
            String longValue = "A".repeat(51);

            // when & then
            assertThatThrownBy(() -> ServiceCode.of(longValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자 이하여야 합니다");
        }

        @Test
        @DisplayName("50자까지는 생성할 수 있다")
        void shouldCreateWithMaxLength() {
            // given
            String maxLengthValue = "A".repeat(50);

            // when
            ServiceCode serviceCode = ServiceCode.of(maxLengthValue);

            // then
            assertThat(serviceCode.value()).isEqualTo(maxLengthValue);
        }

        @Test
        @DisplayName("소문자는 대문자로 변환된다")
        void shouldConvertLowerCaseToUpperCase() {
            // when
            ServiceCode serviceCode = ServiceCode.of("svc_store");

            // then
            assertThat(serviceCode.value()).isEqualTo("SVC_STORE");
        }

        @Test
        @DisplayName("앞뒤 공백은 제거된다")
        void shouldTrimWhitespace() {
            // when
            ServiceCode serviceCode = ServiceCode.of("  SVC_STORE  ");

            // then
            assertThat(serviceCode.value()).isEqualTo("SVC_STORE");
        }

        @Test
        @DisplayName("대소문자 혼합은 대문자로 변환된다")
        void shouldConvertMixedCaseToUpperCase() {
            // when
            ServiceCode serviceCode = ServiceCode.of("Svc_Store");

            // then
            assertThat(serviceCode.value()).isEqualTo("SVC_STORE");
        }
    }

    @Nested
    @DisplayName("ServiceCode fromNullable 테스트")
    class FromNullableTests {

        @Test
        @DisplayName("null 입력 시 null을 반환한다")
        void shouldReturnNullWhenValueIsNull() {
            // when
            ServiceCode result = ServiceCode.fromNullable(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 문자열 입력 시 null을 반환한다")
        void shouldReturnNullWhenValueIsBlank() {
            // when
            ServiceCode result = ServiceCode.fromNullable("");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("공백만 있으면 null을 반환한다")
        void shouldReturnNullWhenValueIsWhitespace() {
            // when
            ServiceCode result = ServiceCode.fromNullable("   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("유효한 값이면 ServiceCode를 반환한다")
        void shouldReturnServiceCodeWhenValueIsValid() {
            // when
            ServiceCode result = ServiceCode.fromNullable("SVC_STORE");

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo("SVC_STORE");
        }

        @Test
        @DisplayName("유효한 값은 대문자로 변환되어 반환된다")
        void shouldReturnUpperCaseWhenValueIsValid() {
            // when
            ServiceCode result = ServiceCode.fromNullable("svc_store");

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo("SVC_STORE");
        }
    }

    @Nested
    @DisplayName("ServiceCode equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 ServiceCode는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            ServiceCode serviceCode1 = ServiceCode.of("SVC_STORE");
            ServiceCode serviceCode2 = ServiceCode.of("SVC_STORE");

            // then
            assertThat(serviceCode1).isEqualTo(serviceCode2);
            assertThat(serviceCode1.hashCode()).isEqualTo(serviceCode2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ServiceCode는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            ServiceCode serviceCode1 = ServiceCode.of("SVC_STORE");
            ServiceCode serviceCode2 = ServiceCode.of("SVC_B2B");

            // then
            assertThat(serviceCode1).isNotEqualTo(serviceCode2);
        }

        @Test
        @DisplayName("대소문자만 다른 경우에도 동등하다 (대문자로 정규화됨)")
        void shouldBeEqualWhenOnlyCaseDiffers() {
            // given
            ServiceCode serviceCode1 = ServiceCode.of("SVC_STORE");
            ServiceCode serviceCode2 = ServiceCode.of("svc_store");

            // then
            assertThat(serviceCode1).isEqualTo(serviceCode2);
            assertThat(serviceCode1.hashCode()).isEqualTo(serviceCode2.hashCode());
        }
    }
}
