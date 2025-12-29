package com.ryuqq.authhub.sdk.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("StaticTokenResolver")
class StaticTokenResolverTest {

    @Nested
    @DisplayName("생성자")
    class Constructor {

        @Test
        @DisplayName("유효한 토큰으로 생성하면 인스턴스가 생성된다")
        void shouldCreateInstanceWithValidToken() {
            // given
            String token = "valid-service-token";

            // when
            StaticTokenResolver resolver = new StaticTokenResolver(token);

            // then
            assertThat(resolver).isNotNull();
        }

        @Test
        @DisplayName("null 토큰으로 생성하면 NullPointerException이 발생한다")
        void shouldThrowExceptionWhenTokenIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> new StaticTokenResolver(null))
                    .withMessageContaining("token must not be null");
        }
    }

    @Nested
    @DisplayName("resolve 메서드")
    class Resolve {

        @Test
        @DisplayName("설정된 토큰을 항상 반환한다")
        void shouldAlwaysReturnConfiguredToken() {
            // given
            String expectedToken = "my-service-token";
            StaticTokenResolver resolver = new StaticTokenResolver(expectedToken);

            // when
            Optional<String> result = resolver.resolve();

            // then
            assertThat(result).isPresent().contains(expectedToken);
        }

        @Test
        @DisplayName("여러 번 호출해도 동일한 토큰을 반환한다")
        void shouldReturnSameTokenOnMultipleCalls() {
            // given
            String expectedToken = "consistent-token";
            StaticTokenResolver resolver = new StaticTokenResolver(expectedToken);

            // when & then
            assertThat(resolver.resolve()).contains(expectedToken);
            assertThat(resolver.resolve()).contains(expectedToken);
            assertThat(resolver.resolve()).contains(expectedToken);
        }

        @Test
        @DisplayName("빈 문자열 토큰도 정상적으로 반환한다")
        void shouldReturnEmptyStringToken() {
            // given
            String emptyToken = "";
            StaticTokenResolver resolver = new StaticTokenResolver(emptyToken);

            // when
            Optional<String> result = resolver.resolve();

            // then
            assertThat(result).isPresent().contains(emptyToken);
        }
    }
}
