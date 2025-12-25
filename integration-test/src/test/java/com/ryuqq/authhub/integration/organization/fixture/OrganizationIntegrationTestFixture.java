package com.ryuqq.authhub.integration.organization.fixture;

import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;

/**
 * 조직 통합 테스트 Fixture
 *
 * <p>API Request/Response 객체 생성 유틸리티
 *
 * @author Development Team
 * @since 1.0.0
 */
public final class OrganizationIntegrationTestFixture {

    private OrganizationIntegrationTestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ========================================
    // 조직 생성 요청
    // ========================================
    public static CreateOrganizationApiRequest createOrganizationRequest(String tenantId) {
        return createOrganizationRequest(tenantId, "Test Organization");
    }

    public static CreateOrganizationApiRequest createOrganizationRequest(
            String tenantId, String name) {
        return new CreateOrganizationApiRequest(tenantId, name);
    }

    public static CreateOrganizationApiRequest createOrganizationRequestWithUniqueName(
            String tenantId) {
        return new CreateOrganizationApiRequest(tenantId, "Test Org " + System.currentTimeMillis());
    }

    // ========================================
    // 조직 수정 요청
    // ========================================
    public static UpdateOrganizationApiRequest updateOrganizationRequest() {
        return updateOrganizationRequest("Updated Organization Name");
    }

    public static UpdateOrganizationApiRequest updateOrganizationRequest(String name) {
        return new UpdateOrganizationApiRequest(name);
    }

    // ========================================
    // 조직 상태 변경 요청
    // ========================================
    public static UpdateOrganizationStatusApiRequest activateOrganizationRequest() {
        return new UpdateOrganizationStatusApiRequest("ACTIVE");
    }

    public static UpdateOrganizationStatusApiRequest deactivateOrganizationRequest() {
        return new UpdateOrganizationStatusApiRequest("INACTIVE");
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================
    public static CreateOrganizationApiRequest createOrganizationRequestWithEmptyTenantId() {
        return new CreateOrganizationApiRequest("", "Test Organization");
    }

    public static CreateOrganizationApiRequest createOrganizationRequestWithEmptyName(
            String tenantId) {
        return new CreateOrganizationApiRequest(tenantId, "");
    }
}
