package com.ryuqq.authhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * AuthHub Application Entry Point.
 *
 * <p>Spring Boot 애플리케이션의 시작점입니다.
 * JWT 기반 인증 시스템을 제공하는 REST API 서버를 구동합니다.</p>
 *
 * <p><strong>아키텍처 패턴:</strong></p>
 * <ul>
 *   <li>Hexagonal Architecture (Ports & Adapters)</li>
 *   <li>Domain-Driven Design (DDD)</li>
 *   <li>CQRS (Command Query Responsibility Segregation)</li>
 * </ul>
 *
 * <p><strong>모듈 구조:</strong></p>
 * <ul>
 *   <li>domain: 핵심 비즈니스 로직 및 Aggregate</li>
 *   <li>application: UseCase 및 Port 정의</li>
 *   <li>adapter-in/rest-api: REST API Controller</li>
 *   <li>adapter-out/persistence-mysql: JPA Repository 구현</li>
 *   <li>adapter-out/persistence-redis: Redis Token Storage</li>
 *   <li>bootstrap: Spring Boot 애플리케이션 구성 (현재 모듈)</li>
 * </ul>
 *
 * <p><strong>Component Scan 범위:</strong></p>
 * <ul>
 *   <li>{@code com.ryuqq.authhub} - 모든 서브 패키지 스캔</li>
 *   <li>REST Controllers, Services, Repositories, Adapters 자동 등록</li>
 * </ul>
 *
 * <p><strong>Configuration Properties Scan:</strong></p>
 * <ul>
 *   <li>{@code @ConfigurationProperties} 클래스 자동 스캔 및 등록</li>
 *   <li>예: JwtProperties, CacheProperties 등</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.ryuqq.authhub")
@ConfigurationPropertiesScan("com.ryuqq.authhub")
public class AuthHubApplication {

    /**
     * Spring Boot 애플리케이션 시작점.
     *
     * <p>내장 Tomcat 서버를 구동하고 Spring Context를 초기화합니다.
     * 기본 포트는 8080이며, application.yml에서 변경 가능합니다.</p>
     *
     * <p><strong>시작 프로세스:</strong></p>
     * <ol>
     *   <li>Spring Context 초기화</li>
     *   <li>Component Scan 및 Bean 등록</li>
     *   <li>Auto-Configuration 실행</li>
     *   <li>DataSource 및 JPA 설정</li>
     *   <li>Redis Connection 설정</li>
     *   <li>Embedded Tomcat 시작</li>
     * </ol>
     *
     * @param args 커맨드 라인 인자
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static void main(final String[] args) {
        SpringApplication.run(AuthHubApplication.class, args);
    }
}
