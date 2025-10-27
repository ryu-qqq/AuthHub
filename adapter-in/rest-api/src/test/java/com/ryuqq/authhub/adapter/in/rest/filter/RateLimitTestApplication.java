package com.ryuqq.authhub.adapter.in.rest.filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * RateLimitFilter 통합 테스트용 Spring Boot Application.
 *
 * <p>통합 테스트 실행을 위한 최소한의 Spring Boot 애플리케이션입니다.
 * 실제 프로덕션 코드에는 사용되지 않으며, 테스트 환경에서만 로딩됩니다.</p>
 *
 * <p><strong>ComponentScan 범위:</strong></p>
 * <ul>
 *   <li>com.ryuqq.authhub.adapter.in.rest - REST API Adapter 컴포넌트</li>
 *   <li>com.ryuqq.authhub.application - Application Layer 컴포넌트</li>
 *   <li>com.ryuqq.authhub.adapter.out.persistence - Persistence Adapter 컴포넌트</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.ryuqq.authhub.adapter.in.rest",
        "com.ryuqq.authhub.application",
        "com.ryuqq.authhub.adapter.out.persistence"
})
public class RateLimitTestApplication {

    /**
     * 테스트 애플리케이션 메인 메서드.
     *
     * @param args 커맨드 라인 인자
     */
    public static void main(final String[] args) {
        SpringApplication.run(RateLimitTestApplication.class, args);
    }
}
