package com.ryuqq.authhub.domain.role.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleId 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RoleId 테스트")
class RoleIdTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드")
    class ForNewTest {

        @Test
        @DisplayName("UUID로 새로운 RoleId를 생성한다")
        void shouldCreateNewRoleIdWithUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            RoleId roleId = RoleId.forNew(uuid);

            // then
            assertThat(roleId).isNotNull();
            assertThat(roleId.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("null UUID로 생성 시 예외 발생")
        void shouldThrowExceptionWhenUuidIsNull() {
            assertThatThrownBy(() -> RoleId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleId는 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("of(UUID) 팩토리 메서드")
    class OfUuidTest {

        @Test
        @DisplayName("UUID로 RoleId를 생성한다")
        void shouldCreateRoleIdWithUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            RoleId roleId = RoleId.of(uuid);

            // then
            assertThat(roleId).isNotNull();
            assertThat(roleId.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("null UUID로 생성 시 예외 발생")
        void shouldThrowExceptionWhenUuidIsNull() {
            assertThatThrownBy(() -> RoleId.of((UUID) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleId는 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("of(String) 팩토리 메서드")
    class OfStringTest {

        @Test
        @DisplayName("문자열로 RoleId를 생성한다")
        void shouldCreateRoleIdWithString() {
            // given
            String uuidString = "550e8400-e29b-41d4-a716-446655440000";

            // when
            RoleId roleId = RoleId.of(uuidString);

            // then
            assertThat(roleId).isNotNull();
            assertThat(roleId.value()).isEqualTo(UUID.fromString(uuidString));
        }

        @Test
        @DisplayName("null 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenStringIsNull() {
            assertThatThrownBy(() -> RoleId.of((String) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleId 문자열은 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenStringIsEmpty() {
            assertThatThrownBy(() -> RoleId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("RoleId 문자열은 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("유효하지 않은 UUID 형식으로 생성 시 예외 발생")
        void shouldThrowExceptionWhenInvalidUuidFormat() {
            assertThatThrownBy(() -> RoleId.of("invalid-uuid"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 UUID 형식입니다");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 UUID를 가진 RoleId는 동일하다")
        void shouldBeEqualWhenSameUuid() {
            // given
            UUID uuid = UUID.randomUUID();
            RoleId roleId1 = RoleId.of(uuid);
            RoleId roleId2 = RoleId.of(uuid);

            // then
            assertThat(roleId1).isEqualTo(roleId2);
            assertThat(roleId1.hashCode()).isEqualTo(roleId2.hashCode());
        }

        @Test
        @DisplayName("다른 UUID를 가진 RoleId는 다르다")
        void shouldNotBeEqualWhenDifferentUuid() {
            // given
            RoleId roleId1 = RoleId.of(UUID.randomUUID());
            RoleId roleId2 = RoleId.of(UUID.randomUUID());

            // then
            assertThat(roleId1).isNotEqualTo(roleId2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());

            // then
            assertThat(roleId).isEqualTo(roleId);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            RoleId roleId = RoleId.of(UUID.randomUUID());

            // then
            assertThat(roleId).isNotEqualTo(null);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("RoleId의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            RoleId roleId = RoleId.of(uuid);

            // when
            String toString = roleId.toString();

            // then
            assertThat(toString).contains("RoleId");
            assertThat(toString).contains("550e8400-e29b-41d4-a716-446655440000");
        }
    }
}
