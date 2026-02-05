package com.ryuqq.authhub.domain.permission.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionId Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionId 테스트")
class PermissionIdTest {

    @Nested
    @DisplayName("PermissionId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            PermissionId permissionId = PermissionId.of(1L);

            // then
            assertThat(permissionId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            PermissionId permissionId = new PermissionId(1L);

            // then
            assertThat(permissionId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> PermissionId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }

        @Test
        @DisplayName("0 이하 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsZero() {
            // when & then
            assertThatThrownBy(() -> PermissionId.of(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("음수 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNegative() {
            // when & then
            assertThatThrownBy(() -> PermissionId.of(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("양수 값으로 생성할 수 있다")
        void shouldCreateWithPositiveValue() {
            // when
            PermissionId permissionId = PermissionId.of(1L);

            // then
            assertThat(permissionId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("큰 Long 값으로도 생성할 수 있다")
        void shouldCreateWithLargeValue() {
            // when
            PermissionId permissionId = PermissionId.of(Long.MAX_VALUE);

            // then
            assertThat(permissionId.value()).isEqualTo(Long.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("PermissionId equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 PermissionId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            PermissionId permissionId1 = PermissionId.of(1L);
            PermissionId permissionId2 = PermissionId.of(1L);

            // then
            assertThat(permissionId1).isEqualTo(permissionId2);
            assertThat(permissionId1.hashCode()).isEqualTo(permissionId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 PermissionId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            PermissionId permissionId1 = PermissionId.of(1L);
            PermissionId permissionId2 = PermissionId.of(2L);

            // then
            assertThat(permissionId1).isNotEqualTo(permissionId2);
        }
    }
}
