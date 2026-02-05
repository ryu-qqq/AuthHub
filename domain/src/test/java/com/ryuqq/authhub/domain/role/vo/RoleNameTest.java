package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleName Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleName 테스트")
class RoleNameTest {

    @Nested
    @DisplayName("RoleName 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            RoleName roleName = RoleName.of("SUPER_ADMIN");

            // then
            assertThat(roleName.value()).isEqualTo("SUPER_ADMIN");
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            RoleName roleName = new RoleName("SUPER_ADMIN");

            // then
            assertThat(roleName.value()).isEqualTo("SUPER_ADMIN");
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> RoleName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> RoleName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> RoleName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("대문자로 시작하는 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPattern() {
            // when
            RoleName roleName1 = RoleName.of("SUPER_ADMIN");
            RoleName roleName2 = RoleName.of("TENANT_ADMIN");
            RoleName roleName3 = RoleName.of("USER_MANAGER");
            RoleName roleName4 = RoleName.of("VIEWER");

            // then
            assertThat(roleName1.value()).isEqualTo("SUPER_ADMIN");
            assertThat(roleName2.value()).isEqualTo("TENANT_ADMIN");
            assertThat(roleName3.value()).isEqualTo("USER_MANAGER");
            assertThat(roleName4.value()).isEqualTo("VIEWER");
        }

        @Test
        @DisplayName("숫자가 포함된 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPatternIncludingNumbers() {
            // when
            RoleName roleName = RoleName.of("ROLE_123");

            // then
            assertThat(roleName.value()).isEqualTo("ROLE_123");
        }

        @Test
        @DisplayName("언더스코어가 포함된 유효한 값으로 생성할 수 있다")
        void shouldCreateWithValidPatternIncludingUnderscore() {
            // when
            RoleName roleName = RoleName.of("SUPER_ADMIN_ROLE");

            // then
            assertThat(roleName.value()).isEqualTo("SUPER_ADMIN_ROLE");
        }

        @Test
        @DisplayName("소문자로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithLowerCase() {
            // when & then
            assertThatThrownBy(() -> RoleName.of("super_admin"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자로 시작하고 대문자, 숫자, 언더스코어만 포함해야 합니다");
        }

        @Test
        @DisplayName("숫자로 시작하면 예외가 발생한다")
        void shouldThrowExceptionWhenStartsWithNumber() {
            // when & then
            assertThatThrownBy(() -> RoleName.of("1ROLE"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자로 시작하고 대문자, 숫자, 언더스코어만 포함해야 합니다");
        }

        @Test
        @DisplayName("하이픈이 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsHyphen() {
            // when & then
            assertThatThrownBy(() -> RoleName.of("SUPER-ADMIN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자로 시작하고 대문자, 숫자, 언더스코어만 포함해야 합니다");
        }

        @Test
        @DisplayName("공백이 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsWhitespace() {
            // when & then
            assertThatThrownBy(() -> RoleName.of("SUPER ADMIN"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자로 시작하고 대문자, 숫자, 언더스코어만 포함해야 합니다");
        }

        @Test
        @DisplayName("소문자가 포함되면 예외가 발생한다")
        void shouldThrowExceptionWhenContainsLowerCase() {
            // when & then
            assertThatThrownBy(() -> RoleName.of("SUPER_Admin"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("영문 대문자로 시작하고 대문자, 숫자, 언더스코어만 포함해야 합니다");
        }
    }

    @Nested
    @DisplayName("RoleName fromNullable 테스트")
    class FromNullableTests {

        @Test
        @DisplayName("null 입력 시 null을 반환한다")
        void shouldReturnNullWhenValueIsNull() {
            // when
            RoleName result = RoleName.fromNullable(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 문자열 입력 시 null을 반환한다")
        void shouldReturnNullWhenValueIsBlank() {
            // when
            RoleName result = RoleName.fromNullable("");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("공백만 있으면 null을 반환한다")
        void shouldReturnNullWhenValueIsWhitespace() {
            // when
            RoleName result = RoleName.fromNullable("   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("유효한 값이면 RoleName을 반환한다")
        void shouldReturnRoleNameWhenValueIsValid() {
            // when
            RoleName result = RoleName.fromNullable("SUPER_ADMIN");

            // then
            assertThat(result).isNotNull();
            assertThat(result.value()).isEqualTo("SUPER_ADMIN");
        }
    }

    @Nested
    @DisplayName("RoleName equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 RoleName은 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            RoleName roleName1 = RoleName.of("SUPER_ADMIN");
            RoleName roleName2 = RoleName.of("SUPER_ADMIN");

            // then
            assertThat(roleName1).isEqualTo(roleName2);
            assertThat(roleName1.hashCode()).isEqualTo(roleName2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 RoleName은 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            RoleName roleName1 = RoleName.of("SUPER_ADMIN");
            RoleName roleName2 = RoleName.of("TENANT_ADMIN");

            // then
            assertThat(roleName1).isNotEqualTo(roleName2);
        }
    }
}
