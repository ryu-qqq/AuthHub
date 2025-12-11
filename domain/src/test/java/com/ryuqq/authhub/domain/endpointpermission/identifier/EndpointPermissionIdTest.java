package com.ryuqq.authhub.domain.endpointpermission.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** EndpointPermissionId Value Object 단위 테스트 */
@Tag("unit")
@Tag("domain")
@Tag("identifier")
@DisplayName("EndpointPermissionId VO 단위 테스트")
class EndpointPermissionIdTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTests {

        @Test
        @DisplayName("forNew() - Application에서 생성된 UUID로 생성")
        void forNew_WithUUID_ShouldCreate() {
            // Given
            UUID uuid = UUID.randomUUID();

            // When
            EndpointPermissionId id = EndpointPermissionId.forNew(uuid);

            // Then
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("forNew() - null UUID는 예외 발생")
        void forNew_WithNull_ShouldThrowException() {
            assertThatThrownBy(() -> EndpointPermissionId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("EndpointPermissionId는 null일 수 없습니다");
        }

        @Test
        @DisplayName("of(UUID) - 기존 UUID로 생성")
        void of_WithUUID_ShouldCreate() {
            // Given
            UUID uuid = UUID.randomUUID();

            // When
            EndpointPermissionId id = EndpointPermissionId.of(uuid);

            // Then
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("of(UUID) - null UUID는 예외 발생")
        void of_WithNullUUID_ShouldThrowException() {
            assertThatThrownBy(() -> EndpointPermissionId.of((UUID) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("EndpointPermissionId는 null일 수 없습니다");
        }

        @Test
        @DisplayName("of(String) - 문자열 UUID로 생성")
        void of_WithString_ShouldCreate() {
            // Given
            String uuidString = "550e8400-e29b-41d4-a716-446655440000";

            // When
            EndpointPermissionId id = EndpointPermissionId.of(uuidString);

            // Then
            assertThat(id.value().toString()).isEqualTo(uuidString);
        }

        @Test
        @DisplayName("of(String) - null 문자열은 예외 발생")
        void of_WithNullString_ShouldThrowException() {
            assertThatThrownBy(() -> EndpointPermissionId.of((String) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("EndpointPermissionId 문자열은 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("of(String) - 빈 문자열은 예외 발생")
        void of_WithEmptyString_ShouldThrowException() {
            assertThatThrownBy(() -> EndpointPermissionId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("EndpointPermissionId 문자열은 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("of(String) - 잘못된 형식의 문자열은 예외 발생")
        void of_WithInvalidString_ShouldThrowException() {
            assertThatThrownBy(() -> EndpointPermissionId.of("invalid-uuid"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 UUID 형식입니다");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTests {

        @Test
        @DisplayName("동일한 UUID를 가진 ID는 동등하다")
        void equals_SameUUID_ShouldBeEqual() {
            // Given
            UUID uuid = UUID.randomUUID();
            EndpointPermissionId id1 = EndpointPermissionId.of(uuid);
            EndpointPermissionId id2 = EndpointPermissionId.of(uuid);

            // Then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 UUID를 가진 ID는 동등하지 않다")
        void equals_DifferentUUID_ShouldNotBeEqual() {
            // Given
            EndpointPermissionId id1 = EndpointPermissionId.of(UUID.randomUUID());
            EndpointPermissionId id2 = EndpointPermissionId.of(UUID.randomUUID());

            // Then
            assertThat(id1).isNotEqualTo(id2);
        }
    }

    @Nested
    @DisplayName("toString 테스트")
    class ToStringTests {

        @Test
        @DisplayName("toString()은 ID 정보를 포함한다")
        void toString_ShouldContainValue() {
            // Given
            UUID uuid = UUID.randomUUID();
            EndpointPermissionId id = EndpointPermissionId.of(uuid);

            // Then
            assertThat(id.toString()).contains(uuid.toString());
            assertThat(id.toString()).contains("EndpointPermissionId");
        }
    }
}
