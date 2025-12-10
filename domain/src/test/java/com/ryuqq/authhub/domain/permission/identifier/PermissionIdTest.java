package com.ryuqq.authhub.domain.permission.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionId 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("PermissionId 테스트")
class PermissionIdTest {

    @Nested
    @DisplayName("forNew 메서드")
    class ForNewTest {

        @Test
        @DisplayName("UUID로 새 PermissionId를 생성한다")
        void shouldCreateNewPermissionId() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            PermissionId permissionId = PermissionId.forNew(uuid);

            // then
            assertThat(permissionId).isNotNull();
            assertThat(permissionId.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("null UUID로 생성 시 예외 발생")
        void shouldThrowExceptionWhenNullUuid() {
            assertThatThrownBy(() -> PermissionId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("of(UUID) 메서드")
    class OfUuidTest {

        @Test
        @DisplayName("UUID로 PermissionId를 생성한다")
        void shouldCreatePermissionIdFromUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            PermissionId permissionId = PermissionId.of(uuid);

            // then
            assertThat(permissionId.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("null UUID로 생성 시 예외 발생")
        void shouldThrowExceptionWhenNullUuid() {
            assertThatThrownBy(() -> PermissionId.of((UUID) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }
    }

    @Nested
    @DisplayName("of(String) 메서드")
    class OfStringTest {

        @Test
        @DisplayName("문자열로 PermissionId를 생성한다")
        void shouldCreatePermissionIdFromString() {
            // given
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();

            // when
            PermissionId permissionId = PermissionId.of(uuidString);

            // then
            assertThat(permissionId.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("null 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenNullString() {
            assertThatThrownBy(() -> PermissionId.of((String) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값");
        }

        @Test
        @DisplayName("빈 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenEmptyString() {
            assertThatThrownBy(() -> PermissionId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값");
        }

        @Test
        @DisplayName("공백 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenBlankString() {
            assertThatThrownBy(() -> PermissionId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값");
        }

        @Test
        @DisplayName("유효하지 않은 UUID 형식으로 생성 시 예외 발생")
        void shouldThrowExceptionWhenInvalidUuidFormat() {
            assertThatThrownBy(() -> PermissionId.of("invalid-uuid"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 UUID");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 UUID를 가진 PermissionId는 동일하다")
        void shouldBeEqualWhenSameUuid() {
            // given
            UUID uuid = UUID.randomUUID();
            PermissionId id1 = PermissionId.of(uuid);
            PermissionId id2 = PermissionId.of(uuid);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 UUID를 가진 PermissionId는 다르다")
        void shouldNotBeEqualWhenDifferentUuid() {
            // given
            PermissionId id1 = PermissionId.of(UUID.randomUUID());
            PermissionId id2 = PermissionId.of(UUID.randomUUID());

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("PermissionId의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            UUID uuid = UUID.randomUUID();
            PermissionId permissionId = PermissionId.of(uuid);

            // when
            String toString = permissionId.toString();

            // then
            assertThat(toString).contains("PermissionId");
            assertThat(toString).contains(uuid.toString());
        }
    }
}
