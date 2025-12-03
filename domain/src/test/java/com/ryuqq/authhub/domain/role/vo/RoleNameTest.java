package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("RoleName Value Object 테스트")
class RoleNameTest {

    @Nested
    @DisplayName("of() 메서드는")
    class OfMethod {

        @Test
        @DisplayName("올바른 형식의 전체 이름으로 생성할 수 있다")
        void shouldCreateWithValidFullName() {
            RoleName roleName = RoleName.of("ROLE_ADMIN");

            assertThat(roleName.value()).isEqualTo("ROLE_ADMIN");
            assertThat(roleName.nameWithoutPrefix()).isEqualTo("ADMIN");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 문자열은 예외를 던진다")
        void shouldThrowExceptionForNullOrEmptyValue(String value) {
            assertThatThrownBy(() -> RoleName.of(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"ADMIN", "admin", "Role_Admin", "ROLE_admin", "ROLE_", "USER"})
        @DisplayName("올바르지 않은 형식은 예외를 던진다")
        void shouldThrowExceptionForInvalidFormat(String value) {
            assertThatThrownBy(() -> RoleName.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ROLE_로 시작");
        }
    }

    @Nested
    @DisplayName("withPrefix() 메서드는")
    class WithPrefixMethod {

        @Test
        @DisplayName("ROLE_ prefix 없이 이름만으로 생성할 수 있다")
        void shouldCreateWithNameOnly() {
            RoleName roleName = RoleName.withPrefix("ADMIN");

            assertThat(roleName.value()).isEqualTo("ROLE_ADMIN");
            assertThat(roleName.nameWithoutPrefix()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("소문자 이름을 대문자로 변환한다")
        void shouldConvertToUpperCase() {
            RoleName roleName = RoleName.withPrefix("admin");

            assertThat(roleName.value()).isEqualTo("ROLE_ADMIN");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 문자열은 예외를 던진다")
        void shouldThrowExceptionForNullOrEmptyValue(String value) {
            assertThatThrownBy(() -> RoleName.withPrefix(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("같은 값을 가진 RoleName은 동등하다")
        void shouldBeEqualForSameValue() {
            RoleName roleName1 = RoleName.of("ROLE_ADMIN");
            RoleName roleName2 = RoleName.of("ROLE_ADMIN");

            assertThat(roleName1).isEqualTo(roleName2);
            assertThat(roleName1.hashCode()).isEqualTo(roleName2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 RoleName은 동등하지 않다")
        void shouldNotBeEqualForDifferentValue() {
            RoleName roleName1 = RoleName.of("ROLE_ADMIN");
            RoleName roleName2 = RoleName.of("ROLE_USER");

            assertThat(roleName1).isNotEqualTo(roleName2);
        }
    }
}
