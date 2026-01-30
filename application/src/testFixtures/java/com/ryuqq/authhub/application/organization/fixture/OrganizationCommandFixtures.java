package com.ryuqq.authhub.application.organization.fixture;

import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationNameCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;

/**
 * Organization Command DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Command DTO를 제공합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Domain Fixture와 일관된 기본값 사용
 *   <li>테스트 가독성을 위한 명확한 팩토리 메서드
 *   <li>불변 객체 반환으로 테스트 격리 보장
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationCommandFixtures {

    private static final String DEFAULT_TENANT_ID = OrganizationFixture.defaultTenantIdString();
    private static final String DEFAULT_ORG_ID = OrganizationFixture.defaultIdString();
    private static final String DEFAULT_NAME = "Test Organization";

    private OrganizationCommandFixtures() {}

    // ==================== CreateOrganizationCommand ====================

    /**
     * 기본 생성 Command 반환
     *
     * @return CreateOrganizationCommand
     */
    public static CreateOrganizationCommand createCommand() {
        return new CreateOrganizationCommand(DEFAULT_TENANT_ID, DEFAULT_NAME);
    }

    /**
     * 지정된 이름으로 생성 Command 반환
     *
     * @param name 조직 이름
     * @return CreateOrganizationCommand
     */
    public static CreateOrganizationCommand createCommandWithName(String name) {
        return new CreateOrganizationCommand(DEFAULT_TENANT_ID, name);
    }

    /**
     * 지정된 테넌트 ID로 생성 Command 반환
     *
     * @param tenantId 테넌트 ID
     * @return CreateOrganizationCommand
     */
    public static CreateOrganizationCommand createCommandWithTenant(String tenantId) {
        return new CreateOrganizationCommand(tenantId, DEFAULT_NAME);
    }

    /**
     * 모든 값을 지정하여 생성 Command 반환
     *
     * @param tenantId 테넌트 ID
     * @param name 조직 이름
     * @return CreateOrganizationCommand
     */
    public static CreateOrganizationCommand createCommand(String tenantId, String name) {
        return new CreateOrganizationCommand(tenantId, name);
    }

    // ==================== UpdateOrganizationNameCommand ====================

    /**
     * 기본 이름 수정 Command 반환
     *
     * @return UpdateOrganizationNameCommand
     */
    public static UpdateOrganizationNameCommand updateNameCommand() {
        return new UpdateOrganizationNameCommand(DEFAULT_ORG_ID, "Updated Organization");
    }

    /**
     * 지정된 값으로 이름 수정 Command 반환
     *
     * @param organizationId 조직 ID
     * @param newName 새 이름
     * @return UpdateOrganizationNameCommand
     */
    public static UpdateOrganizationNameCommand updateNameCommand(
            String organizationId, String newName) {
        return new UpdateOrganizationNameCommand(organizationId, newName);
    }

    // ==================== UpdateOrganizationStatusCommand ====================

    /**
     * 비활성화 Command 반환
     *
     * @return UpdateOrganizationStatusCommand (status = INACTIVE)
     */
    public static UpdateOrganizationStatusCommand deactivateCommand() {
        return new UpdateOrganizationStatusCommand(DEFAULT_ORG_ID, "INACTIVE");
    }

    /**
     * 활성화 Command 반환
     *
     * @return UpdateOrganizationStatusCommand (status = ACTIVE)
     */
    public static UpdateOrganizationStatusCommand activateCommand() {
        return new UpdateOrganizationStatusCommand(DEFAULT_ORG_ID, "ACTIVE");
    }

    /**
     * 지정된 조직 ID와 상태로 Command 반환
     *
     * @param organizationId 조직 ID
     * @param status 상태 (ACTIVE, INACTIVE)
     * @return UpdateOrganizationStatusCommand
     */
    public static UpdateOrganizationStatusCommand statusCommand(
            String organizationId, String status) {
        return new UpdateOrganizationStatusCommand(organizationId, status);
    }

    // ==================== 기본값 접근자 ====================

    /**
     * 기본 테넌트 ID 반환
     *
     * @return 테넌트 ID 문자열
     */
    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    /**
     * 기본 조직 ID 반환
     *
     * @return 조직 ID 문자열
     */
    public static String defaultOrganizationId() {
        return DEFAULT_ORG_ID;
    }

    /**
     * 기본 조직 이름 반환
     *
     * @return 조직 이름
     */
    public static String defaultName() {
        return DEFAULT_NAME;
    }
}
