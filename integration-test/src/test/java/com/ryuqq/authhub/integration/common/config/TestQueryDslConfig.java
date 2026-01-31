package com.ryuqq.authhub.integration.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 테스트용 QueryDSL 설정.
 *
 * <p>E2E 테스트에서 QueryDSL의 JPAQueryFactory 빈을 제공합니다.
 */
@TestConfiguration
public class TestQueryDslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
