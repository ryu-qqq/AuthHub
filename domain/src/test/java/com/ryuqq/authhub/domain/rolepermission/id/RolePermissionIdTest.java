package com.ryuqq.authhub.domain.rolepermission.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionId Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionId 테스트")
class RolePermissionIdTest {

    @Nested
    @DisplayName("RolePermissionId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            RolePermissionId rolePermissionId = RolePermissionId.of(1L);

            // then
            assertThat(rolePermissionId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            RolePermissionId rolePermissionId = new RolePermissionId(1L);

            // then
            assertThat(rolePermissionId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> RolePermissionId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }

        @Test
        @DisplayName("양수 값으로 생성할 수 있다")
        void shouldCreateWithPositiveValue() {
            // when
            RolePermissionId rolePermissionId = RolePermissionId.of(1L);

            // then
            assertThat(rolePermissionId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("큰 Long 값으로도 생성할 수 있다")
        void shouldCreateWithLargeValue() {
            // when
            RolePermissionId rolePermissionId = RolePermissionId.of(Long.MAX_VALUE);

            // then
            assertThat(rolePermissionId.value()).isEqualTo(Long.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("RolePermissionId equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 RolePermissionId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            RolePermissionId rolePermissionId1 = RolePermissionId.of(1L);
            RolePermissionId rolePermissionId2 = RolePermissionId.of(1L);

            // then
            assertThat(rolePermissionId1).isEqualTo(rolePermissionId2);
            assertThat(rolePermissionId1.hashCode()).isEqualTo(rolePermissionId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 RolePermissionId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            RolePermissionId rolePermissionId1 = RolePermissionId.of(1L);
            RolePermissionId rolePermissionId2 = RolePermissionId.of(2L);

            // then
            assertThat(rolePermissionId1).isNotEqualTo(rolePermissionId2);
        }
    }
}
