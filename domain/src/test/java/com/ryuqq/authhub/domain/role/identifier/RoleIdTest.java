package com.ryuqq.authhub.domain.role.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RoleId Identifier 테스트")
class RoleIdTest {

    @Nested
    @DisplayName("of() 메서드는")
    class OfMethod {

        @Test
        @DisplayName("유효한 Long 값으로 생성할 수 있다")
        void shouldCreateWithValidValue() {
            RoleId roleId = RoleId.of(1L);

            assertThat(roleId.value()).isEqualTo(1L);
            assertThat(roleId.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값은 예외를 던진다")
        void shouldThrowExceptionForNullValue() {
            assertThatThrownBy(() -> RoleId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("0 이하의 값은 예외를 던진다")
        void shouldThrowExceptionForNonPositiveValue() {
            assertThatThrownBy(() -> RoleId.of(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("양수");

            assertThatThrownBy(() -> RoleId.of(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("양수");
        }
    }

    @Nested
    @DisplayName("forNew() 메서드는")
    class ForNewMethod {

        @Test
        @DisplayName("새로운 엔티티용 RoleId를 생성한다")
        void shouldCreateNewRoleId() {
            RoleId roleId = RoleId.forNew();

            assertThat(roleId.value()).isNull();
            assertThat(roleId.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("같은 값을 가진 RoleId는 동등하다")
        void shouldBeEqualForSameValue() {
            RoleId roleId1 = RoleId.of(1L);
            RoleId roleId2 = RoleId.of(1L);

            assertThat(roleId1).isEqualTo(roleId2);
            assertThat(roleId1.hashCode()).isEqualTo(roleId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 RoleId는 동등하지 않다")
        void shouldNotBeEqualForDifferentValue() {
            RoleId roleId1 = RoleId.of(1L);
            RoleId roleId2 = RoleId.of(2L);

            assertThat(roleId1).isNotEqualTo(roleId2);
        }
    }
}
