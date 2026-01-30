package com.ryuqq.authhub.adapter.in.rest.tenant.fixture;

import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.CreateTenantApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.UpdateTenantNameApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.request.UpdateTenantStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenant.dto.response.TenantIdApiResponse;
import java.time.Instant;
import java.util.UUID;

/**
 * Tenant API 테스트 픽스처
 *
 * <p>Tenant 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantApiFixture {

    private static final UUID DEFAULT_TENANT_ID =
            UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final String DEFAULT_TENANT_NAME = "테스트테넌트";
    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final String FIXED_TIME = "2025-01-01T00:00:00Z";

    private TenantApiFixture() {}

    // ========== CreateTenantApiRequest ==========

    /** 기본 생성 요청 */
    public static CreateTenantApiRequest createTenantRequest() {
        return new CreateTenantApiRequest(DEFAULT_TENANT_NAME);
    }

    /** 커스텀 이름으로 생성 요청 */
    public static CreateTenantApiRequest createTenantRequest(String name) {
        return new CreateTenantApiRequest(name);
    }

    // ========== UpdateTenantNameApiRequest ==========

    /** 기본 이름 수정 요청 */
    public static UpdateTenantNameApiRequest updateTenantNameRequest() {
        return new UpdateTenantNameApiRequest("수정된테넌트");
    }

    /** 커스텀 이름 수정 요청 */
    public static UpdateTenantNameApiRequest updateTenantNameRequest(String name) {
        return new UpdateTenantNameApiRequest(name);
    }

    // ========== UpdateTenantStatusApiRequest ==========

    /** 기본 상태 수정 요청 */
    public static UpdateTenantStatusApiRequest updateTenantStatusRequest() {
        return new UpdateTenantStatusApiRequest("INACTIVE");
    }

    /** 커스텀 상태 수정 요청 */
    public static UpdateTenantStatusApiRequest updateTenantStatusRequest(String status) {
        return new UpdateTenantStatusApiRequest(status);
    }

    // ========== TenantIdApiResponse ==========

    /** 기본 ID 응답 */
    public static TenantIdApiResponse tenantIdResponse() {
        return TenantIdApiResponse.of(DEFAULT_TENANT_ID.toString());
    }

    /** 커스텀 ID 응답 */
    public static TenantIdApiResponse tenantIdResponse(String tenantId) {
        return TenantIdApiResponse.of(tenantId);
    }

    // ========== TenantApiResponse ==========

    /** 기본 응답 */
    public static TenantApiResponse tenantResponse() {
        return new TenantApiResponse(
                DEFAULT_TENANT_ID.toString(),
                DEFAULT_TENANT_NAME,
                DEFAULT_STATUS,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 응답 */
    public static TenantApiResponse tenantResponse(String name, String status) {
        return new TenantApiResponse(
                DEFAULT_TENANT_ID.toString(), name, status, FIXED_TIME, FIXED_TIME);
    }

    // ========== Default Values ==========

    public static UUID defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static String defaultTenantIdString() {
        return DEFAULT_TENANT_ID.toString();
    }

    public static String defaultTenantName() {
        return DEFAULT_TENANT_NAME;
    }

    public static String defaultStatus() {
        return DEFAULT_STATUS;
    }

    public static Instant fixedTime() {
        return Instant.parse(FIXED_TIME);
    }
}
