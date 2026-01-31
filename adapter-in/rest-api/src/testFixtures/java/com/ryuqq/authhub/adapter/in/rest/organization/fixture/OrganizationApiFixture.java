package com.ryuqq.authhub.adapter.in.rest.organization.fixture;

import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationIdApiResponse;
import java.time.Instant;

/**
 * Organization API 테스트 픽스처
 *
 * <p>Organization 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationApiFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_ORGANIZATION_ID = "01941234-5678-7000-8000-organization1";
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-tenant000001";
    private static final String DEFAULT_NAME = "개발팀";

    private OrganizationApiFixture() {}

    // ========== CreateOrganizationApiRequest ==========

    /** 기본 조직 생성 요청 */
    public static CreateOrganizationApiRequest createOrganizationRequest() {
        return new CreateOrganizationApiRequest(DEFAULT_TENANT_ID, DEFAULT_NAME);
    }

    /** 커스텀 이름으로 조직 생성 요청 */
    public static CreateOrganizationApiRequest createOrganizationRequestWithName(String name) {
        return new CreateOrganizationApiRequest(DEFAULT_TENANT_ID, name);
    }

    /** 커스텀 테넌트로 조직 생성 요청 */
    public static CreateOrganizationApiRequest createOrganizationRequestWithTenant(
            String tenantId) {
        return new CreateOrganizationApiRequest(tenantId, DEFAULT_NAME);
    }

    // ========== UpdateOrganizationNameApiRequest ==========

    /** 기본 조직 이름 수정 요청 */
    public static UpdateOrganizationNameApiRequest updateOrganizationNameRequest() {
        return new UpdateOrganizationNameApiRequest("새로운 조직명");
    }

    /** 커스텀 이름으로 수정 요청 */
    public static UpdateOrganizationNameApiRequest updateOrganizationNameRequest(String name) {
        return new UpdateOrganizationNameApiRequest(name);
    }

    // ========== UpdateOrganizationStatusApiRequest ==========

    /** 기본 조직 상태 변경 요청 (INACTIVE로 변경) */
    public static UpdateOrganizationStatusApiRequest updateOrganizationStatusRequest() {
        return new UpdateOrganizationStatusApiRequest("INACTIVE");
    }

    /** 커스텀 상태로 변경 요청 */
    public static UpdateOrganizationStatusApiRequest updateOrganizationStatusRequest(
            String status) {
        return new UpdateOrganizationStatusApiRequest(status);
    }

    // ========== OrganizationIdApiResponse ==========

    /** 기본 조직 ID 응답 */
    public static OrganizationIdApiResponse organizationIdResponse() {
        return OrganizationIdApiResponse.of(DEFAULT_ORGANIZATION_ID);
    }

    /** 커스텀 ID로 응답 */
    public static OrganizationIdApiResponse organizationIdResponse(String organizationId) {
        return OrganizationIdApiResponse.of(organizationId);
    }

    // ========== OrganizationApiResponse ==========

    /** 기본 조직 조회 응답 */
    public static OrganizationApiResponse organizationResponse() {
        return new OrganizationApiResponse(
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_NAME,
                "ACTIVE",
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 조직 조회 응답 */
    public static OrganizationApiResponse organizationResponse(
            String organizationId, String name, String status) {
        return new OrganizationApiResponse(
                organizationId, DEFAULT_TENANT_ID, name, status, FIXED_TIME, FIXED_TIME);
    }

    /** 비활성 조직 응답 */
    public static OrganizationApiResponse inactiveOrganizationResponse() {
        return new OrganizationApiResponse(
                DEFAULT_ORGANIZATION_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_NAME,
                "INACTIVE",
                FIXED_TIME,
                FIXED_TIME);
    }

    // ========== Default Values ==========

    public static String defaultOrganizationId() {
        return DEFAULT_ORGANIZATION_ID;
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static String defaultName() {
        return DEFAULT_NAME;
    }

    public static Instant fixedTime() {
        return FIXED_TIME;
    }
}
