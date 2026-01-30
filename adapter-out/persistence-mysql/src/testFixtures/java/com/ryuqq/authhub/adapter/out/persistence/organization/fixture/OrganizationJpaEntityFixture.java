package com.ryuqq.authhub.adapter.out.persistence.organization.fixture;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import java.time.Instant;

/**
 * OrganizationJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final String DEFAULT_ORG_ID = "01941234-5678-7000-8000-123456789def";
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-123456789abc";
    private static final String DEFAULT_NAME = "Test Organization";

    private OrganizationJpaEntityFixture() {}

    /** 기본 OrganizationJpaEntity 생성 */
    public static OrganizationJpaEntity create() {
        return OrganizationJpaEntity.of(
                DEFAULT_ORG_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_NAME,
                OrganizationStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 테넌트로 OrganizationJpaEntity 생성 */
    public static OrganizationJpaEntity createWithTenant(String tenantId) {
        return OrganizationJpaEntity.of(
                DEFAULT_ORG_ID,
                tenantId,
                DEFAULT_NAME,
                OrganizationStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 이름으로 OrganizationJpaEntity 생성 */
    public static OrganizationJpaEntity createWithName(String name) {
        return OrganizationJpaEntity.of(
                DEFAULT_ORG_ID,
                DEFAULT_TENANT_ID,
                name,
                OrganizationStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 지정된 테넌트와 이름으로 OrganizationJpaEntity 생성 */
    public static OrganizationJpaEntity createWithTenantAndName(String tenantId, String name) {
        return OrganizationJpaEntity.of(
                java.util.UUID.randomUUID().toString(),
                tenantId,
                name,
                OrganizationStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 삭제된 OrganizationJpaEntity 생성 */
    public static OrganizationJpaEntity createDeleted() {
        return OrganizationJpaEntity.of(
                DEFAULT_ORG_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_NAME,
                OrganizationStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 OrganizationJpaEntity 생성 */
    public static OrganizationJpaEntity createInactive() {
        return OrganizationJpaEntity.of(
                DEFAULT_ORG_ID,
                DEFAULT_TENANT_ID,
                DEFAULT_NAME,
                OrganizationStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME,
                null);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 Organization ID 반환 */
    public static String defaultOrganizationId() {
        return DEFAULT_ORG_ID;
    }
}
