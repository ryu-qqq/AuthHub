package com.ryuqq.authhub.domain.tenant.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantId 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("TenantId 테스트")
class TenantIdTest {

    @Nested
    @DisplayName("TenantId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("forNew()로 TenantId를 생성한다")
        void shouldCreateWithForNew() {
            // given
            String value = "01941234-5678-7000-8000-123456789999";

            // when
            TenantId tenantId = TenantId.forNew(value);

            // then
            assertThat(tenantId.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("of()로 TenantId를 생성한다")
        void shouldCreateWithOf() {
            // given
            String value = "01941234-5678-7000-8000-123456789999";

            // when
            TenantId tenantId = TenantId.of(value);

            // then
            assertThat(tenantId.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("fromNullable()로 정상 값으로 TenantId를 생성한다")
        void shouldCreateWithFromNullable() {
            // given
            String value = "01941234-5678-7000-8000-123456789999";

            // when
            TenantId tenantId = TenantId.fromNullable(value);

            // then
            assertThat(tenantId).isNotNull();
            assertThat(tenantId.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("fromNullable()에 null을 입력하면 null을 반환한다")
        void shouldReturnNullWhenFromNullableWithNull() {
            // when
            TenantId tenantId = TenantId.fromNullable(null);

            // then
            assertThat(tenantId).isNull();
        }

        @Test
        @DisplayName("fromNullable()에 빈 값을 입력하면 null을 반환한다")
        void shouldReturnNullWhenFromNullableWithBlank() {
            // when
            TenantId tenantId1 = TenantId.fromNullable("");
            TenantId tenantId2 = TenantId.fromNullable("   ");

            // then
            assertThat(tenantId1).isNull();
            assertThat(tenantId2).isNull();
        }

        @Test
        @DisplayName("forNew()에 null을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenForNewWithNull() {
            // when & then
            assertThatThrownBy(() -> TenantId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("forNew()에 빈 값을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenForNewWithBlank() {
            // when & then
            assertThatThrownBy(() -> TenantId.forNew(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null이거나 빈 값일 수 없습니다");

            assertThatThrownBy(() -> TenantId.forNew("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("of()에 null을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenOfWithNull() {
            // when & then
            assertThatThrownBy(() -> TenantId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("of()에 빈 값을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenOfWithBlank() {
            // when & then
            assertThatThrownBy(() -> TenantId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null이거나 빈 값일 수 없습니다");

            assertThatThrownBy(() -> TenantId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TenantId는 null이거나 빈 값일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("TenantId 동등성 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 TenantId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            String value = "01941234-5678-7000-8000-123456789999";
            TenantId id1 = TenantId.of(value);
            TenantId id2 = TenantId.of(value);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 TenantId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            TenantId id1 = TenantId.of("01941234-5678-7000-8000-123456789999");
            TenantId id2 = TenantId.of("01941234-5678-7000-8000-123456789aaa");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("null과 비교하면 동등하지 않다")
        void shouldNotBeEqualWhenComparedWithNull() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-123456789999");

            // then
            assertThat(tenantId).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 비교하면 동등하지 않다")
        void shouldNotBeEqualWhenComparedWithDifferentType() {
            // given
            TenantId tenantId = TenantId.of("01941234-5678-7000-8000-123456789999");
            String differentType = "01941234-5678-7000-8000-123456789999";

            // then
            assertThat(tenantId).isNotEqualTo(differentType);
        }
    }
}
