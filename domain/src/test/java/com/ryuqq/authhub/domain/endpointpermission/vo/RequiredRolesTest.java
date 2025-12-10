package com.ryuqq.authhub.domain.endpointpermission.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** RequiredRoles Value Object 단위 테스트 */
@Tag("unit")
@Tag("domain")
@Tag("vo")
@DisplayName("RequiredRoles VO 단위 테스트")
class RequiredRolesTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTests {

        @Test
        @DisplayName("of() - Set으로 생성 성공")
        void of_WithSet_ShouldCreate() {
            // Given
            Set<String> roles = Set.of("ADMIN", "USER");

            // When
            RequiredRoles result = RequiredRoles.of(roles);

            // Then
            assertThat(result.values()).containsExactlyInAnyOrder("ADMIN", "USER");
        }

        @Test
        @DisplayName("of() - null은 빈 Set으로 처리")
        void of_WithNull_ShouldCreateEmpty() {
            // When
            RequiredRoles result = RequiredRoles.of(null);

            // Then
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.values()).isEmpty();
        }

        @Test
        @DisplayName("empty() - 빈 역할 목록 생성")
        void empty_ShouldCreateEmpty() {
            // When
            RequiredRoles result = RequiredRoles.empty();

            // Then
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("single() - 단일 역할로 생성")
        void single_ShouldCreateWithSingleRole() {
            // When
            RequiredRoles result = RequiredRoles.single("ADMIN");

            // Then
            assertThat(result.values()).containsExactly("ADMIN");
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("single() - null 역할은 예외 발생")
        void single_WithNull_ShouldThrowException() {
            assertThatThrownBy(() -> RequiredRoles.single(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("역할은 null이거나 빈 문자열일 수 없습니다");
        }

        @Test
        @DisplayName("single() - 빈 문자열 역할은 예외 발생")
        void single_WithEmptyString_ShouldThrowException() {
            assertThatThrownBy(() -> RequiredRoles.single("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("역할은 null이거나 빈 문자열일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTests {

        @Test
        @DisplayName("values()는 불변 Set 반환")
        void values_ShouldReturnImmutableSet() {
            // Given
            Set<String> original = Set.of("ADMIN", "USER");
            RequiredRoles roles = RequiredRoles.of(original);

            // Then
            assertThatThrownBy(() -> roles.values().add("NEW_ROLE"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("contains 테스트")
    class ContainsTests {

        @Test
        @DisplayName("contains() - 포함된 역할 확인")
        void contains_ExistingRole_ShouldReturnTrue() {
            // Given
            RequiredRoles roles = RequiredRoles.of(Set.of("ADMIN", "USER"));

            // Then
            assertThat(roles.contains("ADMIN")).isTrue();
            assertThat(roles.contains("MODERATOR")).isFalse();
        }
    }

    @Nested
    @DisplayName("hasAnyOf 테스트 (OR 조건)")
    class HasAnyOfTests {

        @Test
        @DisplayName("hasAnyOf() - 하나라도 만족하면 true")
        void hasAnyOf_WithOneMatching_ShouldReturnTrue() {
            // Given
            RequiredRoles required = RequiredRoles.of(Set.of("ADMIN", "SUPER_ADMIN"));
            Set<String> userRoles = Set.of("USER", "ADMIN");

            // Then
            assertThat(required.hasAnyOf(userRoles)).isTrue();
        }

        @Test
        @DisplayName("hasAnyOf() - 아무것도 만족하지 않으면 false")
        void hasAnyOf_WithNoMatching_ShouldReturnFalse() {
            // Given
            RequiredRoles required = RequiredRoles.of(Set.of("ADMIN", "SUPER_ADMIN"));
            Set<String> userRoles = Set.of("USER", "GUEST");

            // Then
            assertThat(required.hasAnyOf(userRoles)).isFalse();
        }

        @Test
        @DisplayName("hasAnyOf() - 빈 required는 항상 true")
        void hasAnyOf_EmptyRequired_ShouldReturnTrue() {
            // Given
            RequiredRoles required = RequiredRoles.empty();
            Set<String> userRoles = Set.of("USER");

            // Then
            assertThat(required.hasAnyOf(userRoles)).isTrue();
        }

        @Test
        @DisplayName("hasAnyOf() - 빈 userRoles면 false")
        void hasAnyOf_EmptyUserRoles_ShouldReturnFalse() {
            // Given
            RequiredRoles required = RequiredRoles.of(Set.of("ADMIN"));
            Set<String> userRoles = Set.of();

            // Then
            assertThat(required.hasAnyOf(userRoles)).isFalse();
        }

        @Test
        @DisplayName("hasAnyOf() - null userRoles면 false")
        void hasAnyOf_NullUserRoles_ShouldReturnFalse() {
            // Given
            RequiredRoles required = RequiredRoles.of(Set.of("ADMIN"));

            // Then
            assertThat(required.hasAnyOf(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTests {

        @Test
        @DisplayName("동일한 역할을 가진 RequiredRoles는 동등하다")
        void equals_SameRoles_ShouldBeEqual() {
            // Given
            RequiredRoles r1 = RequiredRoles.of(Set.of("ADMIN", "USER"));
            RequiredRoles r2 = RequiredRoles.of(Set.of("USER", "ADMIN"));

            // Then
            assertThat(r1).isEqualTo(r2);
            assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
        }

        @Test
        @DisplayName("다른 역할을 가진 RequiredRoles는 동등하지 않다")
        void equals_DifferentRoles_ShouldNotBeEqual() {
            // Given
            RequiredRoles r1 = RequiredRoles.of(Set.of("ADMIN", "USER"));
            RequiredRoles r2 = RequiredRoles.of(Set.of("GUEST", "MODERATOR"));

            // Then
            assertThat(r1).isNotEqualTo(r2);
        }
    }
}
