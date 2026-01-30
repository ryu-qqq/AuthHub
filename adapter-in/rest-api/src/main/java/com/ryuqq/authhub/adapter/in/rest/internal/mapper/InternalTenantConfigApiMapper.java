package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.TenantConfigApiResponse;
import com.ryuqq.authhub.application.tenant.dto.response.TenantConfigResult;
import org.springframework.stereotype.Component;

/**
 * InternalTenantConfigApiMapper - Internal Tenant Config API Mapper
 *
 * <p>Application Layer 결과를 API 응답으로 변환합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class InternalTenantConfigApiMapper {

    /**
     * Application 결과를 API 응답으로 변환
     *
     * @param result Application Layer 결과
     * @return API 응답 DTO
     */
    public TenantConfigApiResponse toApiResponse(TenantConfigResult result) {
        return new TenantConfigApiResponse(
                result.tenantId(), result.name(), result.status(), result.active());
    }
}
