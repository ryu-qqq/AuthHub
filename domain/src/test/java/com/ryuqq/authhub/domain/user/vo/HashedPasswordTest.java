package com.ryuqq.authhub.domain.user.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * HashedPassword Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("HashedPassword 테스트")
class HashedPasswordTest {

    private static final String VALID_HASH = "$2a$10$hashedpasswordvalue";

    @Nested
    @DisplayName("HashedPassword 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // when
            HashedPassword hashedPassword = HashedPassword.of(VALID_HASH);

            // then
            assertThat(hashedPassword.value()).isEqualTo(VALID_HASH);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsNull() {
            // when & then
            assertThatThrownBy(() -> HashedPassword.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsBlank() {
            // when & then
            assertThatThrownBy(() -> HashedPassword.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }

        @Test
        @DisplayName("공백만 있으면 예외가 발생한다")
        void shouldThrowExceptionWhenValueIsWhitespace() {
            // when & then
            assertThatThrownBy(() -> HashedPassword.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null이거나 빈 값일 수 없습니다");
        }
    }

    @Nested
    @DisplayName("HashedPassword equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 값을 가진 HashedPassword는 동등하다")
        void shouldBeEqualWhenSameValue() {
            // given
            HashedPassword password1 = HashedPassword.of(VALID_HASH);
            HashedPassword password2 = HashedPassword.of(VALID_HASH);

            // then
            assertThat(password1).isEqualTo(password2);
            assertThat(password1.hashCode()).isEqualTo(password2.hashCode());
        }

        @Test
        @DisplayName("다른 값을 가진 HashedPassword는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentValue() {
            // given
            HashedPassword password1 = HashedPassword.of(VALID_HASH);
            HashedPassword password2 = HashedPassword.of("$2a$10$different");

            // then
            assertThat(password1).isNotEqualTo(password2);
        }
    }
}
