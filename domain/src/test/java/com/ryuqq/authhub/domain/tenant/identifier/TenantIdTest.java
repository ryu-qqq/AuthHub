package com.ryuqq.authhub.domain.tenant.identifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantId 식별자 VO 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantId 테스트")
class TenantIdTest {

    @Nested
    @DisplayName("forNew 팩토리 메서드")
    class ForNewTest {

        @Test
        @DisplayName("새로운 UUIDv7 기반 TenantId를 생성한다")
        void shouldCreateNewTenantIdWithUuidV7() {
            // when
            TenantId tenantId = TenantId.forNew(UUID.randomUUID());

            // then
            assertThat(tenantId).isNotNull();
            assertThat(tenantId.value()).isNotNull();
        }

        @Test
        @DisplayName("매번 새로운 ID를 생성한다")
        void shouldCreateUniqueIdEachTime() {
            // when
            TenantId id1 = TenantId.forNew(UUID.randomUUID());
            TenantId id2 = TenantId.forNew(UUID.randomUUID());

            // then
            assertThat(id1).isNotEqualTo(id2);
            assertThat(id1.value()).isNotEqualTo(id2.value());
        }
    }

    @Nested
    @DisplayName("of(UUID) 팩토리 메서드")
    class OfUuidTest {

        @Test
        @DisplayName("UUID로 TenantId를 생성한다")
        void shouldCreateTenantIdFromUuid() {
            // given
            UUID uuid = UUID.randomUUID();

            // when
            TenantId tenantId = TenantId.of(uuid);

            // then
            assertThat(tenantId).isNotNull();
            assertThat(tenantId.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("null UUID로 생성 시 예외 발생")
        void shouldThrowExceptionWhenUuidIsNull() {
            assertThatThrownBy(() -> TenantId.of((UUID) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("of(String) 팩토리 메서드")
    class OfStringTest {

        @Test
        @DisplayName("문자열로 TenantId를 생성한다")
        void shouldCreateTenantIdFromString() {
            // given
            String uuidString = "01941234-5678-7000-8000-123456789abc";

            // when
            TenantId tenantId = TenantId.of(uuidString);

            // then
            assertThat(tenantId).isNotNull();
            assertThat(tenantId.value().toString()).isEqualTo(uuidString);
        }

        @Test
        @DisplayName("null 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenStringIsNull() {
            assertThatThrownBy(() -> TenantId.of((String) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId 문자열은 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성 시 예외 발생")
        void shouldThrowExceptionWhenStringIsBlank() {
            assertThatThrownBy(() -> TenantId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId 문자열은 null이거나 빈 값일 수 없습니다");

            assertThatThrownBy(() -> TenantId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId 문자열은 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("유효하지 않은 UUID 형식 시 예외 발생")
        void shouldThrowExceptionWhenInvalidUuidFormat() {
            assertThatThrownBy(() -> TenantId.of("invalid-uuid"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 UUID 형식입니다");
        }
    }

    @Nested
    @DisplayName("equals 및 hashCode")
    class EqualsHashCodeTest {

        @Test
        @DisplayName("같은 값을 가진 TenantId는 동일하다")
        void shouldBeEqualWhenSameValue() {
            // given
            UUID uuid = UUID.randomUUID();
            TenantId id1 = TenantId.of(uuid);
            TenantId id2 = TenantId.of(uuid);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 TenantId는 다르다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            TenantId id1 = TenantId.of(UUID.randomUUID());
            TenantId id2 = TenantId.of(UUID.randomUUID());

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("자기 자신과 같다")
        void shouldBeEqualToItself() {
            // given
            TenantId tenantId = TenantId.forNew(UUID.randomUUID());

            // then
            assertThat(tenantId).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("null과 같지 않다")
        void shouldNotBeEqualToNull() {
            // given
            TenantId tenantId = TenantId.forNew(UUID.randomUUID());

            // then
            assertThat(tenantId).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 같지 않다")
        void shouldNotBeEqualToDifferentType() {
            // given
            TenantId tenantId = TenantId.forNew(UUID.randomUUID());

            // then
            assertThat(tenantId).isNotEqualTo("not-a-tenant-id");
        }
    }

    @Nested
    @DisplayName("toString")
    class ToStringTest {

        @Test
        @DisplayName("TenantId의 문자열 표현을 반환한다")
        void shouldReturnStringRepresentation() {
            // given
            UUID uuid = UUID.randomUUID();
            TenantId tenantId = TenantId.of(uuid);

            // when
            String toString = tenantId.toString();

            // then
            assertThat(toString).contains("TenantId");
            assertThat(toString).contains(uuid.toString());
        }
    }
}
