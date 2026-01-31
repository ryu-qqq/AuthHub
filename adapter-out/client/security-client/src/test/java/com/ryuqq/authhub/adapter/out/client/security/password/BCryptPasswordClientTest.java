package com.ryuqq.authhub.adapter.out.client.security.password;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BCryptPasswordClient")
class BCryptPasswordClientTest {

    private BCryptPasswordClient sut;

    @BeforeEach
    void setUp() {
        sut = new BCryptPasswordClient();
    }

    @Nested
    @DisplayName("hash")
    class Hash {

        @Test
        @DisplayName("평문 비밀번호를 해시로 변환")
        void hashesRawPassword() {
            String rawPassword = "mySecretPassword123";

            String result = sut.hash(rawPassword);

            assertThat(result).isNotBlank();
            assertThat(result).startsWith("$2a$");
            assertThat(result).isNotEqualTo(rawPassword);
        }

        @Test
        @DisplayName("동일 비밀번호도 매번 다른 해시 생성 (솔트)")
        void generatesDifferentHashEachTime() {
            String rawPassword = "samePassword";

            String hash1 = sut.hash(rawPassword);
            String hash2 = sut.hash(rawPassword);

            assertThat(hash1).isNotEqualTo(hash2);
            assertThat(sut.matches(rawPassword, HashedPassword.of(hash1))).isTrue();
            assertThat(sut.matches(rawPassword, HashedPassword.of(hash2))).isTrue();
        }

        @Test
        @DisplayName("null 비밀번호면 예외")
        void throwsWhenNull() {
            assertThatThrownBy(() -> sut.hash(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password cannot be null or blank");
        }

        @Test
        @DisplayName("빈 비밀번호면 예외")
        void throwsWhenBlank() {
            assertThatThrownBy(() -> sut.hash("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Password cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("matches")
    class Matches {

        @Test
        @DisplayName("일치하는 비밀번호면 true")
        void returnsTrueWhenMatch() {
            String rawPassword = "testPassword";
            String hashed = sut.hash(rawPassword);

            boolean result = sut.matches(rawPassword, HashedPassword.of(hashed));

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("일치하지 않는 비밀번호면 false")
        void returnsFalseWhenNotMatch() {
            String hashed = sut.hash("originalPassword");

            boolean result = sut.matches("wrongPassword", HashedPassword.of(hashed));

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("rawPassword가 null이면 false")
        void returnsFalseWhenRawPasswordNull() {
            String hashed = sut.hash("password");

            boolean result = sut.matches(null, HashedPassword.of(hashed));

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("hashedPassword가 null이면 false")
        void returnsFalseWhenHashedPasswordNull() {
            boolean result = sut.matches("password", null);

            assertThat(result).isFalse();
        }
    }
}
