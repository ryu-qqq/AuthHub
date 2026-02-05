package com.ryuqq.authhub.domain.tenantservice.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceId Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceId 테스트")
class TenantServiceIdTest {

    @Nested
    @DisplayName("TenantServiceId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            TenantServiceId tenantServiceId = TenantServiceId.of(1L);

            // then
            assertThat(tenantServiceId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            TenantServiceId tenantServiceId = new TenantServiceId(1L);

            // then
            assertThat(tenantServiceId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> TenantServiceId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }

        @Test
        @DisplayName("0 이하면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsZeroOrNegative() {
            // when & then
            assertThatThrownBy(() -> TenantServiceId.of(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");

            assertThatThrownBy(() -> TenantServiceId.of(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("양수 값으로 생성할 수 있다")
        void shouldCreateWithPositiveValue() {
            // when
            TenantServiceId tenantServiceId = TenantServiceId.of(1L);

            // then
            assertThat(tenantServiceId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("큰 Long 값으로도 생성할 수 있다")
        void shouldCreateWithLargeValue() {
            // when
            TenantServiceId tenantServiceId = TenantServiceId.of(Long.MAX_VALUE);

            // then
            assertThat(tenantServiceId.value()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("fromNullable(null)이면 null을 반환한다")
        void fromNullableShouldReturnNullWhenNull() {
            // when
            TenantServiceId result = TenantServiceId.fromNullable(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("fromNullable(value)이면 TenantServiceId를 반환한다")
        void fromNullableShouldReturnTenantServiceIdWhenValueExists() {
            // when
            TenantServiceId result = TenantServiceId.fromNullable(42L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo(42L);
        }
    }

    @Nested
    @DisplayName("TenantServiceId equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 TenantServiceId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            TenantServiceId id1 = TenantServiceId.of(1L);
            TenantServiceId id2 = TenantServiceId.of(1L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 TenantServiceId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            TenantServiceId id1 = TenantServiceId.of(1L);
            TenantServiceId id2 = TenantServiceId.of(2L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
