package com.ryuqq.authhub.domain.tenant.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantName VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantName 테스트")
class TenantNameTest {

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("유효한 이름으로 TenantName을 생성한다")
        void shouldCreateTenantNameWithValidValue() {
            // given
            String name = "Test Tenant";

            // when
            TenantName tenantName = TenantName.of(name);

            // then
            assertThat(tenantName).isNotNull();
            assertThat(tenantName.value()).isEqualTo("Test Tenant");
        }

        @Test
        @DisplayName("앞뒤 공백을 제거한다")
        void shouldTrimWhitespace() {
            // given
            String name = "  Trimmed Name  ";

            // when
            TenantName tenantName = TenantName.of(name);

            // then
            assertThat(tenantName.value()).isEqualTo("Trimmed Name");
        }

        @Test
        @DisplayName("최대 길이 100자까지 허용한다")
        void shouldAllowMaxLength() {
            // given
            String name = "A".repeat(100);

            // when
            TenantName tenantName = TenantName.of(name);

            // then
            assertThat(tenantName.value()).hasSize(100);
        }

        @Test
        @DisplayName("null 값으로 생성 시 예외 발생")
        void shouldThrowExceptionWhenValueIsNull() {
            assertThatThrownBy(() -> TenantName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenValueIsBlank() {
            assertThatThrownBy(() -> TenantName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null이거나 빈 문자열일 수 없습니다");

            assertThatThrownBy(() -> TenantName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("100자를 초과하면 예외 발생")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given
            String name = "A".repeat(101);

            // when/then
            assertThatThrownBy(() -> TenantName.of(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1자 이상 100자 이하");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 TenantName은 동일하다")
        void shouldBeEqualWhenSameValue() {
            // given
            TenantName name1 = TenantName.of("Test");
            TenantName name2 = TenantName.of("Test");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 TenantName은 다르다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            TenantName name1 = TenantName.of("Name1");
            TenantName name2 = TenantName.of("Name2");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            TenantName tenantName = TenantName.of("Self");

            // then
            assertThat(tenantName).isEqualTo(tenantName);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            TenantName tenantName = TenantName.of("Test");

            // then
            assertThat(tenantName).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 같지 않다")
        void shouldNotBeEqualToDifferentType() {
            // given
            TenantName tenantName = TenantName.of("Test");

            // then
            assertThat(tenantName).isNotEqualTo("Test");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("TenantName의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            TenantName tenantName = TenantName.of("Test Tenant");

            // when
            String toString = tenantName.toString();

            // then
            assertThat(toString).contains("TenantName");
            assertThat(toString).contains("Test Tenant");
        }
    }
}
