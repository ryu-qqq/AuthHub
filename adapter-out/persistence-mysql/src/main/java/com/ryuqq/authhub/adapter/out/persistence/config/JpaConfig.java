package com.ryuqq.setof.adapter.out.persistence.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 및 QueryDSL 설정
 *
 * @author Development Team
 * @since 1.0.0
 */
@Configuration
@EntityScan(basePackages = "com.ryuqq.authhub.adapter.out.persistence")
@EnableJpaRepositories(basePackages = "com.ryuqq.authhub.adapter.out.persistence")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

    /**
     * Persistence Layer 전용 ObjectMapper
     *
     * <p>Persistence Layer에서 JSON 파싱/직렬화를 위한 ObjectMapper입니다. PersistenceObjectMapper 래퍼를 통해 에러 처리와
     * 함께 사용됩니다.
     *
     * @return ObjectMapper
     */
    @Bean(name = "persistenceJsonObjectMapper")
    public ObjectMapper persistenceJsonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
