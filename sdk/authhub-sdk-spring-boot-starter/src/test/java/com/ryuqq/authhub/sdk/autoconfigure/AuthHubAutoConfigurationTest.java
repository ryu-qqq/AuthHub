package com.ryuqq.authhub.sdk.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.api.UserApi;
import com.ryuqq.authhub.sdk.auth.StaticTokenResolver;
import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.authhub.sdk.client.AuthHubClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@DisplayName("AuthHubAutoConfiguration")
class AuthHubAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(AutoConfigurations.of(AuthHubAutoConfiguration.class));

    @Nested
    @DisplayName("조건부 활성화")
    class ConditionalActivation {

        @Test
        @DisplayName("base-url이 설정되면 AutoConfiguration이 활성화된다")
        void shouldActivateWhenBaseUrlIsSet() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=test-token")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(AuthHubClient.class);
                            });
        }

        @Test
        @DisplayName("base-url이 설정되지 않으면 AutoConfiguration이 비활성화된다")
        void shouldNotActivateWhenBaseUrlIsNotSet() {
            contextRunner.run(
                    context -> {
                        assertThat(context).doesNotHaveBean(AuthHubClient.class);
                    });
        }
    }

    @Nested
    @DisplayName("TokenResolver Bean")
    class TokenResolverBean {

        @Test
        @DisplayName("service-token이 설정되면 ChainTokenResolver가 생성된다")
        void shouldCreateChainTokenResolverWithServiceToken() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=test-service-token")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(TokenResolver.class);
                                TokenResolver resolver = context.getBean(TokenResolver.class);
                                // ChainTokenResolver: ThreadLocal → StaticToken
                                // ThreadLocal이 비어있으면 서비스 토큰 반환
                                assertThat(resolver.resolve()).contains("test-service-token");
                            });
        }

        @Test
        @DisplayName("service-token이 없으면 ThreadLocalTokenResolver만 사용된다")
        void shouldCreateThreadLocalTokenResolverWithoutServiceToken() {
            contextRunner
                    .withPropertyValues("authhub.base-url=https://authhub.example.com")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(TokenResolver.class);
                                TokenResolver resolver = context.getBean(TokenResolver.class);
                                // ThreadLocal에 토큰이 없으므로 empty
                                assertThat(resolver.resolve()).isEmpty();
                            });
        }

        @Test
        @DisplayName("커스텀 TokenResolver Bean이 있으면 사용된다")
        void shouldUseCustomTokenResolverIfProvided() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=service-token")
                    .withBean(TokenResolver.class, () -> new StaticTokenResolver("custom-token"))
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(TokenResolver.class);
                                TokenResolver resolver = context.getBean(TokenResolver.class);
                                assertThat(resolver.resolve()).contains("custom-token");
                            });
        }
    }

    @Nested
    @DisplayName("AuthHubClient Bean")
    class AuthHubClientBean {

        @Test
        @DisplayName("AuthHubClient Bean이 생성된다")
        void shouldCreateAuthHubClientBean() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=test-token")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(AuthHubClient.class);
                            });
        }

        @Test
        @DisplayName("커스텀 AuthHubClient Bean이 있으면 사용된다")
        void shouldUseCustomClientIfProvided() {
            AuthHubClient customClient =
                    new AuthHubClient() {
                        @Override
                        public AuthApi auth() {
                            return null;
                        }

                        @Override
                        public OnboardingApi onboarding() {
                            return null;
                        }

                        @Override
                        public UserApi user() {
                            return null;
                        }
                    };

            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=test-token")
                    .withBean(AuthHubClient.class, () -> customClient)
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(AuthHubClient.class);
                                assertThat(context.getBean(AuthHubClient.class))
                                        .isSameAs(customClient);
                            });
        }
    }

    @Nested
    @DisplayName("API Bean")
    class ApiBeans {

        @Test
        @DisplayName("AuthApi Bean이 생성된다")
        void shouldCreateAuthApiBean() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=test-token")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(AuthApi.class);
                            });
        }

        @Test
        @DisplayName("OnboardingApi Bean이 생성된다")
        void shouldCreateOnboardingApiBean() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=test-token")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(OnboardingApi.class);
                            });
        }

        @Test
        @DisplayName("모든 API Bean이 한 번에 생성된다")
        void shouldCreateAllApiBeans() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=test-token")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(AuthApi.class);
                                assertThat(context).hasSingleBean(OnboardingApi.class);
                            });
        }
    }

    @Nested
    @DisplayName("Properties 바인딩")
    class PropertiesBinding {

        @Test
        @DisplayName("AuthHubProperties Bean이 생성된다")
        void shouldCreatePropertiesBean() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=test-token")
                    .run(
                            context -> {
                                assertThat(context).hasSingleBean(AuthHubProperties.class);
                            });
        }

        @Test
        @DisplayName("속성이 올바르게 바인딩된다")
        void shouldBindPropertiesCorrectly() {
            contextRunner
                    .withPropertyValues(
                            "authhub.base-url=https://authhub.example.com",
                            "authhub.service-token=my-service-token",
                            "authhub.timeout.connect=10s",
                            "authhub.timeout.read=60s")
                    .run(
                            context -> {
                                AuthHubProperties props = context.getBean(AuthHubProperties.class);
                                assertThat(props.getBaseUrl())
                                        .isEqualTo("https://authhub.example.com");
                                assertThat(props.getServiceToken()).isEqualTo("my-service-token");
                            });
        }
    }
}
