package com.ryuqq.authhub.sdk.autoconfigure;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.api.UserApi;
import com.ryuqq.authhub.sdk.auth.ChainTokenResolver;
import com.ryuqq.authhub.sdk.auth.ThreadLocalTokenResolver;
import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.authhub.sdk.client.AuthHubClient;
import com.ryuqq.authhub.sdk.client.AuthHubClientBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * AuthHub SDK Spring Boot AutoConfiguration.
 *
 * <p>authhub.base-url 속성이 설정되면 자동으로 AuthHubClient를 구성합니다.
 *
 * <p>자동 구성되는 Bean:
 *
 * <ul>
 *   <li>{@link AuthHubClient} - 메인 클라이언트
 *   <li>{@link AuthApi} - 인증 API (로그인, 로그아웃, 토큰 갱신, 내 정보 조회)
 *   <li>{@link OnboardingApi} - 온보딩 API (테넌트 + 조직 일괄 생성)
 * </ul>
 *
 * <p>설정 예시:
 *
 * <pre>
 * authhub:
 *   base-url: https://auth.example.com
 *   service-token: ${AUTHHUB_SERVICE_TOKEN}
 *   timeout:
 *     connect: 5s
 *     read: 30s
 * </pre>
 */
@AutoConfiguration
@ConditionalOnClass(AuthHubClient.class)
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
@EnableConfigurationProperties(AuthHubProperties.class)
public class AuthHubAutoConfiguration {

    private final AuthHubProperties properties;

    public AuthHubAutoConfiguration(AuthHubProperties properties) {
        this.properties = properties;
    }

    /**
     * TokenResolver Bean (서비스 토큰 + ThreadLocal 체인).
     *
     * <p>authhub.service-token이 설정되면 ChainTokenResolver를 생성합니다. ThreadLocal을 우선 시도하고, 없으면 서비스 토큰을
     * 사용합니다.
     */
    @Bean
    @ConditionalOnMissingBean(TokenResolver.class)
    @ConditionalOnProperty(prefix = "authhub", name = "service-token")
    public TokenResolver tokenResolverWithServiceToken() {
        return ChainTokenResolver.withFallback(properties.getServiceToken());
    }

    /**
     * TokenResolver Bean (ThreadLocal만).
     *
     * <p>service-token이 설정되지 않은 경우 ThreadLocalTokenResolver만 사용합니다.
     */
    @Bean
    @ConditionalOnMissingBean(TokenResolver.class)
    public TokenResolver tokenResolverThreadLocalOnly() {
        return ThreadLocalTokenResolver.INSTANCE;
    }

    /** AuthHubClient Bean. */
    @Bean
    @ConditionalOnMissingBean
    public AuthHubClient authHubClient(TokenResolver tokenResolver) {
        AuthHubClientBuilder builder =
                AuthHubClient.builder()
                        .baseUrl(properties.getBaseUrl())
                        .tokenResolver(tokenResolver)
                        .connectTimeout(properties.getTimeout().getConnect())
                        .readTimeout(properties.getTimeout().getRead());

        return builder.build();
    }

    /** AuthApi Bean - 로그인, 로그아웃, 토큰 갱신, 내 정보 조회. */
    @Bean
    @ConditionalOnMissingBean
    public AuthApi authApi(AuthHubClient client) {
        return client.auth();
    }

    /** OnboardingApi Bean - 테넌트 + 조직 일괄 생성. */
    @Bean
    @ConditionalOnMissingBean
    public OnboardingApi onboardingApi(AuthHubClient client) {
        return client.onboarding();
    }

    /** UserApi Bean - 사용자 생성 + 역할 할당. */
    @Bean
    @ConditionalOnMissingBean
    public UserApi userApi(AuthHubClient client) {
        return client.user();
    }

    /**
     * AuthHubTokenContextFilter 자동 등록.
     *
     * <p>HTTP 요청에서 Authorization 헤더를 추출하여 ThreadLocal에 저장합니다.
     */
    @Bean
    @ConditionalOnMissingBean(name = "authHubTokenContextFilterRegistration")
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    public FilterRegistrationBean<AuthHubTokenContextFilter>
            authHubTokenContextFilterRegistration() {
        FilterRegistrationBean<AuthHubTokenContextFilter> registrationBean =
                new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthHubTokenContextFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registrationBean.setName("authHubTokenContextFilter");
        return registrationBean;
    }
}
