package com.ryuqq.authhub.adapter.in.rest.service.fixture;

import com.ryuqq.authhub.adapter.in.rest.service.dto.request.CreateServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.request.UpdateServiceApiRequest;
import com.ryuqq.authhub.adapter.in.rest.service.dto.response.ServiceApiResponse;
import com.ryuqq.authhub.adapter.in.rest.service.dto.response.ServiceIdApiResponse;
import java.time.Instant;

/**
 * Service API 테스트 픽스처
 *
 * <p>Service 관련 API 테스트에 사용되는 DTO 픽스처를 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ServiceApiFixture {

    private static final Long DEFAULT_SERVICE_ID = 1L;
    private static final String DEFAULT_SERVICE_CODE = "SVC_STORE";
    private static final String DEFAULT_NAME = "자사몰";
    private static final String DEFAULT_DESCRIPTION = "자사몰 서비스";
    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final String FIXED_TIME = "2025-01-01T00:00:00Z";

    private ServiceApiFixture() {}

    // ========== CreateServiceApiRequest ==========

    /** 기본 생성 요청 */
    public static CreateServiceApiRequest createServiceRequest() {
        return new CreateServiceApiRequest(DEFAULT_SERVICE_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    /** 커스텀 서비스 코드와 이름으로 생성 요청 */
    public static CreateServiceApiRequest createServiceRequest(String serviceCode, String name) {
        return new CreateServiceApiRequest(serviceCode, name, DEFAULT_DESCRIPTION);
    }

    /** 커스텀 서비스 코드, 이름, 설명으로 생성 요청 */
    public static CreateServiceApiRequest createServiceRequest(
            String serviceCode, String name, String description) {
        return new CreateServiceApiRequest(serviceCode, name, description);
    }

    // ========== UpdateServiceApiRequest ==========

    /** 기본 수정 요청 */
    public static UpdateServiceApiRequest updateServiceRequest() {
        return new UpdateServiceApiRequest(DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_STATUS);
    }

    /** 커스텀 이름, 설명, 상태로 수정 요청 */
    public static UpdateServiceApiRequest updateServiceRequest(
            String name, String description, String status) {
        return new UpdateServiceApiRequest(name, description, status);
    }

    // ========== ServiceIdApiResponse ==========

    /** 기본 ID 응답 */
    public static ServiceIdApiResponse serviceIdResponse() {
        return ServiceIdApiResponse.of(DEFAULT_SERVICE_ID);
    }

    /** 커스텀 ID 응답 */
    public static ServiceIdApiResponse serviceIdResponse(Long serviceId) {
        return ServiceIdApiResponse.of(serviceId);
    }

    // ========== ServiceApiResponse ==========

    /** 기본 응답 */
    public static ServiceApiResponse serviceResponse() {
        return new ServiceApiResponse(
                DEFAULT_SERVICE_ID,
                DEFAULT_SERVICE_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_STATUS,
                FIXED_TIME,
                FIXED_TIME);
    }

    /** 커스텀 응답 */
    public static ServiceApiResponse serviceResponse(
            Long serviceId, String serviceCode, String name) {
        return new ServiceApiResponse(
                serviceId,
                serviceCode,
                name,
                DEFAULT_DESCRIPTION,
                DEFAULT_STATUS,
                FIXED_TIME,
                FIXED_TIME);
    }

    // ========== Default Values ==========

    public static Long defaultServiceId() {
        return DEFAULT_SERVICE_ID;
    }

    public static String defaultServiceCode() {
        return DEFAULT_SERVICE_CODE;
    }

    public static String defaultName() {
        return DEFAULT_NAME;
    }

    public static String defaultDescription() {
        return DEFAULT_DESCRIPTION;
    }

    public static String defaultStatus() {
        return DEFAULT_STATUS;
    }

    public static Instant fixedTime() {
        return Instant.parse(FIXED_TIME);
    }
}
