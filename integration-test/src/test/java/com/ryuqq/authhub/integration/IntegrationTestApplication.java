package com.ryuqq.authhub.integration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 통합 테스트 전용 Spring Boot Application.
 *
 * <p>@DataJpaTest가 @SpringBootConfiguration을 찾을 수 있도록 제공. 실제 애플리케이션 로직은 포함하지 않음.
 */
@SpringBootApplication(
        scanBasePackages = {
            "com.ryuqq.authhub.adapter.out.persistence",
            "com.ryuqq.authhub.integration.common.config"
        })
@EnableJpaRepositories(basePackages = "com.ryuqq.authhub.adapter.out.persistence")
@EntityScan(basePackages = "com.ryuqq.authhub.adapter.out.persistence")
public class IntegrationTestApplication {}
