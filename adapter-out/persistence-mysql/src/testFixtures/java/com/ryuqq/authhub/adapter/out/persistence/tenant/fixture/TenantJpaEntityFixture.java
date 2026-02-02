package com.ryuqq.authhub.adapter.out.persistence.tenant.fixture;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TenantJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-123456789abc";
    private static final String DEFAULT_NAME = "Test Tenant";
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private TenantJpaEntityFixture() {}

    /** 고유한 Tenant ID 생성 */
    private static String generateUniqueTenantId() {
        return UUID.randomUUID().toString();
    }

    /** 기본 TenantJpaEntity 생성 */
    public static TenantJpaEntity create() {
        return TenantJpaEntity.of(
                DEFAULT_TENANT_ID, DEFAULT_NAME, TenantStatus.ACTIVE, FIXED_TIME, FIXED_TIME, null);
    }

    /** 지정된 이름으로 TenantJpaEntity 생성 (고유 ID 사용) */
    public static TenantJpaEntity createWithName(String name) {
        return TenantJpaEntity.of(
                generateUniqueTenantId(), name, TenantStatus.ACTIVE, FIXED_TIME, FIXED_TIME, null);
    }

    /** 삭제된 TenantJpaEntity 생성 (고유 ID 사용) */
    public static TenantJpaEntity createDeleted() {
        return TenantJpaEntity.of(
                generateUniqueTenantId(),
                DEFAULT_NAME,
                TenantStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 TenantJpaEntity 생성 (고유 ID 사용) */
    public static TenantJpaEntity createInactive() {
        return TenantJpaEntity.of(
                generateUniqueTenantId(),
                DEFAULT_NAME,
                TenantStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 Tenant ID 반환 */
    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }
}
