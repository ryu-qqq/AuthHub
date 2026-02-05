package com.ryuqq.authhub.adapter.out.persistence.tenantservice.fixture;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.Instant;

/**
 * TenantServiceJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantServiceJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_TENANT_SERVICE_ID = 1L;
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-123456789abc";
    private static final Long DEFAULT_SERVICE_ID = 1L;

    private TenantServiceJpaEntityFixture() {}

    /** 기본 TenantServiceJpaEntity 생성 (ID 없음, 신규 저장용) */
    public static TenantServiceJpaEntity create() {
        return TenantServiceJpaEntity.of(
                null,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** ID가 할당된 TenantServiceJpaEntity 생성 (조회된 엔티티 테스트용) */
    public static TenantServiceJpaEntity createWithId(Long tenantServiceId) {
        return TenantServiceJpaEntity.of(
                tenantServiceId,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** ID 없이 생성 (신규 저장용) */
    public static TenantServiceJpaEntity createNew() {
        return TenantServiceJpaEntity.of(
                null,
                "01941234-5678-7000-8000-000000000001",
                2L,
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 테넌트로 TenantServiceJpaEntity 생성 */
    public static TenantServiceJpaEntity createWithTenant(String tenantId) {
        return TenantServiceJpaEntity.of(
                null,
                tenantId,
                DEFAULT_SERVICE_ID,
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 서비스로 TenantServiceJpaEntity 생성 */
    public static TenantServiceJpaEntity createWithService(Long serviceId) {
        return TenantServiceJpaEntity.of(
                null,
                DEFAULT_TENANT_ID,
                serviceId,
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 테넌트와 서비스로 TenantServiceJpaEntity 생성 */
    public static TenantServiceJpaEntity createWithTenantAndService(
            String tenantId, Long serviceId) {
        return TenantServiceJpaEntity.of(
                null,
                tenantId,
                serviceId,
                TenantServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 TenantServiceJpaEntity 생성 (Mapper 테스트용, ID 포함) */
    public static TenantServiceJpaEntity createInactive() {
        return TenantServiceJpaEntity.of(
                DEFAULT_TENANT_SERVICE_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                TenantServiceStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 TenantServiceJpaEntity 생성 (신규 저장용, ID 없음) */
    public static TenantServiceJpaEntity createNewInactive() {
        return TenantServiceJpaEntity.of(
                null,
                "01941234-5678-7000-8000-000000000002",
                3L,
                TenantServiceStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 일시 중지된 TenantServiceJpaEntity 생성 (Mapper 테스트용, ID 포함) */
    public static TenantServiceJpaEntity createSuspended() {
        return TenantServiceJpaEntity.of(
                DEFAULT_TENANT_SERVICE_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_SERVICE_ID,
                TenantServiceStatus.SUSPENDED,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 일시 중지된 TenantServiceJpaEntity 생성 (신규 저장용, ID 없음) */
    public static TenantServiceJpaEntity createNewSuspended() {
        return TenantServiceJpaEntity.of(
                null,
                "01941234-5678-7000-8000-000000000003",
                4L,
                TenantServiceStatus.SUSPENDED,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 TenantService ID 반환 */
    public static Long defaultTenantServiceId() {
        return DEFAULT_TENANT_SERVICE_ID;
    }

    /** 기본 Tenant ID 반환 */
    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    /** 기본 Service ID 반환 */
    public static Long defaultServiceId() {
        return DEFAULT_SERVICE_ID;
    }
}
