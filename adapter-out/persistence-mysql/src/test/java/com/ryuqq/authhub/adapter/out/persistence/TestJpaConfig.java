package com.ryuqq.authhub.adapter.out.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * TestJpaConfig - Persistence Layer 테스트 설정
 *
 * <p>@DataJpaTest가 @SpringBootConfiguration을 찾을 수 있도록 테스트 전용 설정 클래스를 제공합니다.
 *
 * <p><strong>주요 기능:</strong>
 *
 * <ul>
 *   <li>@SpringBootApplication 마커 제공
 *   <li>JPAQueryFactory 빈 등록 (QueryDSL 테스트)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@SpringBootApplication
public class TestJpaConfig {

    /**
     * JPAQueryFactory 빈 등록 (테스트용)
     *
     * @param entityManager JPA EntityManager
     * @return JPAQueryFactory 인스턴스
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
