package com.ryuqq.authhub.adapter.in.rest.tenantservice.fixture;

import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.SubscribeTenantServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.request.UpdateTenantServiceStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.response.TenantServiceApiResponse;
import com.ryuqq.authhub.adapter.in.rest.tenantservice.dto.response.TenantServiceIdApiResponse;
import java.time.Instant;

/**
 * TenantService API 테스트 픽스처
 *
 * <p>TenantService 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantServiceApiFixture {

    private static final Long DEFAULT_TENANT_SERVICE_ID = 1L;
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-123456789abc";
    private static final Long DEFAULT_SERVICE_ID = 1L;
    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final String FIXED_TIME = "2025-01-01T00:00:00Z";

    private TenantServiceApiFixture() {}

    // ========== SubscribeTenantServiceApiRequest ==========

    /** 기본 구독 요청 */
    public static SubscribeTenantServiceApiRequest subscribeRequest() {
        return new SubscribeTenantServiceApiRequest(DEFAULT_TENANT_ID, DEFAULT_SERVICE_ID);
    }

    /** 커스텀 테넌트 ID로 구독 요청 */
    public static SubscribeTenantServiceApiRequest subscribeRequest(String tenantId) {
        return new SubscribeTenantServiceApiRequest(tenantId, DEFAULT_SERVICE_ID);
    }

    /** 커스텀 테넌트 ID + 서비스 ID로 구독 요청 */
    public static SubscribeTenantServiceApiRequest subscribeRequest(
            String tenantId, Long serviceId) {
        return new SubscribeTenantServiceApiRequest(tenantId, serviceId);
    }

    // ========== UpdateTenantServiceStatusApiRequest ==========

    /** 기본 상태 변경 요청 (INACTIVE) */
    public static UpdateTenantServiceStatusApiRequest updateStatusRequest() {
        return new UpdateTenantServiceStatusApiRequest("INACTIVE");
    }

    /** 커스텀 상태로 변경 요청 */
    public static UpdateTenantServiceStatusApiRequest updateStatusRequest(String status) {
        return new UpdateTenantServiceStatusApiRequest(status);
    }

    // ========== TenantServiceIdApiResponse ==========

    /** 기본 ID 응답 */
    public static TenantServiceIdApiResponse tenantServiceIdResponse() {
        return TenantServiceIdApiResponse.of(DEFAULT_TENANT_SERVICE_ID);
    }

    /** 커스텀 ID 응답 */
    public static TenantServiceIdApiResponse tenantServiceIdResponse(Long tenantServiceId) {
        return TenantServiceIdApiResponse.of(tenantServiceId);
    }

    // ========== TenantServiceApiResponse ==========

    /** 기본 응답 */
    public static TenantServiceApiResponse tenantServiceResponse() {
        return new TenantServiceApiResponse(
                DEFAULT_TENANT_SERVICE_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                DEFAULT_STATUS,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 응답 */
    public static TenantServiceApiResponse tenantServiceResponse(
            Long tenantServiceId, String tenantId, Long serviceId, String status) {
        return new TenantServiceApiResponse(
                tenantServiceId, tenantId, serviceId, status, FIXED_TIME, FIXED_TIME, FIXED_TIME);
    }

    // ========== Default Values ==========

    public static Long defaultTenantServiceId() {
        return DEFAULT_TENANT_SERVICE_ID;
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static Long defaultServiceId() {
        return DEFAULT_SERVICE_ID;
    }

    public static String defaultStatus() {
        return DEFAULT_STATUS;
    }

    public static Instant fixedTime() {
        return Instant.parse(FIXED_TIME);
    }
}
