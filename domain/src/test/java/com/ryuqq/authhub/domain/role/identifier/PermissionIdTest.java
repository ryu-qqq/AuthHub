package com.ryuqq.authhub.domain.role.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PermissionId Identifier 테스트")
class PermissionIdTest {

    @Nested
    @DisplayName("of() 메서드는")
    class OfMethod {

        @Test
        @DisplayName("유효한 Long 값으로 생성할 수 있다")
        void shouldCreateWithValidValue() {
            PermissionId permissionId = PermissionId.of(1L);

            assertThat(permissionId.value()).isEqualTo(1L);
            assertThat(permissionId.isNew()).isFalse();
        }

        @Test
        @DisplayName("null 값은 예외를 던진다")
        void shouldThrowExceptionForNullValue() {
            assertThatThrownBy(() -> PermissionId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("0 이하의 값은 예외를 던진다")
        void shouldThrowExceptionForNonPositiveValue() {
            assertThatThrownBy(() -> PermissionId.of(0L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("양수");

            assertThatThrownBy(() -> PermissionId.of(-1L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("양수");
        }
    }

    @Nested
    @DisplayName("forNew() 메서드는")
    class ForNewMethod {

        @Test
        @DisplayName("새로운 엔티티용 PermissionId를 생성한다")
        void shouldCreateNewPermissionId() {
            PermissionId permissionId = PermissionId.forNew();

            assertThat(permissionId.value()).isNull();
            assertThat(permissionId.isNew()).isTrue();
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("같은 값을 가진 PermissionId는 동등하다")
        void shouldBeEqualForSameValue() {
            PermissionId permissionId1 = PermissionId.of(1L);
            PermissionId permissionId2 = PermissionId.of(1L);

            assertThat(permissionId1).isEqualTo(permissionId2);
            assertThat(permissionId1.hashCode()).isEqualTo(permissionId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 PermissionId는 동등하지 않다")
        void shouldNotBeEqualForDifferentValue() {
            PermissionId permissionId1 = PermissionId.of(1L);
            PermissionId permissionId2 = PermissionId.of(2L);

            assertThat(permissionId1).isNotEqualTo(permissionId2);
        }
    }
}
