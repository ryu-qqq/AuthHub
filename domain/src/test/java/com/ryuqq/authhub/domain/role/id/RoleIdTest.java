package com.ryuqq.authhub.domain.role.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleId Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleId 테스트")
class RoleIdTest {

    @Nested
    @DisplayName("RoleId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            RoleId roleId = RoleId.of(1L);

            // then
            assertThat(roleId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            RoleId roleId = new RoleId(1L);

            // then
            assertThat(roleId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> RoleId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }

        @Test
        @DisplayName("0 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsZero() {
            // when & then
            assertThatThrownBy(() -> RoleId.of(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("음수 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNegative() {
            // when & then
            assertThatThrownBy(() -> RoleId.of(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("양수 값으로 생성할 수 있다")
        void shouldCreateWithPositiveValue() {
            // when
            RoleId roleId = RoleId.of(1L);

            // then
            assertThat(roleId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("큰 Long 값으로도 생성할 수 있다")
        void shouldCreateWithLargeValue() {
            // when
            RoleId roleId = RoleId.of(Long.MAX_VALUE);

            // then
            assertThat(roleId.value()).isEqualTo(Long.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("RoleId equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 RoleId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            RoleId roleId1 = RoleId.of(1L);
            RoleId roleId2 = RoleId.of(1L);

            // then
            assertThat(roleId1).isEqualTo(roleId2);
            assertThat(roleId1.hashCode()).isEqualTo(roleId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 RoleId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            RoleId roleId1 = RoleId.of(1L);
            RoleId roleId2 = RoleId.of(2L);

            // then
            assertThat(roleId1).isNotEqualTo(roleId2);
        }

        @Test
        @DisplayName("null과 비교하면 false를 반환한다")
        void shouldNotBeEqualWhenComparedWithNull() {
            // given
            RoleId roleId = RoleId.of(1L);

            // then
            assertThat(roleId).isNotEqualTo(null);
        }
    }
}
