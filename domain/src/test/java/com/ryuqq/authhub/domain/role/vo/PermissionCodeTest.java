package com.ryuqq.authhub.domain.role.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("PermissionCode Value Object 테스트")
class PermissionCodeTest {

    @Nested
    @DisplayName("of(String) 메서드는")
    class OfStringMethod {

        @Test
        @DisplayName("올바른 형식의 권한 코드를 생성할 수 있다")
        void shouldCreateWithValidCode() {
            PermissionCode permissionCode = PermissionCode.of("user:read");

            assertThat(permissionCode.value()).isEqualTo("user:read");
            assertThat(permissionCode.resource()).isEqualTo("user");
            assertThat(permissionCode.action()).isEqualTo("read");
        }

        @Test
        @DisplayName("대문자를 소문자로 변환한다")
        void shouldConvertToLowerCase() {
            PermissionCode permissionCode = PermissionCode.of("USER:READ");

            assertThat(permissionCode.value()).isEqualTo("user:read");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 문자열은 예외를 던진다")
        void shouldThrowExceptionForNullOrEmptyValue(String value) {
            assertThatThrownBy(() -> PermissionCode.of(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"user", "user:", ":read", "user-read", "user.read"})
        @DisplayName("올바르지 않은 형식은 예외를 던진다")
        void shouldThrowExceptionForInvalidFormat(String value) {
            assertThatThrownBy(() -> PermissionCode.of(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("resource:action");
        }
    }

    @Nested
    @DisplayName("of(String, String) 메서드는")
    class OfResourceActionMethod {

        @Test
        @DisplayName("리소스와 액션을 조합하여 생성할 수 있다")
        void shouldCreateWithResourceAndAction() {
            PermissionCode permissionCode = PermissionCode.of("user", "write");

            assertThat(permissionCode.value()).isEqualTo("user:write");
            assertThat(permissionCode.resource()).isEqualTo("user");
            assertThat(permissionCode.action()).isEqualTo("write");
        }
    }

    @Nested
    @DisplayName("implies() 메서드는")
    class ImpliesMethod {

        @Test
        @DisplayName("동일한 권한은 true를 반환한다")
        void shouldReturnTrueForSamePermission() {
            PermissionCode permissionCode = PermissionCode.of("user:read");

            assertThat(permissionCode.implies(PermissionCode.of("user:read"))).isTrue();
        }

        @Test
        @DisplayName("액션 와일드카드는 모든 액션을 포함한다")
        void shouldImplyAllActionsWithWildcard() {
            PermissionCode wildcardPermission = PermissionCode.of("user:*");

            assertThat(wildcardPermission.implies(PermissionCode.of("user:read"))).isTrue();
            assertThat(wildcardPermission.implies(PermissionCode.of("user:write"))).isTrue();
            assertThat(wildcardPermission.implies(PermissionCode.of("user:delete"))).isTrue();
        }

        @Test
        @DisplayName("다른 리소스는 포함하지 않는다")
        void shouldNotImplyDifferentResource() {
            PermissionCode permissionCode = PermissionCode.of("user:*");

            assertThat(permissionCode.implies(PermissionCode.of("organization:read"))).isFalse();
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("같은 값을 가진 PermissionCode는 동등하다")
        void shouldBeEqualForSameValue() {
            PermissionCode code1 = PermissionCode.of("user:read");
            PermissionCode code2 = PermissionCode.of("user:read");

            assertThat(code1).isEqualTo(code2);
            assertThat(code1.hashCode()).isEqualTo(code2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 PermissionCode는 동등하지 않다")
        void shouldNotBeEqualForDifferentValue() {
            PermissionCode code1 = PermissionCode.of("user:read");
            PermissionCode code2 = PermissionCode.of("user:write");

            assertThat(code1).isNotEqualTo(code2);
        }
    }
}
