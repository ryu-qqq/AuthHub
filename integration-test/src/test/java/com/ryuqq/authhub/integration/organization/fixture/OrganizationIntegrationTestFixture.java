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

    /**
     * 기본 조직 생성 요청 생성
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @return "Test Organization"으로 설정된 조직 생성 요청
     */
    public static CreateOrganizationApiRequest createOrganizationRequest(String tenantId) {
        return createOrganizationRequest(tenantId, "Test Organization");
    }

    /**
     * 커스텀 조직 생성 요청 생성
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @param name 조직명
     * @return 조직 생성 요청
     */
    public static CreateOrganizationApiRequest createOrganizationRequest(
            String tenantId, String name) {
        return new CreateOrganizationApiRequest(tenantId, name);
    }

    /**
     * 유니크 이름을 가진 조직 생성 요청 생성
     *
     * <p>테스트 간 충돌 방지를 위해 타임스탬프 기반 유니크 이름을 사용합니다.
     *
     * @param tenantId 테넌트 ID (UUID 문자열)
     * @return 유니크 이름을 가진 조직 생성 요청
     */
    public static CreateOrganizationApiRequest createOrganizationRequestWithUniqueName(
            String tenantId) {
        return new CreateOrganizationApiRequest(tenantId, "Test Org " + System.currentTimeMillis());
    }

    // ========================================
    // 조직 수정 요청
    // ========================================

    /**
     * 기본 조직 수정 요청 생성
     *
     * @return "Updated Organization Name"으로 설정된 수정 요청
     */
    public static UpdateOrganizationApiRequest updateOrganizationRequest() {
        return updateOrganizationRequest("Updated Organization Name");
    }

    /**
     * 커스텀 조직 수정 요청 생성
     *
     * @param name 새로운 조직명
     * @return 조직 수정 요청
     */
    public static UpdateOrganizationApiRequest updateOrganizationRequest(String name) {
        return new UpdateOrganizationApiRequest(name);
    }

    // ========================================
    // 조직 상태 변경 요청
    // ========================================

    /**
     * 조직 활성화 요청 생성
     *
     * @return status="ACTIVE"인 상태 변경 요청
     */
    public static UpdateOrganizationStatusApiRequest activateOrganizationRequest() {
        return new UpdateOrganizationStatusApiRequest("ACTIVE");
    }

    /**
     * 조직 비활성화 요청 생성
     *
     * @return status="INACTIVE"인 상태 변경 요청
     */
    public static UpdateOrganizationStatusApiRequest deactivateOrganizationRequest() {
        return new UpdateOrganizationStatusApiRequest("INACTIVE");
    }

    // ========================================
    // 검증 실패용 Fixture
    // ========================================

    /**
     * 빈 테넌트 ID를 가진 조직 생성 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @return tenantId가 빈 문자열인 조직 생성 요청
     */
    public static CreateOrganizationApiRequest createOrganizationRequestWithEmptyTenantId() {
        return new CreateOrganizationApiRequest("", "Test Organization");
    }

    /**
     * 빈 이름을 가진 조직 생성 요청 (검증 실패용)
     *
     * <p>@NotBlank 검증 실패를 테스트하기 위한 Fixture입니다.
     *
     * @param tenantId 테넌트 ID
     * @return name이 빈 문자열인 조직 생성 요청
     */
    public static CreateOrganizationApiRequest createOrganizationRequestWithEmptyName(
            String tenantId) {
        return new CreateOrganizationApiRequest(tenantId, "");
    }
}
