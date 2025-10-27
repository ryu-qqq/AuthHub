package com.ryuqq.authhub.config;

import com.ryuqq.authhub.adapter.in.rest.filter.RateLimitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Security Filter 설정 클래스.
 *
 * <p>Spring Boot 애플리케이션의 보안 필터를 설정하는 Configuration 클래스입니다.
 * RateLimitFilter를 포함한 보안 관련 필터들을 Bean으로 등록하고 URL 패턴과 실행 순서를 정의합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>RateLimitFilter Bean 등록 및 URL 패턴 설정</li>
 *   <li>필터 실행 순서 정의 (Order)</li>
 *   <li>향후 추가될 보안 필터 설정 (AuditLogFilter, BlacklistCheckFilter 등)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Bootstrap Layer - 애플리케이션 설정 관심사</li>
 *   <li>✅ @Configuration 사용 - Spring Bean 설정</li>
 * </ul>
 *
 * <p><strong>필터 실행 순서:</strong></p>
 * <ol>
 *   <li>Order 1: RateLimitFilter (가장 먼저 실행, 과도한 요청 차단)</li>
 *   <li>Order 2: (향후) BlacklistCheckFilter (블랙리스트 토큰 확인)</li>
 *   <li>Order 3: (향후) AuditLogFilter (감사 로그 기록)</li>
 *   <li>Order 4: (향후) SecurityHeaderFilter (보안 헤더 추가)</li>
 * </ol>
 *
 * <p><strong>URL 패턴:</strong></p>
 * <ul>
 *   <li>/api/* - 모든 API 엔드포인트에 Rate Limiting 적용</li>
 *   <li>/actuator/* - 제외 (모니터링 엔드포인트는 제한 없음)</li>
 *   <li>/health - 제외 (헬스체크는 제한 없음)</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // Spring Boot 자동 설정
 * // 애플리케이션 시작 시 자동으로 RateLimitFilter가 등록됨
 *
 * // 필터 적용 URL
 * GET /api/v1/users → RateLimitFilter 적용 ✅
 * GET /api/v1/auth/login → RateLimitFilter 적용 ✅
 * GET /actuator/health → RateLimitFilter 미적용 ❌
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Configuration
public class SecurityFilterConfig {

    /**
     * API 엔드포인트 URL 패턴.
     * 모든 /api/* 경로에 Rate Limiting을 적용합니다.
     */
    private static final String API_URL_PATTERN = "/api/*";

    /**
     * RateLimitFilter 실행 순서.
     * 가장 먼저 실행되어 과도한 요청을 조기에 차단합니다.
     */
    private static final int RATE_LIMIT_FILTER_ORDER = 1;

    /**
     * RateLimitFilter를 등록하는 FilterRegistrationBean을 생성합니다.
     *
     * <p>Spring Boot의 FilterRegistrationBean을 사용하여
     * RateLimitFilter의 URL 패턴과 실행 순서를 명시적으로 설정합니다.</p>
     *
     * <p><strong>설정 내용:</strong></p>
     * <ul>
     *   <li>Filter: RateLimitFilter (Spring Bean으로 주입)</li>
     *   <li>URL Pattern: /api/* (모든 API 엔드포인트)</li>
     *   <li>Order: 1 (가장 먼저 실행)</li>
     * </ul>
     *
     * <p><strong>Order 설정 이유:</strong></p>
     * <ul>
     *   <li>Rate Limiting을 가장 먼저 적용하여 리소스 낭비 방지</li>
     *   <li>제한 초과 요청은 다른 필터를 거치지 않고 즉시 차단</li>
     *   <li>성능 최적화 - 불필요한 처리 최소화</li>
     * </ul>
     *
     * @param rateLimitFilter RateLimitFilter Bean (Spring에서 자동 주입)
     * @return FilterRegistrationBean<RateLimitFilter> 등록된 필터 Bean
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(
            final RateLimitFilter rateLimitFilter
    ) {
        final FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();

        // 1. Filter 설정
        registration.setFilter(rateLimitFilter);

        // 2. URL 패턴 설정 (모든 API 엔드포인트)
        registration.addUrlPatterns(API_URL_PATTERN);

        // 3. 실행 순서 설정 (가장 먼저 실행)
        registration.setOrder(RATE_LIMIT_FILTER_ORDER);

        // 4. 필터 이름 설정 (디버깅 및 로깅용)
        registration.setName("rateLimitFilter");

        return registration;
    }
}
