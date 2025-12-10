package com.ryuqq.authhub.domain.organization.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationName VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationName 테스트")
class OrganizationNameTest {

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("유효한 이름으로 OrganizationName을 생성한다")
        void shouldCreateOrganizationNameWithValidValue() {
            // given
            String name = "Test Organization";

            // when
            OrganizationName organizationName = OrganizationName.of(name);

            // then
            assertThat(organizationName).isNotNull();
            assertThat(organizationName.value()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("앞뒤 공백을 제거한다")
        void shouldTrimWhitespace() {
            // given
            String name = "  Trimmed Name  ";

            // when
            OrganizationName organizationName = OrganizationName.of(name);

            // then
            assertThat(organizationName.value()).isEqualTo("Trimmed Name");
        }

        @Test
        @DisplayName("최대 길이 100자까지 허용한다")
        void shouldAllowMaxLength() {
            // given
            String name = "A".repeat(100);

            // when
            OrganizationName organizationName = OrganizationName.of(name);

            // then
            assertThat(organizationName.value()).hasSize(100);
        }

        @Test
        @DisplayName("null 값으로 생성 시 예외 발생")
        void shouldThrowExceptionWhenValueIsNull() {
            assertThatThrownBy(() -> OrganizationName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenValueIsBlank() {
            assertThatThrownBy(() -> OrganizationName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 null이거나 빈 문자열일 수 없습니다");

            assertThatThrownBy(() -> OrganizationName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("100자를 초과하면 예외 발생")
        void shouldThrowExceptionWhenExceedsMaxLength() {
            // given
            String name = "A".repeat(101);

            // when/then
            assertThatThrownBy(() -> OrganizationName.of(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1자 이상 100자 이하");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 OrganizationName은 동일하다")
        void shouldBeEqualWhenSameValue() {
            // given
            OrganizationName name1 = OrganizationName.of("Test");
            OrganizationName name2 = OrganizationName.of("Test");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 OrganizationName은 다르다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            OrganizationName name1 = OrganizationName.of("Name1");
            OrganizationName name2 = OrganizationName.of("Name2");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            OrganizationName organizationName = OrganizationName.of("Self");

            // then
            assertThat(organizationName).isEqualTo(organizationName);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            OrganizationName organizationName = OrganizationName.of("Test");

            // then
            assertThat(organizationName).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 같지 않다")
        void shouldNotBeEqualToDifferentType() {
            // given
            OrganizationName organizationName = OrganizationName.of("Test");

            // then
            assertThat(organizationName).isNotEqualTo("Test");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("OrganizationName의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            OrganizationName organizationName = OrganizationName.of("Test Organization");

            // when
            String toString = organizationName.toString();

            // then
            assertThat(toString).contains("OrganizationName");
            assertThat(toString).contains("Test Organization");
        }
    }
}
