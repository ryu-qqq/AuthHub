package com.ryuqq.authhub.application.onboarding.service;

import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.onboarding.dto.response.TenantOnboardingResponse;
import com.ryuqq.authhub.application.onboarding.port.in.TenantOnboardingUseCase;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.port.in.command.CreateOrganizationUseCase;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.response.TenantResponse;
import com.ryuqq.authhub.application.tenant.port.in.command.CreateTenantUseCase;
import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.in.command.AssignUserRoleUseCase;
import com.ryuqq.authhub.application.user.port.in.command.CreateUserUseCase;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import java.security.SecureRandom;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantOnboardingFacade - 테넌트 온보딩 Facade 서비스
 *
 * <p>입점 승인 시 Tenant, Organization, User를 일괄 생성하는 Facade입니다.
 *
 * <p><strong>온보딩 프로세스:</strong>
 *
 * <ol>
 *   <li>Tenant 생성 (ACTIVE 상태)
 *   <li>기본 Organization 생성
 *   <li>임시 비밀번호 생성
 *   <li>마스터 User 생성
 *   <li>TENANT_ADMIN 역할 할당
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Facade에서 {@code @Transactional} 관리
 *   <li>UseCase 조합을 통한 복합 비즈니스 로직 처리
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class TenantOnboardingFacade implements TenantOnboardingUseCase {

    private static final String TENANT_ADMIN_ROLE_NAME = "TENANT_ADMIN";
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final String TEMP_PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";

    private final CreateTenantUseCase createTenantUseCase;
    private final CreateOrganizationUseCase createOrganizationUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final AssignUserRoleUseCase assignUserRoleUseCase;
    private final RoleReadManager roleReadManager;
    private final SecureRandom secureRandom;

    public TenantOnboardingFacade(
            CreateTenantUseCase createTenantUseCase,
            CreateOrganizationUseCase createOrganizationUseCase,
            CreateUserUseCase createUserUseCase,
            AssignUserRoleUseCase assignUserRoleUseCase,
            RoleReadManager roleReadManager) {
        this.createTenantUseCase = createTenantUseCase;
        this.createOrganizationUseCase = createOrganizationUseCase;
        this.createUserUseCase = createUserUseCase;
        this.assignUserRoleUseCase = assignUserRoleUseCase;
        this.roleReadManager = roleReadManager;
        this.secureRandom = new SecureRandom();
    }

    @Override
    @Transactional
    public TenantOnboardingResponse execute(TenantOnboardingCommand command) {
        // 1. Tenant 생성
        TenantResponse tenant = createTenant(command.tenantName());

        // 2. Organization 생성
        OrganizationResponse organization =
                createOrganization(tenant.tenantId(), command.organizationName());

        // 3. 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();

        // 4. User 생성
        UserResponse user =
                createUser(
                        tenant.tenantId(),
                        organization.organizationId(),
                        command.masterEmail(),
                        temporaryPassword);

        // 5. TENANT_ADMIN 역할 할당
        assignTenantAdminRole(user.userId());

        return new TenantOnboardingResponse(
                tenant.tenantId(), organization.organizationId(), user.userId(), temporaryPassword);
    }

    private TenantResponse createTenant(String tenantName) {
        CreateTenantCommand command = new CreateTenantCommand(tenantName);
        return createTenantUseCase.execute(command);
    }

    private OrganizationResponse createOrganization(UUID tenantId, String organizationName) {
        CreateOrganizationCommand command =
                new CreateOrganizationCommand(tenantId, organizationName);
        return createOrganizationUseCase.execute(command);
    }

    private UserResponse createUser(
            UUID tenantId, UUID organizationId, String email, String password) {
        CreateUserCommand command =
                new CreateUserCommand(tenantId, organizationId, email, password);
        return createUserUseCase.execute(command);
    }

    private void assignTenantAdminRole(UUID userId) {
        // GLOBAL 범위의 TENANT_ADMIN 역할 조회 (tenantId = null)
        Role tenantAdminRole =
                roleReadManager.getByTenantIdAndName(null, RoleName.of(TENANT_ADMIN_ROLE_NAME));
        AssignUserRoleCommand command =
                new AssignUserRoleCommand(userId, tenantAdminRole.roleIdValue());
        assignUserRoleUseCase.execute(command);
    }

    private String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = secureRandom.nextInt(TEMP_PASSWORD_CHARS.length());
            sb.append(TEMP_PASSWORD_CHARS.charAt(index));
        }
        return sb.toString();
    }
}
