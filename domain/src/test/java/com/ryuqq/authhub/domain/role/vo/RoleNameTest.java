package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleName 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleName 테스트")
class RoleNameTest {

    @Nested
    @DisplayName("of 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("유효한 역할 이름을 생성한다")
        void shouldCreateValidRoleName() {
            // when
            RoleName roleName = RoleName.of("ADMIN");

            // then
            assertThat(roleName).isNotNull();
            assertThat(roleName.value()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("소문자를 대문자로 변환한다")
        void shouldConvertToUpperCase() {
            // when
            RoleName roleName = RoleName.of("admin");

            // then
            assertThat(roleName.value()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("언더스코어가 포함된 이름을 허용한다")
        void shouldAllowUnderscore() {
            // when
            RoleName roleName = RoleName.of("SUPER_ADMIN");

            // then
            assertThat(roleName.value()).isEqualTo("SUPER_ADMIN");
        }

        @Test
        @DisplayName("숫자가 포함된 이름을 허용한다")
        void shouldAllowNumbers() {
            // when
            RoleName roleName = RoleName.of("ADMIN2");

            // then
            assertThat(roleName.value()).isEqualTo("ADMIN2");
        }

        @Test
        @DisplayName("공백을 트림한다")
        void shouldTrimWhitespace() {
            // when
            RoleName roleName = RoleName.of("  ADMIN  ");

            // then
            assertThat(roleName.value()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("null이면 예외 발생")
        void shouldThrowExceptionWhenNull() {
            assertThatThrownBy(() -> RoleName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외 발생")
        void shouldThrowExceptionWhenEmpty() {
            assertThatThrownBy(() -> RoleName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외 발생")
        void shouldThrowExceptionWhenOnlyWhitespace() {
            assertThatThrownBy(() -> RoleName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleName은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("50자 초과하면 예외 발생")
        void shouldThrowExceptionWhenTooLong() {
            String longName = "A".repeat(51);
            assertThatThrownBy(() -> RoleName.of(longName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자 이하여야 합니다");
        }

        @Test
        @DisplayName("숫자로 시작하면 예외 발생")
        void shouldThrowExceptionWhenStartsWithNumber() {
            assertThatThrownBy(() -> RoleName.of("1ADMIN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자로 시작");
        }

        @Test
        @DisplayName("특수문자가 포함되면 예외 발생")
        void shouldThrowExceptionWhenContainsSpecialCharacters() {
            assertThatThrownBy(() -> RoleName.of("ADMIN-ROLE"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자, 숫자, 언더스코어만 사용");
        }

        @Test
        @DisplayName("공백이 포함되면 예외 발생")
        void shouldThrowExceptionWhenContainsSpace() {
            assertThatThrownBy(() -> RoleName.of("ADMIN ROLE"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자, 숫자, 언더스코어만 사용");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 RoleName은 동일하다")
        void shouldBeEqualWhenSameValue() {
            // given
            RoleName name1 = RoleName.of("ADMIN");
            RoleName name2 = RoleName.of("ADMIN");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 RoleName은 다르다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            RoleName name1 = RoleName.of("ADMIN");
            RoleName name2 = RoleName.of("USER");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }

        @Test
        @DisplayName("대소문자 구분 없이 같으면 동일하다")
        void shouldBeEqualWhenSameValueIgnoreCase() {
            // given
            RoleName name1 = RoleName.of("admin");
            RoleName name2 = RoleName.of("ADMIN");

            // then
            assertThat(name1).isEqualTo(name2);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("RoleName의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            RoleName roleName = RoleName.of("ADMIN");

            // when
            String toString = roleName.toString();

            // then
            assertThat(toString).contains("RoleName");
            assertThat(toString).contains("ADMIN");
        }
    }
}
