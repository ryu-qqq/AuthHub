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

    /**
     * 기본 테넌트 생성 요청 생성
     *
     * @return "Test Tenant"로 설정된 테넌트 생성 요청
     */
    public static CreateTenantApiRequest createTenantRequest() {
        return createTenantRequest("Test Tenant");
    }

    /**
     * 커스텀 테넌트 생성 요청 생성
     *
     * @param name 테넌트명
     * @return 테넌트 생성 요청
     */
    public static CreateTenantApiRequest createTenantRequest(String name) {
        return new CreateTenantApiRequest(name);
    }

    /**
     * 유니크 이름을 가진 테넌트 생성 요청 생성
     *
     * <p>테스트 간 충돌 방지를 위해 타임스탬프 기반 유니크 이름을 사용합니다.
     *
     * @return 유니크 이름을 가진 테넌트 생성 요청
     */
    public static CreateTenantApiRequest createTenantRequestWithUniqueName() {
        return new CreateTenantApiRequest("Test Tenant " + System.currentTimeMillis());
    }

    // ========================================
    // 테넌트 이름 수정 요청
    // ========================================

    /**
     * 기본 테넌트 이름 수정 요청 생성
     *
     * @return "Updated Tenant Name"으로 설정된 수정 요청
     */
    public static UpdateTenantNameApiRequest updateTenantNameRequest() {
        return updateTenantNameRequest("Updated Tenant Name");
    }

    /**
     * 커스텀 테넌트 이름 수정 요청 생성
     *
     * @param name 새로운 테넌트명
     * @return 테넌트 이름 수정 요청
     */
    public static UpdateTenantNameApiRequest updateTenantNameRequest(String name) {
        return new UpdateTenantNameApiRequest(name);
    }

    // ========================================
    // 테넌트 상태 변경 요청
    // ========================================

    /**
     * 테넌트 활성화 요청 생성
     *
     * @return status="ACTIVE"인 상태 변경 요청
     */
    public static UpdateTenantStatusApiRequest activateTenantRequest() {
        return new UpdateTenantStatusApiRequest("ACTIVE");
    }

    /**
     * 테넌트 비활성화 요청 생성
     *
     * @return status="INACTIVE"인 상태 변경 요청
     */
    public static UpdateTenantStatusApiRequest deactivateTenantRequest() {
        return new UpdateTenantStatusApiRequest("INACTIVE");
    }

    /**
     * 테넌트 정지 요청 생성
     *
     * @return status="SUSPENDED"인 상태 변경 요청
     */
    public static UpdateTenantStatusApiRequest suspendTenantRequest() {
        return new UpdateTenantStatusApiRequest("SUSPENDED");
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================

    /**
     * 빈 이름을 가진 테넌트 생성 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @return name이 빈 문자열인 테넌트 생성 요청
     */
    public static CreateTenantApiRequest createTenantRequestWithEmptyName() {
        return new CreateTenantApiRequest("");
    }

    /**
     * 너무 짧은 이름을 가진 테넌트 생성 요청 (검증 실패용)
     *
     * <p>@Size(min=2) 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @return 1글자 이름을 가진 테넌트 생성 요청
     */
    public static CreateTenantApiRequest createTenantRequestWithTooShortName() {
        return new CreateTenantApiRequest("A");
    }
}
