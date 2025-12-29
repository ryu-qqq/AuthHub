package com.ryuqq.authhub.application.onboarding.factory;

import com.ryuqq.authhub.application.onboarding.dto.bundle.TenantOnboardingPersistBundle;
import com.ryuqq.authhub.application.onboarding.dto.command.TenantOnboardingCommand;
import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderPort;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * TenantOnboardingFactory - 테넌트 온보딩 Factory
 *
 * <p>Command를 Domain 객체로 변환하고 Bundle을 생성합니다.
 *
 * <p><strong>역할:</strong>
 *
 * <ul>
 *   <li>Command → Domain 변환
 *   <li>UUIDv7 기반 ID 생성
 *   <li>임시 비밀번호 생성 및 해싱
 *   <li>Bundle 생성
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code create*()} 메서드 네이밍
 *   <li>순수 변환만 수행 (영속화 금지)
 *   <li>{@code @Transactional} 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantOnboardingFactory {

    private static final String TENANT_ADMIN_ROLE_NAME = "TENANT_ADMIN";
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final String TEMP_PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";

    private final Clock clock;
    private final UuidHolder uuidHolder;
    private final PasswordEncoderPort passwordEncoderPort;
    private final RoleReadManager roleReadManager;
    private final SecureRandom secureRandom;

    public TenantOnboardingFactory(
            Clock clock,
            UuidHolder uuidHolder,
            PasswordEncoderPort passwordEncoderPort,
            RoleReadManager roleReadManager) {
        this.clock = clock;
        this.uuidHolder = uuidHolder;
        this.passwordEncoderPort = passwordEncoderPort;
        this.roleReadManager = roleReadManager;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Command → Bundle 변환
     *
     * <p>온보딩 Command를 영속화용 Bundle로 변환합니다. 모든 ID가 미리 생성되어 도메인 객체에 할당됩니다.
     *
     * <p><strong>생성 순서:</strong>
     *
     * <ol>
     *   <li>UUIDv7 기반 ID 생성 (TenantId, OrganizationId, UserId)
     *   <li>임시 비밀번호 생성 및 해싱
     *   <li>TENANT_ADMIN 역할 ID 조회
     *   <li>Tenant 생성
     *   <li>Organization 생성 (TenantId 포함)
     *   <li>User 생성 (TenantId, OrganizationId 포함)
     *   <li>UserRole 생성 (UserId, RoleId 포함)
     *   <li>Bundle로 묶음
     * </ol>
     *
     * @param command 온보딩 Command
     * @return 영속화용 Bundle (모든 ID 할당됨)
     */
    public TenantOnboardingPersistBundle createBundle(TenantOnboardingCommand command) {
        // 1. UUIDv7 기반 ID 미리 생성
        TenantId tenantId = TenantId.forNew(uuidHolder.random());
        OrganizationId organizationId = OrganizationId.forNew(uuidHolder.random());
        UserId userId = UserId.of(uuidHolder.random());

        // 2. 임시 비밀번호 생성 및 해싱
        String temporaryPassword = generateTemporaryPassword();
        String hashedPassword = passwordEncoderPort.hash(temporaryPassword);

        // 3. TENANT_ADMIN 역할 ID 조회
        RoleId tenantAdminRoleId = getTenantAdminRoleId();

        // 4. Tenant 생성
        Tenant tenant = Tenant.create(tenantId, TenantName.of(command.tenantName()), clock);

        // 5. Organization 생성 (TenantId 포함)
        Organization organization =
                Organization.create(
                        organizationId,
                        tenantId,
                        OrganizationName.of(command.organizationName()),
                        clock);

        // 6. User 생성 (TenantId, OrganizationId 포함)
        User user =
                User.create(
                        userId,
                        tenantId,
                        organizationId,
                        command.masterEmail(),
                        command.masterPhoneNumber(),
                        hashedPassword,
                        clock);

        // 7. UserRole 생성 (UserId, RoleId 포함)
        Instant now = clock.instant();
        UserRole userRole = UserRole.of(userId, tenantAdminRoleId, now);

        // 8. Bundle로 묶음
        return TenantOnboardingPersistBundle.of(
                tenant, organization, user, userRole, temporaryPassword);
    }

    /**
     * TENANT_ADMIN 역할 ID 조회
     *
     * @return TENANT_ADMIN RoleId
     */
    public RoleId getTenantAdminRoleId() {
        // GLOBAL 범위의 TENANT_ADMIN 역할 조회 (tenantId = null)
        Role tenantAdminRole =
                roleReadManager.getByTenantIdAndName(null, RoleName.of(TENANT_ADMIN_ROLE_NAME));
        return tenantAdminRole.getRoleId();
    }

    /**
     * 임시 비밀번호 생성
     *
     * @return 12자리 임시 비밀번호
     */
    private String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = secureRandom.nextInt(TEMP_PASSWORD_CHARS.length());
            sb.append(TEMP_PASSWORD_CHARS.charAt(index));
        }
        return sb.toString();
    }
}
