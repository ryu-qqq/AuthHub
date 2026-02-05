package com.ryuqq.authhub.domain.permissionendpoint.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionEndpointId Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionEndpointId Value Object 테스트")
class PermissionEndpointIdTest {

    @Nested
    @DisplayName("PermissionEndpointId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("정상적인 ID 값으로 생성한다")
        void shouldCreateWithValidId() {
            // when
            PermissionEndpointId id = PermissionEndpointId.of(1L);

            // then
            assertThat(id.value()).isEqualTo(1L);
        }

        @Test
        @DisplayName("큰 ID 값으로 생성한다")
        void shouldCreateWithLargeId() {
            // when
            PermissionEndpointId id = PermissionEndpointId.of(Long.MAX_VALUE);

            // then
            assertThat(id.value()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> PermissionEndpointId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }

        @Test
        @DisplayName("0 이하 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsZero() {
            // when & then
            assertThatThrownBy(() -> PermissionEndpointId.of(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }

        @Test
        @DisplayName("음수 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNegative() {
            // when & then
            assertThatThrownBy(() -> PermissionEndpointId.of(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("0보다 커야 합니다");
        }
    }

    @Nested
    @DisplayName("PermissionEndpointId 동등성 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 ID는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            PermissionEndpointId id1 = PermissionEndpointId.of(1L);
            PermissionEndpointId id2 = PermissionEndpointId.of(1L);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 ID는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            PermissionEndpointId id1 = PermissionEndpointId.of(1L);
            PermissionEndpointId id2 = PermissionEndpointId.of(2L);

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
