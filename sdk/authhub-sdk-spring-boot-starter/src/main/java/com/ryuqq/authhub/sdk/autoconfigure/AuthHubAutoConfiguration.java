package com.ryuqq.authhub.sdk.autoconfigure;

import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.api.OrganizationApi;
import com.ryuqq.authhub.sdk.api.PermissionApi;
import com.ryuqq.authhub.sdk.api.RoleApi;
import com.ryuqq.authhub.sdk.api.TenantApi;
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
 * AuthHub SDK Spring Boot AutoConfiguration. authhub.base-url 속성이 설정되면 자동으로 AuthHubClient를 구성합니다.
 *
 * <p>자동 구성되는 Bean:
 *
 * <ul>
 *   <li>{@link AuthHubClient} - 메인 클라이언트
 *   <li>{@link TenantApi} - 테넌트 API
 *   <li>{@link OrganizationApi} - 조직 API
 *   <li>{@link RoleApi} - 역할 API
 *   <li>{@link UserApi} - 사용자 API
 *   <li>{@link PermissionApi} - 권한 API
 *   <li>{@link OnboardingApi} - 온보딩 API
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
     * TokenResolver Bean (서비스 토큰 + ThreadLocal 체인). authhub.service-token이 설정되면 ChainTokenResolver를
     * 생성합니다. ThreadLocal을 우선 시도하고, 없으면 서비스 토큰을 사용합니다.
     */
    @Bean
    @ConditionalOnMissingBean(TokenResolver.class)
    @ConditionalOnProperty(prefix = "authhub", name = "service-token")
    public TokenResolver tokenResolverWithServiceToken() {
        return ChainTokenResolver.withFallback(properties.getServiceToken());
    }

    /**
     * TokenResolver Bean (ThreadLocal만). service-token이 설정되지 않은 경우 ThreadLocalTokenResolver만 사용합니다.
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

    @Bean
    @ConditionalOnMissingBean
    public TenantApi tenantApi(AuthHubClient client) {
        return client.tenants();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrganizationApi organizationApi(AuthHubClient client) {
        return client.organizations();
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleApi roleApi(AuthHubClient client) {
        return client.roles();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserApi userApi(AuthHubClient client) {
        return client.users();
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionApi permissionApi(AuthHubClient client) {
        return client.permissions();
    }

    @Bean
    @ConditionalOnMissingBean
    public OnboardingApi onboardingApi(AuthHubClient client) {
        return client.onboarding();
    }

    /** AuthHubTokenContextFilter 자동 등록. HTTP 요청에서 Authorization 헤더를 추출하여 ThreadLocal에 저장합니다. */
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
