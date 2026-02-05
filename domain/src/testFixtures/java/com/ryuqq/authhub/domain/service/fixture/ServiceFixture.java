package com.ryuqq.authhub.domain.service.fixture;

import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import com.ryuqq.authhub.domain.service.vo.ServiceDescription;
import com.ryuqq.authhub.domain.service.vo.ServiceName;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
import java.time.Instant;

/**
 * Service 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ServiceFixture {

    private static final Instant FIXED_TIME = Instant.parse("2025-01-01T00:00:00Z");
    private static final Long DEFAULT_SERVICE_ID = 1L;
    private static final String DEFAULT_SERVICE_CODE = "SVC_STORE";

    private ServiceFixture() {}

    /** 기본 Service 생성 (ID 할당됨, ACTIVE) */
    public static Service create() {
        return Service.reconstitute(
                ServiceId.of(DEFAULT_SERVICE_ID),
                ServiceCode.of(DEFAULT_SERVICE_CODE),
                ServiceName.of("자사몰"),
                ServiceDescription.of("자사몰 서비스"),
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 서비스 코드로 Service 생성 */
    public static Service createWithCode(String code) {
        return Service.reconstitute(
                ServiceId.of(DEFAULT_SERVICE_ID),
                ServiceCode.of(code),
                ServiceName.of("Test Service"),
                ServiceDescription.of("Test Description"),
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 지정된 이름으로 Service 생성 */
    public static Service createWithName(String name) {
        return Service.reconstitute(
                ServiceId.of(DEFAULT_SERVICE_ID),
                ServiceCode.of(DEFAULT_SERVICE_CODE),
                ServiceName.of(name),
                ServiceDescription.of("Test Description"),
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 새로운 Service 생성 (ID 미할당) */
    public static Service createNew() {
        return Service.create(
                ServiceCode.of("SVC_NEW"),
                ServiceName.of("New Service"),
                ServiceDescription.of("New Service Description"),
                FIXED_TIME);
    }

    /** 지정된 상태로 Service 생성 */
    public static Service createWithStatus(ServiceStatus status) {
        return Service.reconstitute(
                ServiceId.of(DEFAULT_SERVICE_ID),
                ServiceCode.of(DEFAULT_SERVICE_CODE),
                ServiceName.of("Test Service"),
                ServiceDescription.of("Test Description"),
                status,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 비활성화된 Service 생성 */
    public static Service createInactive() {
        return createWithStatus(ServiceStatus.INACTIVE);
    }

    /** 설명 없는 Service 생성 */
    public static Service createWithoutDescription() {
        return Service.reconstitute(
                ServiceId.of(DEFAULT_SERVICE_ID),
                ServiceCode.of(DEFAULT_SERVICE_CODE),
                ServiceName.of("Test Service"),
                ServiceDescription.empty(),
                ServiceStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 테스트용 고정 시간 반환 */
    public static Instant fixedTime() {
        return FIXED_TIME;
    }

    /** 기본 ServiceId 반환 */
    public static ServiceId defaultId() {
        return ServiceId.of(DEFAULT_SERVICE_ID);
    }

    /** 기본 Service ID Long 값 반환 */
    public static Long defaultIdValue() {
        return DEFAULT_SERVICE_ID;
    }

    /** 기본 ServiceCode 반환 */
    public static ServiceCode defaultCode() {
        return ServiceCode.of(DEFAULT_SERVICE_CODE);
    }

    /** 기본 Service Code 문자열 반환 */
    public static String defaultCodeValue() {
        return DEFAULT_SERVICE_CODE;
    }
}
