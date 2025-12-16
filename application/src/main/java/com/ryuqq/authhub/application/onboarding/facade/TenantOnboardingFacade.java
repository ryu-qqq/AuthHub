package com.ryuqq.authhub.application.onboarding.facade;

import com.ryuqq.authhub.application.organization.manager.command.OrganizationTransactionManager;
import com.ryuqq.authhub.application.tenant.manager.command.TenantTransactionManager;
import com.ryuqq.authhub.application.user.manager.command.UserRoleTransactionManager;
import com.ryuqq.authhub.application.user.manager.command.UserTransactionManager;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantOnboardingFacade - 테넌트 온보딩 트랜잭션 조율
 *
 * <p>여러 Aggregate를 단일 트랜잭션에서 영속화합니다. TenantId 참조 순서 문제로 인해 도메인 객체 생성도 함께 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional} 메서드 레벨
 *   <li>2개 이상 TransactionManager 조합
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantOnboardingFacade {

    private final TenantTransactionManager tenantTransactionManager;
    private final OrganizationTransactionManager organizationTransactionManager;
    private final UserTransactionManager userTransactionManager;
    private final UserRoleTransactionManager userRoleTransactionManager;
    private final Clock clock;

    public TenantOnboardingFacade(
            TenantTransactionManager tenantTransactionManager,
            OrganizationTransactionManager organizationTransactionManager,
            UserTransactionManager userTransactionManager,
            UserRoleTransactionManager userRoleTransactionManager,
            Clock clock) {
        this.tenantTransactionManager = tenantTransactionManager;
        this.organizationTransactionManager = organizationTransactionManager;
        this.userTransactionManager = userTransactionManager;
        this.userRoleTransactionManager = userRoleTransactionManager;
        this.clock = clock;
    }

    /**
     * 테넌트 온보딩 리소스 일괄 생성
     *
     * <p>Tenant → Organization → User → UserRole 순서로 생성 및 영속화합니다. TenantId 참조 순서로 인해 도메인 객체 생성이
     * 트랜잭션 내에서 수행됩니다.
     *
     * @param tenantName 테넌트 이름
     * @param organizationName 조직 이름
     * @param userIdentifier 사용자 식별자 (이메일)
     * @param hashedPassword 해시된 비밀번호
     * @param adminRole ADMIN 역할
     * @return 영속화된 결과
     */
    @Transactional
    public OnboardingPersistenceResult persistAll(
            String tenantName,
            String organizationName,
            String userIdentifier,
            String hashedPassword,
            Role adminRole) {

        // 1. Tenant 생성 및 영속화
        Tenant tenant = Tenant.create(TenantName.of(tenantName), clock);
        Tenant savedTenant = tenantTransactionManager.persist(tenant);
        TenantId tenantId = savedTenant.getTenantId();

        // 2. Organization 생성 및 영속화 (savedTenant의 ID 사용)
        Organization organization =
                Organization.create(tenantId, OrganizationName.of(organizationName), clock);
        Organization savedOrganization = organizationTransactionManager.persist(organization);
        OrganizationId organizationId = savedOrganization.getOrganizationId();

        // 3. User 생성 및 영속화
        UserId userId = UserId.of(UUID.randomUUID());
        User user =
                User.create(
                        userId, tenantId, organizationId, userIdentifier, hashedPassword, clock);
        User savedUser = userTransactionManager.persist(user);

        // 4. UserRole 생성 및 영속화
        UserRole userRole =
                UserRole.of(savedUser.getUserId(), adminRole.getRoleId(), clock.instant());
        userRoleTransactionManager.persist(userRole);

        return OnboardingPersistenceResult.of(savedTenant, savedOrganization, savedUser);
    }

    /**
     * 온보딩 영속화 결과 - 내부 전달용
     *
     * <p><strong>Law of Demeter 준수:</strong> 편의 메서드를 통해 내부 객체의 값에 직접 접근할 수 있습니다.
     */
    public record OnboardingPersistenceResult(Tenant tenant, Organization organization, User user) {

        public static OnboardingPersistenceResult of(
                Tenant tenant, Organization organization, User user) {
            return new OnboardingPersistenceResult(tenant, organization, user);
        }

        /** 테넌트 ID 값 반환 (Law of Demeter 준수) */
        public UUID tenantIdValue() {
            return tenant.getTenantId().value();
        }

        /** 조직 ID 값 반환 (Law of Demeter 준수) */
        public UUID organizationIdValue() {
            return organization.getOrganizationId().value();
        }

        /** 사용자 ID 값 반환 (Law of Demeter 준수) */
        public UUID userIdValue() {
            return user.getUserId().value();
        }
    }
}
