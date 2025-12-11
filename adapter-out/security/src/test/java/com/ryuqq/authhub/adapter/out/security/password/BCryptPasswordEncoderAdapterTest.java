package com.ryuqq.authhub.adapter.out.security.password;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * BCryptPasswordEncoderAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("BCryptPasswordEncoderAdapter 단위 테스트")
class BCryptPasswordEncoderAdapterTest {

    private BCryptPasswordEncoderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BCryptPasswordEncoderAdapter();
    }

    @Nested
    @DisplayName("hash 메서드")
    class HashTest {

        @Test
        @DisplayName("비밀번호를 성공적으로 해시한다")
        void shouldHashPasswordSuccessfully() {
            // given
            String rawPassword = "password123!@#";

            // when
            String hashedPassword = adapter.hash(rawPassword);

            // then
            assertThat(hashedPassword).isNotNull();
            assertThat(hashedPassword).isNotBlank();
            assertThat(hashedPassword).isNotEqualTo(rawPassword);
            assertThat(hashedPassword).startsWith("$2a$"); // BCrypt prefix
        }

        @Test
        @DisplayName("같은 비밀번호도 매번 다른 해시값을 생성한다 (솔트)")
        void shouldGenerateDifferentHashesForSamePassword() {
            // given
            String rawPassword = "password123";

            // when
            String hash1 = adapter.hash(rawPassword);
            String hash2 = adapter.hash(rawPassword);

            // then
            assertThat(hash1).isNotEqualTo(hash2);
        }

        @Test
        @DisplayName("null 비밀번호는 예외를 발생시킨다")
        void shouldThrowExceptionForNullPassword() {
            // when & then
            assertThatThrownBy(() -> adapter.hash(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password cannot be null or blank");
        }

        @Test
        @DisplayName("빈 비밀번호는 예외를 발생시킨다")
        void shouldThrowExceptionForBlankPassword() {
            // when & then
            assertThatThrownBy(() -> adapter.hash(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password cannot be null or blank");

            assertThatThrownBy(() -> adapter.hash("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("matches 메서드")
    class MatchesTest {

        @Test
        @DisplayName("올바른 비밀번호는 true를 반환한다")
        void shouldReturnTrueForCorrectPassword() {
            // given
            String rawPassword = "password123!@#";
            String hashedPassword = adapter.hash(rawPassword);

            // when
            boolean result = adapter.matches(rawPassword, hashedPassword);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("잘못된 비밀번호는 false를 반환한다")
        void shouldReturnFalseForIncorrectPassword() {
            // given
            String rawPassword = "password123";
            String hashedPassword = adapter.hash(rawPassword);

            // when
            boolean result = adapter.matches("wrongPassword", hashedPassword);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null rawPassword는 false를 반환한다")
        void shouldReturnFalseForNullRawPassword() {
            // given
            String hashedPassword = adapter.hash("password123");

            // when
            boolean result = adapter.matches(null, hashedPassword);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null hashedPassword는 false를 반환한다")
        void shouldReturnFalseForNullHashedPassword() {
            // when
            boolean result = adapter.matches("password123", null);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("둘 다 null이면 false를 반환한다")
        void shouldReturnFalseForBothNull() {
            // when
            boolean result = adapter.matches(null, null);

            // then
            assertThat(result).isFalse();
        }
    }
}
