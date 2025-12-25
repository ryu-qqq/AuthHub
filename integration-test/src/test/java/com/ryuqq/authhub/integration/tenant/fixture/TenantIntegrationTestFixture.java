package com.ryuqq.authhub.integration.tenant.fixture;

import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.command.UpdateTenantStatusApiRequest;

/**
 * 테넌트 통합 테스트 Fixture
 *
 * <p>API Request/Response 객체 생성 유틸리티
 *
 * @author Development Team
 * @since 1.0.0
 */
public final class TenantIntegrationTestFixture {

    private TenantIntegrationTestFixture() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // ========================================
    // 테넌트 생성 요청
    // ========================================
    public static CreateTenantApiRequest createTenantRequest() {
        return createTenantRequest("Test Tenant");
    }

    public static CreateTenantApiRequest createTenantRequest(String name) {
        return new CreateTenantApiRequest(name);
    }

    public static CreateTenantApiRequest createTenantRequestWithUniqueName() {
        return new CreateTenantApiRequest("Test Tenant " + System.currentTimeMillis());
    }

    // ========================================
    // 테넌트 이름 수정 요청
    // ========================================
    public static UpdateTenantNameApiRequest updateTenantNameRequest() {
        return updateTenantNameRequest("Updated Tenant Name");
    }

    public static UpdateTenantNameApiRequest updateTenantNameRequest(String name) {
        return new UpdateTenantNameApiRequest(name);
    }

    // ========================================
    // 테넌트 상태 변경 요청
    // ========================================
    public static UpdateTenantStatusApiRequest activateTenantRequest() {
        return new UpdateTenantStatusApiRequest("ACTIVE");
    }

    public static UpdateTenantStatusApiRequest deactivateTenantRequest() {
        return new UpdateTenantStatusApiRequest("INACTIVE");
    }

    public static UpdateTenantStatusApiRequest suspendTenantRequest() {
        return new UpdateTenantStatusApiRequest("SUSPENDED");
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================
    public static CreateTenantApiRequest createTenantRequestWithEmptyName() {
        return new CreateTenantApiRequest("");
    }

    public static CreateTenantApiRequest createTenantRequestWithTooShortName() {
        return new CreateTenantApiRequest("A");
    }
}
