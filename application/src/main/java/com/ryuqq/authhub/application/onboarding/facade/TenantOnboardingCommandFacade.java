package com.ryuqq.authhub.application.onboarding.facade;

import com.ryuqq.authhub.application.onboarding.dto.bundle.TenantOnboardingPersistBundle;
import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.user.manager.command.UserRoleTransactionManager;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantOnboardingCommandFacade - 테넌트 온보딩 Command Facade
 *
 * <p>여러 TransactionManager를 조합하여 테넌트 온보딩 영속화를 관리합니다.
 *
 * <p><strong>영속화 순서:</strong>
 *
 * <ol>
 *   <li>Tenant 영속화
 *   <li>Organization 영속화
 *   <li>User 영속화
 *   <li>UserRole 영속화
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>2개 이상 TransactionManager 조합
 *   <li>{@code persist*()} 메서드 네이밍
 *   <li>메서드 레벨 {@code @Transactional}
 *   <li>비즈니스 로직 금지 (순수 영속화만)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantOnboardingCommandFacade {

    private final TenantTransactionManager tenantTransactionManager;
    private final OrganizationTransactionManager organizationTransactionManager;
    private final UserTransactionManager userTransactionManager;
    private final UserRoleTransactionManager userRoleTransactionManager;

    public TenantOnboardingCommandFacade(
            TenantTransactionManager tenantTransactionManager,
            OrganizationTransactionManager organizationTransactionManager,
            UserTransactionManager userTransactionManager,
            UserRoleTransactionManager userRoleTransactionManager) {
        this.tenantTransactionManager = tenantTransactionManager;
        this.organizationTransactionManager = organizationTransactionManager;
        this.userTransactionManager = userTransactionManager;
        this.userRoleTransactionManager = userRoleTransactionManager;
    }

    /**
     * 테넌트 온보딩 전체 영속화
     *
     * <p>Bundle의 모든 도메인 객체를 순차적으로 영속화합니다. 모든 ID가 Factory에서 미리 생성되어 있으므로 단순 영속화만 수행합니다.
     *
     * @param bundle 영속화할 Bundle (모든 ID 할당됨)
     * @return 영속화된 Bundle
     */
    @Transactional
    public TenantOnboardingPersistBundle persistOnboarding(TenantOnboardingPersistBundle bundle) {
        // 1. Tenant 영속화
        Tenant persistedTenant = tenantTransactionManager.persist(bundle.tenant());

        // 2. Organization 영속화
        Organization persistedOrganization =
                organizationTransactionManager.persist(bundle.organization());

        // 3. User 영속화
        User persistedUser = userTransactionManager.persist(bundle.user());

        // 4. UserRole 영속화
        UserRole persistedUserRole = userRoleTransactionManager.persist(bundle.userRole());

        // 5. 영속화된 객체들로 Bundle 반환
        return TenantOnboardingPersistBundle.of(
                persistedTenant,
                persistedOrganization,
                persistedUser,
                persistedUserRole,
                bundle.temporaryPassword());
    }
}
