package com.ryuqq.authhub.sdk.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.ryuqq.authhub.sdk.auth.ChainTokenResolver;
import com.ryuqq.authhub.sdk.auth.StaticTokenResolver;
import com.ryuqq.authhub.sdk.auth.TokenResolver;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("AuthHubClientBuilder")
class AuthHubClientBuilderTest {

    @Nested
    @DisplayName("baseUrl 메서드")
    class BaseUrl {

        @Test
        @DisplayName("baseUrl을 설정한다")
        void shouldSetBaseUrl() {
            // given
            String baseUrl = "https://authhub.example.com";

            // when
            AuthHubClientBuilder builder = AuthHubClient.builder().baseUrl(baseUrl);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("null baseUrl은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenBaseUrlIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> AuthHubClient.builder().baseUrl(null))
                    .withMessageContaining("baseUrl must not be null");
        }
    }

    @Nested
    @DisplayName("serviceToken 메서드")
    class ServiceToken {

        @Test
        @DisplayName("serviceToken을 설정한다")
        void shouldSetServiceToken() {
            // given
            String serviceToken = "my-service-token";

            // when
            AuthHubClientBuilder builder = AuthHubClient.builder().serviceToken(serviceToken);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("null serviceToken은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenServiceTokenIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> AuthHubClient.builder().serviceToken(null))
                    .withMessageContaining("serviceToken must not be null");
        }
    }

    @Nested
    @DisplayName("tokenResolver 메서드")
    class TokenResolverMethod {

        @Test
        @DisplayName("커스텀 TokenResolver를 설정한다")
        void shouldSetCustomTokenResolver() {
            // given
            TokenResolver customResolver = new StaticTokenResolver("custom-token");

            // when
            AuthHubClientBuilder builder = AuthHubClient.builder().tokenResolver(customResolver);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("null tokenResolver는 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenTokenResolverIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> AuthHubClient.builder().tokenResolver(null))
                    .withMessageContaining("tokenResolver must not be null");
        }
    }

    @Nested
    @DisplayName("타임아웃 설정")
    class Timeouts {

        @Test
        @DisplayName("connectTimeout을 설정한다")
        void shouldSetConnectTimeout() {
            // given
            Duration timeout = Duration.ofSeconds(10);

            // when
            AuthHubClientBuilder builder = AuthHubClient.builder().connectTimeout(timeout);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("readTimeout을 설정한다")
        void shouldSetReadTimeout() {
            // given
            Duration timeout = Duration.ofSeconds(60);

            // when
            AuthHubClientBuilder builder = AuthHubClient.builder().readTimeout(timeout);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("null connectTimeout은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenConnectTimeoutIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> AuthHubClient.builder().connectTimeout(null))
                    .withMessageContaining("connectTimeout must not be null");
        }

        @Test
        @DisplayName("null readTimeout은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenReadTimeoutIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> AuthHubClient.builder().readTimeout(null))
                    .withMessageContaining("readTimeout must not be null");
        }
    }

    @Nested
    @DisplayName("build 메서드")
    class Build {

        @Test
        @DisplayName("serviceToken으로 클라이언트를 빌드한다")
        void shouldBuildWithServiceToken() {
            // when
            AuthHubClient client =
                    AuthHubClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceToken("test-service-token")
                            .build();

            // then
            assertThat(client).isNotNull();
        }

        @Test
        @DisplayName("tokenResolver로 클라이언트를 빌드한다")
        void shouldBuildWithTokenResolver() {
            // given
            TokenResolver resolver = new StaticTokenResolver("custom-token");

            // when
            AuthHubClient client =
                    AuthHubClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .tokenResolver(resolver)
                            .build();

            // then
            assertThat(client).isNotNull();
        }

        @Test
        @DisplayName("tokenResolver가 serviceToken보다 우선한다")
        void tokenResolverShouldTakePrecedenceOverServiceToken() {
            // given
            TokenResolver customResolver = () -> Optional.of("custom-resolver-token");

            // when
            AuthHubClient client =
                    AuthHubClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceToken("service-token")
                            .tokenResolver(customResolver)
                            .build();

            // then
            assertThat(client).isNotNull();
        }

        @Test
        @DisplayName("baseUrl이 없으면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenBaseUrlNotSet() {
            assertThatIllegalStateException()
                    .isThrownBy(() -> AuthHubClient.builder().serviceToken("test-token").build())
                    .withMessageContaining("baseUrl must be set");
        }

        @Test
        @DisplayName("serviceToken과 tokenResolver가 모두 없으면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenNoAuthConfigured() {
            assertThatIllegalStateException()
                    .isThrownBy(
                            () ->
                                    AuthHubClient.builder()
                                            .baseUrl("https://authhub.example.com")
                                            .build())
                    .withMessageContaining("Either serviceToken or tokenResolver must be set");
        }

        @Test
        @DisplayName("빈 baseUrl이면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenBaseUrlIsEmpty() {
            assertThatIllegalStateException()
                    .isThrownBy(
                            () -> AuthHubClient.builder().baseUrl("").serviceToken("token").build())
                    .withMessageContaining("baseUrl must be set");
        }

        @Test
        @DisplayName("커스텀 타임아웃으로 빌드한다")
        void shouldBuildWithCustomTimeouts() {
            // when
            AuthHubClient client =
                    AuthHubClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceToken("test-token")
                            .connectTimeout(Duration.ofSeconds(10))
                            .readTimeout(Duration.ofSeconds(60))
                            .build();

            // then
            assertThat(client).isNotNull();
        }
    }

    @Nested
    @DisplayName("Fluent API")
    class FluentApi {

        @Test
        @DisplayName("모든 메서드가 체이닝 가능하다")
        void shouldSupportMethodChaining() {
            // when
            AuthHubClient client =
                    AuthHubClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceToken("service-token")
                            .connectTimeout(Duration.ofSeconds(5))
                            .readTimeout(Duration.ofSeconds(30))
                            .tokenResolver(ChainTokenResolver.withFallback("fallback-token"))
                            .build();

            // then
            assertThat(client).isNotNull();
        }
    }

    @Nested
    @DisplayName("빌드된 클라이언트 API")
    class ClientApis {

        @Test
        @DisplayName("auth() API를 반환한다")
        void shouldProvideAuthApi() {
            // given
            AuthHubClient client =
                    AuthHubClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceToken("test-token")
                            .build();

            // when & then
            assertThat(client.auth()).isNotNull();
        }

        @Test
        @DisplayName("onboarding() API를 반환한다")
        void shouldProvideOnboardingApi() {
            // given
            AuthHubClient client =
                    AuthHubClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceToken("test-token")
                            .build();

            // when & then
            assertThat(client.onboarding()).isNotNull();
        }
    }
}
