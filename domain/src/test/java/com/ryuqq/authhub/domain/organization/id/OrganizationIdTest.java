package com.ryuqq.authhub.domain.organization.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationId 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("OrganizationId 테스트")
class OrganizationIdTest {

    @Nested
    @DisplayName("OrganizationId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("forNew()로 OrganizationId를 생성한다")
        void shouldCreateWithForNew() {
            // given
            String value = "01941234-5678-7000-8000-123456789999";

            // when
            OrganizationId organizationId = OrganizationId.forNew(value);

            // then
            assertThat(organizationId.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("of()로 OrganizationId를 생성한다")
        void shouldCreateWithOf() {
            // given
            String value = "01941234-5678-7000-8000-123456789999";

            // when
            OrganizationId organizationId = OrganizationId.of(value);

            // then
            assertThat(organizationId.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("forNew()에 null을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenForNewWithNull() {
            // when & then
            assertThatThrownBy(() -> OrganizationId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId는 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("forNew()에 빈 값을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenForNewWithBlank() {
            // when & then
            assertThatThrownBy(() -> OrganizationId.forNew(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId는 null이거나 빈 값일 수 없습니다");

            assertThatThrownBy(() -> OrganizationId.forNew("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId는 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("of()에 null을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenOfWithNull() {
            // when & then
            assertThatThrownBy(() -> OrganizationId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId는 null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("of()에 빈 값을 입력하면 예외가 발생한다")
        void shouldThrowExceptionWhenOfWithBlank() {
            // when & then
            assertThatThrownBy(() -> OrganizationId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId는 null이거나 빈 값일 수 없습니다");

            assertThatThrownBy(() -> OrganizationId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("OrganizationId는 null이거나 빈 값일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("OrganizationId 동등성 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 OrganizationId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            String value = "01941234-5678-7000-8000-123456789999";
            OrganizationId id1 = OrganizationId.of(value);
            OrganizationId id2 = OrganizationId.of(value);

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 OrganizationId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            OrganizationId id1 = OrganizationId.of("01941234-5678-7000-8000-123456789999");
            OrganizationId id2 = OrganizationId.of("01941234-5678-7000-8000-123456789aaa");

            // then
            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("null과 비교하면 동등하지 않다")
        void shouldNotBeEqualWhenComparedWithNull() {
            // given
            OrganizationId organizationId =
                    OrganizationId.of("01941234-5678-7000-8000-123456789999");

            // then
            assertThat(organizationId).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입과 비교하면 동등하지 않다")
        void shouldNotBeEqualWhenComparedWithDifferentType() {
            // given
            OrganizationId organizationId =
                    OrganizationId.of("01941234-5678-7000-8000-123456789999");
            String differentType = "01941234-5678-7000-8000-123456789999";

            // then
            assertThat(organizationId).isNotEqualTo(differentType);
        }
    }
}
