package com.ryuqq.authhub.domain.endpointpermission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** RequiredPermissions Value Object 단위 테스트 */
@Tag("unit")
@Tag("domain")
@Tag("vo")
@DisplayName("RequiredPermissions VO 단위 테스트")
class RequiredPermissionsTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTests {

        @Test
        @DisplayName("of() - Set으로 생성 성공")
        void of_WithSet_ShouldCreate() {
            // Given
            Set<String> permissions = Set.of("user:read", "user:write");

            // When
            RequiredPermissions result = RequiredPermissions.of(permissions);

            // Then
            assertThat(result.values()).containsExactlyInAnyOrder("user:read", "user:write");
        }

        @Test
        @DisplayName("of() - null은 빈 Set으로 처리")
        void of_WithNull_ShouldCreateEmpty() {
            // When
            RequiredPermissions result = RequiredPermissions.of(null);

            // Then
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.values()).isEmpty();
        }

        @Test
        @DisplayName("empty() - 빈 권한 목록 생성")
        void empty_ShouldCreateEmpty() {
            // When
            RequiredPermissions result = RequiredPermissions.empty();

            // Then
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("single() - 단일 권한으로 생성")
        void single_ShouldCreateWithSinglePermission() {
            // When
            RequiredPermissions result = RequiredPermissions.single("admin:all");

            // Then
            assertThat(result.values()).containsExactly("admin:all");
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("single() - null 권한은 예외 발생")
        void single_WithNull_ShouldThrowException() {
            assertThatThrownBy(() -> RequiredPermissions.single(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("권한은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("single() - 빈 문자열 권한은 예외 발생")
        void single_WithEmptyString_ShouldThrowException() {
            assertThatThrownBy(() -> RequiredPermissions.single("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("권한은 null이거나 빈 문자열일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTests {

        @Test
        @DisplayName("values()는 불변 Set 반환")
        void values_ShouldReturnImmutableSet() {
            // Given
            Set<String> original = Set.of("user:read", "user:write");
            RequiredPermissions permissions = RequiredPermissions.of(original);

            // Then
            assertThatThrownBy(() -> permissions.values().add("new:permission"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("contains 테스트")
    class ContainsTests {

        @Test
        @DisplayName("contains() - 포함된 권한 확인")
        void contains_ExistingPermission_ShouldReturnTrue() {
            // Given
            RequiredPermissions permissions =
                    RequiredPermissions.of(Set.of("user:read", "user:write"));

            // Then
            assertThat(permissions.contains("user:read")).isTrue();
            assertThat(permissions.contains("user:delete")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasAnyOf 테스트 (OR 조건)")
    class HasAnyOfTests {

        @Test
        @DisplayName("hasAnyOf() - 하나라도 만족하면 true")
        void hasAnyOf_WithOneMatching_ShouldReturnTrue() {
            // Given
            RequiredPermissions required =
                    RequiredPermissions.of(Set.of("admin:all", "user:manage"));
            Set<String> userPermissions = Set.of("user:read", "user:manage");

            // Then
            assertThat(required.hasAnyOf(userPermissions)).isTrue();
        }

        @Test
        @DisplayName("hasAnyOf() - 아무것도 만족하지 않으면 false")
        void hasAnyOf_WithNoMatching_ShouldReturnFalse() {
            // Given
            RequiredPermissions required =
                    RequiredPermissions.of(Set.of("admin:all", "super:admin"));
            Set<String> userPermissions = Set.of("user:read", "user:write");

            // Then
            assertThat(required.hasAnyOf(userPermissions)).isFalse();
        }

        @Test
        @DisplayName("hasAnyOf() - 빈 required는 항상 true")
        void hasAnyOf_EmptyRequired_ShouldReturnTrue() {
            // Given
            RequiredPermissions required = RequiredPermissions.empty();
            Set<String> userPermissions = Set.of("user:read");

            // Then
            assertThat(required.hasAnyOf(userPermissions)).isTrue();
        }

        @Test
        @DisplayName("hasAnyOf() - 빈 userPermissions면 false")
        void hasAnyOf_EmptyUserPermissions_ShouldReturnFalse() {
            // Given
            RequiredPermissions required = RequiredPermissions.of(Set.of("admin:all"));
            Set<String> userPermissions = Set.of();

            // Then
            assertThat(required.hasAnyOf(userPermissions)).isFalse();
        }

        @Test
        @DisplayName("hasAnyOf() - null userPermissions면 false")
        void hasAnyOf_NullUserPermissions_ShouldReturnFalse() {
            // Given
            RequiredPermissions required = RequiredPermissions.of(Set.of("admin:all"));

            // Then
            assertThat(required.hasAnyOf(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTests {

        @Test
        @DisplayName("동일한 권한을 가진 RequiredPermissions는 동등하다")
        void equals_SamePermissions_ShouldBeEqual() {
            // Given
            RequiredPermissions p1 = RequiredPermissions.of(Set.of("a", "b"));
            RequiredPermissions p2 = RequiredPermissions.of(Set.of("b", "a"));

            // Then
            assertThat(p1).isEqualTo(p2);
            assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        }

        @Test
        @DisplayName("다른 권한을 가진 RequiredPermissions는 동등하지 않다")
        void equals_DifferentPermissions_ShouldNotBeEqual() {
            // Given
            RequiredPermissions p1 = RequiredPermissions.of(Set.of("a", "b"));
            RequiredPermissions p2 = RequiredPermissions.of(Set.of("c", "d"));

            // Then
            assertThat(p1).isNotEqualTo(p2);
        }
    }
}
