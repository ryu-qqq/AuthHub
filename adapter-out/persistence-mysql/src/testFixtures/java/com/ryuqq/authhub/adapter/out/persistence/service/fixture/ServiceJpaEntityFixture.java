package com.ryuqq.authhub.adapter.out.persistence.service.fixture;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.time.Instant;

/**
 * ServiceJpaEntity 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ServiceJpaEntityFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_SERVICE_ID = 1L;
    private static final String DEFAULT_SERVICE_CODE = "SVC_STORE";

    private ServiceJpaEntityFixture() {}

    /** 기본 ServiceJpaEntity 생성 (ID 없음, 신규 저장용) */
    public static ServiceJpaEntity create() {
        return ServiceJpaEntity.of(
                null,
                DEFAULT_SERVICE_CODE,
                "자사몰",
                "자사몰 서비스",
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** ID가 할당된 ServiceJpaEntity 생성 (조회된 엔티티 테스트용) */
    public static ServiceJpaEntity createWithId(Long serviceId) {
        return ServiceJpaEntity.of(
                serviceId,
                DEFAULT_SERVICE_CODE,
                "자사몰",
                "자사몰 서비스",
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** ID 없이 생성 (신규 저장용) */
    public static ServiceJpaEntity createNew() {
        return ServiceJpaEntity.of(
                null,
                "SVC_NEW",
                "New Service",
                "New Service Description",
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 서비스 코드로 ServiceJpaEntity 생성 */
    public static ServiceJpaEntity createWithCode(String serviceCode) {
        return ServiceJpaEntity.of(
                null,
                serviceCode,
                "Test Service",
                "Test Description",
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 이름으로 ServiceJpaEntity 생성 */
    public static ServiceJpaEntity createWithName(String name) {
        return ServiceJpaEntity.of(
                null,
                "SVC_TEST",
                name,
                "Test Description",
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 코드와 이름으로 ServiceJpaEntity 생성 */
    public static ServiceJpaEntity createWithCodeAndName(String serviceCode, String name) {
        return ServiceJpaEntity.of(
                null,
                serviceCode,
                name,
                "Test Description",
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 ServiceJpaEntity 생성 (Mapper 테스트용, ID 포함) */
    public static ServiceJpaEntity createInactive() {
        return ServiceJpaEntity.of(
                DEFAULT_SERVICE_ID,
                "SVC_INACTIVE",
                "Inactive Service",
                "Inactive Description",
                ServiceStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 ServiceJpaEntity 생성 (신규 저장용, ID 없음) */
    public static ServiceJpaEntity createNewInactive() {
        return ServiceJpaEntity.of(
                null,
                "SVC_INACTIVE",
                "Inactive Service",
                "Inactive Description",
                ServiceStatus.INACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 설명 없는 ServiceJpaEntity 생성 (Mapper 테스트용, ID 포함) */
    public static ServiceJpaEntity createWithoutDescription() {
        return ServiceJpaEntity.of(
                DEFAULT_SERVICE_ID,
                "SVC_NO_DESC",
                "Service Without Description",
                null,
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 Service ID 반환 */
    public static Long defaultServiceId() {
        return DEFAULT_SERVICE_ID;
    }

    /** 기본 Service Code 반환 */
    public static String defaultServiceCode() {
        return DEFAULT_SERVICE_CODE;
    }
}
