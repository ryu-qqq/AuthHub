package com.ryuqq.authhub.domain.service.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ServiceId Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ServiceId 테스트")
class ServiceIdTest {

    @Nested
    @DisplayName("ServiceId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            ServiceId serviceId = ServiceId.of(1L);

            // then
            assertThat(serviceId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            ServiceId serviceId = new ServiceId(1L);

            // then
            assertThat(serviceId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> ServiceId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }

        @Test
        @DisplayName("0 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsZero() {
            // when & then
            assertThatThrownBy(() -> ServiceId.of(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("음수 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNegative() {
            // when & then
            assertThatThrownBy(() -> ServiceId.of(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("양수 값으로 생성할 수 있다")
        void shouldCreateWithPositiveValue() {
            // when
            ServiceId serviceId = ServiceId.of(1L);

            // then
            assertThat(serviceId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("큰 Long 값으로도 생성할 수 있다")
        void shouldCreateWithLargeValue() {
            // when
            ServiceId serviceId = ServiceId.of(Long.MAX_VALUE);

            // then
            assertThat(serviceId.value()).isEqualTo(Long.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("ServiceId fromNullable 테스트")
    class FromNullableTests {

        @Test
        @DisplayName("null 입력 시 null을 반환한다")
        void shouldReturnNullWhenValueIsNull() {
            // when
            ServiceId result = ServiceId.fromNullable(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("유효한 값이면 ServiceId를 반환한다")
        void shouldReturnServiceIdWhenValueIsValid() {
            // when
            ServiceId result = ServiceId.fromNullable(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("0 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsZero() {
            // when & then
            assertThatThrownBy(() -> ServiceId.fromNullable(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("음수 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNegative() {
            // when & then
            assertThatThrownBy(() -> ServiceId.fromNullable(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }
    }

    @Nested
    @DisplayName("ServiceId equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 ServiceId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            ServiceId serviceId1 = ServiceId.of(1L);
            ServiceId serviceId2 = ServiceId.of(1L);

            // then
            assertThat(serviceId1).isEqualTo(serviceId2);
            assertThat(serviceId1.hashCode()).isEqualTo(serviceId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ServiceId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            ServiceId serviceId1 = ServiceId.of(1L);
            ServiceId serviceId2 = ServiceId.of(2L);

            // then
            assertThat(serviceId1).isNotEqualTo(serviceId2);
        }

        @Test
        @DisplayName("null과 비교하면 false를 반환한다")
        void shouldNotBeEqualWhenComparedWithNull() {
            // given
            ServiceId serviceId = ServiceId.of(1L);

            // then
            assertThat(serviceId).isNotEqualTo(null);
        }
    }
}
