package com.ryuqq.authhub.domain.organization.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationId 식별자 VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OrganizationId 테스트")
class OrganizationIdTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드")
    class ForNewTest {

        @Test
        @DisplayName("새로운 UUIDv7 기반 OrganizationId를 생성한다")
        void shouldCreateNewOrganizationIdWithUuidV7() {
            // when
            OrganizationId organizationId = OrganizationId.forNew(UUID.randomUUID());

            // then
            assertThat(organizationId).isNotNull();
            assertThat(organizationId.value()).isNotNull();
        }

        @Test
        @DisplayName("매번 새로운 ID를 생성한다")
        void shouldCreateUniqueIdEachTime() {
            // when
            OrganizationId id1 = OrganizationId.forNew(UUID.randomUUID());
            OrganizationId id2 = OrganizationId.forNew(UUID.randomUUID());

            // then
            assertThat(id1).isNotEqualTo(id2);
            assertThat(id1.value()).isNotEqualTo(id2.value());
        }
    }

    @Nested
    @DisplayName("of(UUID) 팩토리 메서드")
    class OfUuidTest {

        @Test
        @DisplayName("UUID로 OrganizationId를 생성한다")
        void shouldCreateOrganizationIdFromUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            OrganizationId organizationId = OrganizationId.of(uuid);

            // then
            assertThat(organizationId).isNotNull();
            assertThat(organizationId.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("null UUID로 생성 시 예외 발생")
        void shouldThrowExceptionWhenUuidIsNull() {
            assertThatThrownBy(() -> OrganizationId.of((UUID) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId는 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("of(String) 팩토리 메서드")
    class OfStringTest {

        @Test
        @DisplayName("문자열로 OrganizationId를 생성한다")
        void shouldCreateOrganizationIdFromString() {
            // given
            String uuidString = "01941234-5678-7000-8000-123456789def";

            // when
            OrganizationId organizationId = OrganizationId.of(uuidString);

            // then
            assertThat(organizationId).isNotNull();
            assertThat(organizationId.value().toString()).isEqualTo(uuidString);
        }

        @Test
        @DisplayName("null 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenStringIsNull() {
            assertThatThrownBy(() -> OrganizationId.of((String) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId 문자열은 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenStringIsBlank() {
            assertThatThrownBy(() -> OrganizationId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId 문자열은 null이거나 빈 값일 수 없습니다");

            assertThatThrownBy(() -> OrganizationId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId 문자열은 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("유효하지 않은 UUID 형식 시 예외 발생")
        void shouldThrowExceptionWhenInvalidUuidFormat() {
            assertThatThrownBy(() -> OrganizationId.of("invalid-uuid"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 UUID 형식입니다");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 OrganizationId는 동일하다")
        void shouldBeEqualWhenSameValue() {
            // given
            UUID uuid = UUID.randomUUID();
            OrganizationId id1 = OrganizationId.of(uuid);
            OrganizationId id2 = OrganizationId.of(uuid);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 OrganizationId는 다르다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            OrganizationId id1 = OrganizationId.of(UUID.randomUUID());
            OrganizationId id2 = OrganizationId.of(UUID.randomUUID());

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            OrganizationId organizationId = OrganizationId.forNew(UUID.randomUUID());

            // then
            assertThat(organizationId).isEqualTo(organizationId);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            OrganizationId organizationId = OrganizationId.forNew(UUID.randomUUID());

            // then
            assertThat(organizationId).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 같지 않다")
        void shouldNotBeEqualToDifferentType() {
            // given
            OrganizationId organizationId = OrganizationId.forNew(UUID.randomUUID());

            // then
            assertThat(organizationId).isNotEqualTo("not-an-organization-id");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("OrganizationId의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            UUID uuid = UUID.randomUUID();
            OrganizationId organizationId = OrganizationId.of(uuid);

            // when
            String toString = organizationId.toString();

            // then
            assertThat(toString).contains("OrganizationId");
            assertThat(toString).contains(uuid.toString());
        }
    }
}
