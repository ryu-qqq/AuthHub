package com.ryuqq.authhub.domain.tenant.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantName 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantName 테스트")
class TenantNameTest {

    @Nested
    @DisplayName("TenantName 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of()로 TenantName을 생성한다")
        void shouldCreateWithOf() {
            // given
            String value = "Test Tenant";

            // when
            TenantName name = TenantName.of(value);

            // then
            assertThat(name.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("of()에 null을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenOfWithNull() {
            // when & then
            assertThatThrownBy(() -> TenantName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("of()에 빈 값을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenOfWithBlank() {
            // when & then
            assertThatThrownBy(() -> TenantName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null이거나 빈 문자열일 수 없습니다");

            assertThatThrownBy(() -> TenantName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("of()에 공백만 입력하면 예외가 발생한다 (trim 후 빈값)")
        void shouldThrowExceptionWhenOfWithOnlyWhitespace() {
            // when & then
            assertThatThrownBy(() -> TenantName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("TenantName 경계값 테스트")
    class BoundaryTests {

        @Test
        @DisplayName("1자 길이의 TenantName을 생성할 수 있다")
        void shouldCreateWithMinLength() {
            // given
            String value = "A";

            // when
            TenantName name = TenantName.of(value);

            // then
            assertThat(name.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("100자 길이의 TenantName을 생성할 수 있다")
        void shouldCreateWithMaxLength() {
            // given
            String value = "A".repeat(100);

            // when
            TenantName name = TenantName.of(value);

            // then
            assertThat(name.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("101자 길이의 TenantName은 예외가 발생한다")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given
            String value = "A".repeat(101);

            // when & then
            assertThatThrownBy(() -> TenantName.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 1자 이상 100자 이하여야 합니다");
        }
    }

    @Nested
    @DisplayName("TenantName 공백 처리 테스트")
    class WhitespaceTests {

        @Test
        @DisplayName("앞뒤 공백이 있는 값은 trim되어 저장된다")
        void shouldTrimWhitespace() {
            // given
            String value = "  Test Tenant  ";

            // when
            TenantName name = TenantName.of(value);

            // then
            assertThat(name.value()).isEqualTo("Test Tenant");
        }

        @Test
        @DisplayName("trim 후 빈 값이 되면 예외가 발생한다")
        void shouldThrowExceptionWhenTrimmedValueIsEmpty() {
            // when & then
            assertThatThrownBy(() -> TenantName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("TenantName 동등성 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 TenantName은 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            String value = "Test Tenant";
            TenantName name1 = TenantName.of(value);
            TenantName name2 = TenantName.of(value);

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 TenantName은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            TenantName name1 = TenantName.of("Test Tenant 1");
            TenantName name2 = TenantName.of("Test Tenant 2");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }

        @Test
        @DisplayName("null과 비교하면 동등하지 않다")
        void shouldNotBeEqualWhenComparedWithNull() {
            // given
            TenantName name = TenantName.of("Test Tenant");

            // then
            assertThat(name).isNotEqualTo(null);
        }
    }
}
