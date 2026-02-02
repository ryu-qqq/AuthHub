package com.ryuqq.authhub.integration.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Repository 통합 테스트 설정.
 *
 * <p>DataJpaTest에서 QueryDslRepository 테스트에 필요한 빈을 등록합니다.
 *
 * <ul>
 *   <li>JPAQueryFactory - QueryDSL 쿼리 실행
 *   <li>ConditionBuilder - 검색 조건 빌더
 *   <li>QueryDslRepository - QueryDSL 기반 Repository
 * </ul>
 */
@TestConfiguration
@ComponentScan(
        basePackages = {
            "com.ryuqq.authhub.adapter.out.persistence.user.repository",
            "com.ryuqq.authhub.adapter.out.persistence.user.condition",
            "com.ryuqq.authhub.adapter.out.persistence.tenant.repository",
            "com.ryuqq.authhub.adapter.out.persistence.tenant.condition",
            "com.ryuqq.authhub.adapter.out.persistence.organization.repository",
            "com.ryuqq.authhub.adapter.out.persistence.organization.condition",
            "com.ryuqq.authhub.adapter.out.persistence.role.repository",
            "com.ryuqq.authhub.adapter.out.persistence.role.condition",
            "com.ryuqq.authhub.adapter.out.persistence.permission.repository",
            "com.ryuqq.authhub.adapter.out.persistence.permission.condition",
            "com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository",
            "com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.condition",
            "com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository",
            "com.ryuqq.authhub.adapter.out.persistence.rolepermission.condition",
            "com.ryuqq.authhub.adapter.out.persistence.userrole.repository",
            "com.ryuqq.authhub.adapter.out.persistence.userrole.condition",
            "com.ryuqq.authhub.adapter.out.persistence.token.repository",
            "com.ryuqq.authhub.adapter.out.persistence.token.condition"
        })
public class RepositoryTestConfig {

    /**
     * QueryDSL용 JPAQueryFactory 빈 등록.
     *
     * @param entityManager EntityManager
     * @return JPAQueryFactory
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
