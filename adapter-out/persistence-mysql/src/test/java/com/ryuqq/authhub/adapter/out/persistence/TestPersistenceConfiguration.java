package com.ryuqq.authhub.adapter.out.persistence;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Persistence Layer Test Configuration.
 *
 * <p>@DataJpaTest에서 사용할 최소한의 Spring Boot 설정입니다.
 * @SpringBootConfiguration을 제공하여 테스트 컨텍스트를 부트스트랩합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@SpringBootConfiguration
@EntityScan(basePackages = "com.ryuqq.authhub.adapter.out.persistence")
@EnableJpaRepositories(basePackages = "com.ryuqq.authhub.adapter.out.persistence")
public class TestPersistenceConfiguration {
}
