package com.ryuqq.authhub.domain.user.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserId Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserId 테스트")
class UserIdTest {

    private static final String VALID_UUID = "01941234-5678-7000-8000-123456789001";

    @Nested
    @DisplayName("UserId 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("forNew() 팩토리 메서드로 생성한다")
        void shouldCreateViaForNew() {
            // when
            UserId userId = UserId.forNew(VALID_UUID);

            // then
            assertThat(userId.value()).isEqualTo(VALID_UUID);
        }

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaOf() {
            // when
            UserId userId = UserId.of(VALID_UUID);

            // then
            assertThat(userId.value()).isEqualTo(VALID_UUID);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> UserId.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("forNew에 null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenForNewValueIsNull() {
            // when & then
            assertThatThrownBy(() -> UserId.forNew(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> UserId.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> UserId.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("유효한 UUID 문자열로 생성할 수 있다")
        void shouldCreateWithValidUuid() {
            // when
            UserId userId = UserId.of(VALID_UUID);

            // then
            assertThat(userId.value()).isEqualTo(VALID_UUID);
        }
    }

    @Nested
    @DisplayName("UserId equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 UserId는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            UserId userId1 = UserId.of(VALID_UUID);
            UserId userId2 = UserId.of(VALID_UUID);

            // then
            assertThat(userId1).isEqualTo(userId2);
            assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 UserId는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            UserId userId1 = UserId.of(VALID_UUID);
            UserId userId2 = UserId.of("01941234-5678-7000-8000-123456789002");

            // then
            assertThat(userId1).isNotEqualTo(userId2);
        }
    }
}
