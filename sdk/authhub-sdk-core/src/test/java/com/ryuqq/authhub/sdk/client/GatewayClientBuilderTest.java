package com.ryuqq.authhub.sdk.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("GatewayClientBuilder")
class GatewayClientBuilderTest {

    @Nested
    @DisplayName("baseUrl 메서드")
    class BaseUrl {

        @Test
        @DisplayName("baseUrl을 설정한다")
        void shouldSetBaseUrl() {
            // given
            String baseUrl = "https://authhub.example.com";

            // when
            GatewayClientBuilder builder = GatewayClient.builder().baseUrl(baseUrl);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("null baseUrl은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenBaseUrlIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> GatewayClient.builder().baseUrl(null))
                    .withMessageContaining("baseUrl must not be null");
        }
    }

    @Nested
    @DisplayName("serviceName 메서드")
    class ServiceName {

        @Test
        @DisplayName("serviceName을 설정한다")
        void shouldSetServiceName() {
            // given
            String serviceName = "gateway";

            // when
            GatewayClientBuilder builder = GatewayClient.builder().serviceName(serviceName);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("null serviceName은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenServiceNameIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> GatewayClient.builder().serviceName(null))
                    .withMessageContaining("serviceName must not be null");
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
            GatewayClientBuilder builder = GatewayClient.builder().serviceToken(serviceToken);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("null serviceToken은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenServiceTokenIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> GatewayClient.builder().serviceToken(null))
                    .withMessageContaining("serviceToken must not be null");
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
            GatewayClientBuilder builder = GatewayClient.builder().connectTimeout(timeout);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("readTimeout을 설정한다")
        void shouldSetReadTimeout() {
            // given
            Duration timeout = Duration.ofSeconds(60);

            // when
            GatewayClientBuilder builder = GatewayClient.builder().readTimeout(timeout);

            // then
            assertThat(builder).isNotNull();
        }

        @Test
        @DisplayName("null connectTimeout은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenConnectTimeoutIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> GatewayClient.builder().connectTimeout(null))
                    .withMessageContaining("connectTimeout must not be null");
        }

        @Test
        @DisplayName("null readTimeout은 NullPointerException을 발생시킨다")
        void shouldThrowExceptionWhenReadTimeoutIsNull() {
            assertThatNullPointerException()
                    .isThrownBy(() -> GatewayClient.builder().readTimeout(null))
                    .withMessageContaining("readTimeout must not be null");
        }
    }

    @Nested
    @DisplayName("build 메서드")
    class Build {

        @Test
        @DisplayName("모든 필수 값으로 클라이언트를 빌드한다")
        void shouldBuildWithAllRequiredValues() {
            // when
            GatewayClient client =
                    GatewayClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceName("gateway")
                            .serviceToken("test-service-token")
                            .build();

            // then
            assertThat(client).isNotNull();
        }

        @Test
        @DisplayName("baseUrl이 없으면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenBaseUrlNotSet() {
            assertThatIllegalStateException()
                    .isThrownBy(
                            () ->
                                    GatewayClient.builder()
                                            .serviceName("gateway")
                                            .serviceToken("test-token")
                                            .build())
                    .withMessageContaining("baseUrl must be set");
        }

        @Test
        @DisplayName("serviceName이 없으면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenServiceNameNotSet() {
            assertThatIllegalStateException()
                    .isThrownBy(
                            () ->
                                    GatewayClient.builder()
                                            .baseUrl("https://authhub.example.com")
                                            .serviceToken("test-token")
                                            .build())
                    .withMessageContaining("serviceName must be set");
        }

        @Test
        @DisplayName("serviceToken이 없으면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenServiceTokenNotSet() {
            assertThatIllegalStateException()
                    .isThrownBy(
                            () ->
                                    GatewayClient.builder()
                                            .baseUrl("https://authhub.example.com")
                                            .serviceName("gateway")
                                            .build())
                    .withMessageContaining("serviceToken must be set");
        }

        @Test
        @DisplayName("빈 baseUrl이면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenBaseUrlIsEmpty() {
            assertThatIllegalStateException()
                    .isThrownBy(
                            () ->
                                    GatewayClient.builder()
                                            .baseUrl("")
                                            .serviceName("gateway")
                                            .serviceToken("token")
                                            .build())
                    .withMessageContaining("baseUrl must be set");
        }

        @Test
        @DisplayName("빈 serviceName이면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenServiceNameIsEmpty() {
            assertThatIllegalStateException()
                    .isThrownBy(
                            () ->
                                    GatewayClient.builder()
                                            .baseUrl("https://authhub.example.com")
                                            .serviceName("")
                                            .serviceToken("token")
                                            .build())
                    .withMessageContaining("serviceName must be set");
        }

        @Test
        @DisplayName("빈 serviceToken이면 IllegalStateException이 발생한다")
        void shouldThrowExceptionWhenServiceTokenIsEmpty() {
            assertThatIllegalStateException()
                    .isThrownBy(
                            () ->
                                    GatewayClient.builder()
                                            .baseUrl("https://authhub.example.com")
                                            .serviceName("gateway")
                                            .serviceToken("")
                                            .build())
                    .withMessageContaining("serviceToken must be set");
        }

        @Test
        @DisplayName("커스텀 타임아웃으로 빌드한다")
        void shouldBuildWithCustomTimeouts() {
            // when
            GatewayClient client =
                    GatewayClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceName("gateway")
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
            GatewayClient client =
                    GatewayClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceName("gateway")
                            .serviceToken("service-token")
                            .connectTimeout(Duration.ofSeconds(5))
                            .readTimeout(Duration.ofSeconds(30))
                            .build();

            // then
            assertThat(client).isNotNull();
        }
    }

    @Nested
    @DisplayName("빌드된 클라이언트 API")
    class ClientApis {

        @Test
        @DisplayName("internal() API를 반환한다")
        void shouldProvideInternalApi() {
            // given
            GatewayClient client =
                    GatewayClient.builder()
                            .baseUrl("https://authhub.example.com")
                            .serviceName("gateway")
                            .serviceToken("test-token")
                            .build();

            // when & then
            assertThat(client.internal()).isNotNull();
        }
    }
}
