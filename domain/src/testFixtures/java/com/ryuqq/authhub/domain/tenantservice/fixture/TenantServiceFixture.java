package com.ryuqq.authhub.domain.tenantservice.fixture;

import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenantservice.aggregate.TenantService;
import com.ryuqq.authhub.domain.tenantservice.id.TenantServiceId;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.Instant;

/**
 * TenantService 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantServiceFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_TENANT_SERVICE_ID = 1L;
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-123456789abc";
    private static final Long DEFAULT_SERVICE_ID = 1L;

    private TenantServiceFixture() {}

    /** 기본 TenantService 생성 (ID 할당됨, ACTIVE) */
    public static TenantService create() {
        return TenantService.reconstitute(
                TenantServiceId.of(DEFAULT_TENANT_SERVICE_ID),
                TenantId.of(DEFAULT_TENANT_ID),
                ServiceId.of(DEFAULT_SERVICE_ID),
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 테넌트로 TenantService 생성 */
    public static TenantService createWithTenant(String tenantId) {
        return TenantService.reconstitute(
                TenantServiceId.of(DEFAULT_TENANT_SERVICE_ID),
                TenantId.of(tenantId),
                ServiceId.of(DEFAULT_SERVICE_ID),
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 서비스로 TenantService 생성 */
    public static TenantService createWithService(Long serviceId) {
        return TenantService.reconstitute(
                TenantServiceId.of(DEFAULT_TENANT_SERVICE_ID),
                TenantId.of(DEFAULT_TENANT_ID),
                ServiceId.of(serviceId),
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 TenantService 생성 (ID 미할당) */
    public static TenantService createNew() {
        return TenantService.create(
                TenantId.of(DEFAULT_TENANT_ID), ServiceId.of(DEFAULT_SERVICE_ID), FIXED_TIME);
    }

    /** 지정된 상태로 TenantService 생성 */
    public static TenantService createWithStatus(TenantServiceStatus status) {
        return TenantService.reconstitute(
                TenantServiceId.of(DEFAULT_TENANT_SERVICE_ID),
                TenantId.of(DEFAULT_TENANT_ID),
                ServiceId.of(DEFAULT_SERVICE_ID),
                status,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 TenantService 생성 */
    public static TenantService createInactive() {
        return createWithStatus(TenantServiceStatus.INACTIVE);
    }

    /** 일시 중지된 TenantService 생성 */
    public static TenantService createSuspended() {
        return createWithStatus(TenantServiceStatus.SUSPENDED);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 TenantServiceId 반환 */
    public static TenantServiceId defaultId() {
        return TenantServiceId.of(DEFAULT_TENANT_SERVICE_ID);
    }

    /** 기본 TenantService ID Long 값 반환 */
    public static Long defaultIdValue() {
        return DEFAULT_TENANT_SERVICE_ID;
    }

    /** 기본 TenantId 반환 */
    public static TenantId defaultTenantId() {
        return TenantId.of(DEFAULT_TENANT_ID);
    }

    /** 기본 Tenant ID 문자열 반환 */
    public static String defaultTenantIdValue() {
        return DEFAULT_TENANT_ID;
    }

    /** 기본 ServiceId 반환 */
    public static ServiceId defaultServiceId() {
        return ServiceId.of(DEFAULT_SERVICE_ID);
    }

    /** 기본 Service ID Long 값 반환 */
    public static Long defaultServiceIdValue() {
        return DEFAULT_SERVICE_ID;
    }
}
