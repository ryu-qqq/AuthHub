package com.ryuqq.authhub.application.tenant.fixture;

import com.ryuqq.authhub.application.tenant.dto.bundle.OnboardingBundle;
import com.ryuqq.authhub.application.tenant.dto.command.CreateTenantCommand;
import com.ryuqq.authhub.application.tenant.dto.command.OnboardingCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantNameCommand;
import com.ryuqq.authhub.application.tenant.dto.command.UpdateTenantStatusCommand;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;

/**
 * Tenant Command DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Command DTO를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantCommandFixtures {

    private static final String DEFAULT_TENANT_ID = TenantFixture.defaultIdString();
    private static final String DEFAULT_NAME = "Test Tenant";
    private static final String DEFAULT_ORG_NAME = "Test Organization";

    private TenantCommandFixtures() {}

    /** 기본 생성 Command 반환 */
    public static CreateTenantCommand createCommand() {
        return new CreateTenantCommand(DEFAULT_NAME);
    }

    /** 지정된 이름으로 생성 Command 반환 */
    public static CreateTenantCommand createCommandWithName(String name) {
        return new CreateTenantCommand(name);
    }

    /** 기본 이름 수정 Command 반환 */
    public static UpdateTenantNameCommand updateNameCommand() {
        return new UpdateTenantNameCommand(DEFAULT_TENANT_ID, "Updated Tenant");
    }

    /** 지정된 값으로 이름 수정 Command 반환 */
    public static UpdateTenantNameCommand updateNameCommand(String tenantId, String newName) {
        return new UpdateTenantNameCommand(tenantId, newName);
    }

    /** 비활성화 Command 반환 */
    public static UpdateTenantStatusCommand deactivateCommand() {
        return new UpdateTenantStatusCommand(DEFAULT_TENANT_ID, "INACTIVE");
    }

    /** 활성화 Command 반환 */
    public static UpdateTenantStatusCommand activateCommand() {
        return new UpdateTenantStatusCommand(DEFAULT_TENANT_ID, "ACTIVE");
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static String defaultName() {
        return DEFAULT_NAME;
    }

    // ==================== Onboarding Fixtures ====================

    private static final String DEFAULT_IDEMPOTENCY_KEY = "test-idempotency-key-12345";

    /** 기본 온보딩 Command 반환 */
    public static OnboardingCommand createOnboardingCommand() {
        return new OnboardingCommand(DEFAULT_NAME, DEFAULT_ORG_NAME, DEFAULT_IDEMPOTENCY_KEY);
    }

    /** 지정된 값으로 온보딩 Command 반환 */
    public static OnboardingCommand createOnboardingCommand(
            String tenantName, String organizationName) {
        return new OnboardingCommand(tenantName, organizationName, DEFAULT_IDEMPOTENCY_KEY);
    }

    /** 지정된 값으로 온보딩 Command 반환 (멱등키 포함) */
    public static OnboardingCommand createOnboardingCommand(
            String tenantName, String organizationName, String idempotencyKey) {
        return new OnboardingCommand(tenantName, organizationName, idempotencyKey);
    }

    /** 기본 온보딩 번들 반환 (Tenant + Organization) */
    public static OnboardingBundle createOnboardingBundle() {
        Tenant tenant = TenantFixture.create();
        Organization organization = OrganizationFixture.createWithTenant(tenant.tenantIdValue());
        return new OnboardingBundle(tenant, organization);
    }

    /** 지정된 Tenant와 Organization으로 온보딩 번들 반환 */
    public static OnboardingBundle createOnboardingBundle(
            Tenant tenant, Organization organization) {
        return new OnboardingBundle(tenant, organization);
    }
}
