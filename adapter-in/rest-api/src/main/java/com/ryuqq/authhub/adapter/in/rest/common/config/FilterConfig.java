package com.ryuqq.authhub.adapter.in.rest.common.config;

import com.ryuqq.authhub.adapter.in.rest.common.filter.RequestResponseLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Filter 등록 설정
 *
 * <p>REST API에서 사용하는 서블릿 필터들을 등록합니다.
 *
 * <p>필터 실행 순서:
 *
 * <ol>
 *   <li>RequestResponseLoggingFilter (가장 먼저 - 요청/응답 로깅)
 *   <li>Security Filters (Spring Security)
 *   <li>기타 필터들
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
public class FilterConfig {

    /**
     * Request/Response 로깅 필터 등록
     *
     * <p>모든 요청에 대해 로깅하며, MDC에 추적 정보를 설정합니다. Security 필터보다 먼저 실행되어야 하므로 가장 높은 우선순위로 설정합니다.
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilter() {
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean =
                new FilterRegistrationBean<>();

        registrationBean.setFilter(new RequestResponseLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.setName("requestResponseLoggingFilter");

        return registrationBean;
    }
}
