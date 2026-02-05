package com.ryuqq.authhub.domain.userrole.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleId Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleId 테스트")
class UserRoleIdTest {

    @Nested
    @DisplayName("UserRoleId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            UserRoleId userRoleId = UserRoleId.of(1L);

            // then
            assertThat(userRoleId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("생성자로 직접 생성할 수 있다")
        void shouldCreateViaConstructor() {
            // when
            UserRoleId userRoleId = new UserRoleId(1L);

            // then
            assertThat(userRoleId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> UserRoleId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }

        @Test
        @DisplayName("양수 값으로 생성할 수 있다")
        void shouldCreateWithPositiveValue() {
            // when
            UserRoleId userRoleId = UserRoleId.of(1L);

            // then
            assertThat(userRoleId.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("큰 Long 값으로도 생성할 수 있다")
        void shouldCreateWithLargeValue() {
            // when
            UserRoleId userRoleId = UserRoleId.of(Long.MAX_VALUE);

            // then
            assertThat(userRoleId.value()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("0 값으로도 생성할 수 있다")
        void shouldCreateWithZeroValue() {
            // when
            UserRoleId userRoleId = UserRoleId.of(0L);

            // then
            assertThat(userRoleId.value()).isEqualTo(0L);
        }

        @Test
        @DisplayName("음수 값으로도 생성할 수 있다")
        void shouldCreateWithNegativeValue() {
            // when
            UserRoleId userRoleId = UserRoleId.of(-1L);

            // then
            assertThat(userRoleId.value()).isEqualTo(-1L);
        }
    }

    @Nested
    @DisplayName("UserRoleId equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 UserRoleId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            UserRoleId userRoleId1 = UserRoleId.of(1L);
            UserRoleId userRoleId2 = UserRoleId.of(1L);

            // then
            assertThat(userRoleId1).isEqualTo(userRoleId2);
            assertThat(userRoleId1.hashCode()).isEqualTo(userRoleId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 UserRoleId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            UserRoleId userRoleId1 = UserRoleId.of(1L);
            UserRoleId userRoleId2 = UserRoleId.of(2L);

            // then
            assertThat(userRoleId1).isNotEqualTo(userRoleId2);
        }

        @Test
        @DisplayName("null과 비교하면 false를 반환한다")
        void shouldNotBeEqualWhenComparedWithNull() {
            // given
            UserRoleId userRoleId = UserRoleId.of(1L);

            // then
            assertThat(userRoleId).isNotEqualTo(null);
        }
    }
}
