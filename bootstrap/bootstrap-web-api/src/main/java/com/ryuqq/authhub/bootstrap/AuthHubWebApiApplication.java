package com.ryuqq.authhub.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Spring Standards Web API Application
 *
 * <p>Hexagonal Architecture 기반 Spring Boot 애플리케이션의 진입점입니다.
 *
 * <p><strong>아키텍처 구조:</strong>
 *
 * <ul>
 *   <li><strong>Domain Layer:</strong> 비즈니스 로직과 도메인 모델
 *   <li><strong>Application Layer:</strong> Use Case 구현 (CQRS 패턴)
 *   <li><strong>Adapter-In:</strong> REST API 컨트롤러 (Inbound)
 *   <li><strong>Adapter-Out:</strong> 데이터베이스, 외부 API 연동 (Outbound)
 *   <li><strong>Bootstrap:</strong> 애플리케이션 실행 및 빈 와이어링
 * </ul>
 *
 * <p><strong>Component Scan 범위:</strong>
 *
 * <ul>
 *   <li>com.ryuqq.authhub.domain
 *   <li>com.ryuqq.authhub.application
 *   <li>com.ryuqq.authhub.adapter.in.rest
 *   <li>com.ryuqq.authhub.adapter.out.persistence
 *   <li>com.ryuqq.bootstrap
 * </ul>
 *
 * <p><strong>실행 방법:</strong>
 *
 * <pre>{@code
 * # Gradle로 실행
 * ./gradlew :bootstrap:bootstrap-web-api:bootRun
 *
 * # JAR로 실행
 * java -jar bootstrap/bootstrap-web-api/build/libs/spring-standards-web-api.jar
 *
 * # Profile 지정
 * ./gradlew :bootstrap:bootstrap-web-api:bootRun --args='--spring.profiles.active=dev'
 * }</pre>
 *
 * @author windsurf
 * @since 1.0.0
 */
@SpringBootApplication(
        scanBasePackages = {
            "com.ryuqq.authhub.domain",
            "com.ryuqq.authhub.application",
            "com.ryuqq.authhub.adapter.in.rest",
            "com.ryuqq.authhub.adapter.out.persistence",
            "com.ryuqq.authhub.adapter.out.client",
            "com.ryuqq.bootstrap"
        })
@EnableJpaRepositories(basePackages = "com.ryuqq.authhub.adapter.out.persistence")
@EntityScan(basePackages = "com.ryuqq.authhub.adapter.out.persistence")
public class AuthHubWebApiApplication {

    /**
     * 애플리케이션 진입점
     *
     * @param args 커맨드 라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthHubWebApiApplication.class, args);
    }
}
