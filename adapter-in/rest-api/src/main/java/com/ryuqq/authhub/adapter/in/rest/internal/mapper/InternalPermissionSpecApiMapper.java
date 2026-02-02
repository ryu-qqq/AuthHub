package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.EndpointPermissionSpecApiResponse;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.EndpointPermissionSpecListApiResponse;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * InternalPermissionSpecApiMapper - Internal Permission Spec API Mapper
 *
 * <p>Application Layer 결과를 API 응답으로 변환합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class InternalPermissionSpecApiMapper {

    /**
     * Application 결과를 API 응답으로 변환
     *
     * @param result Application Layer 결과
     * @return API 응답 DTO
     */
    public EndpointPermissionSpecListApiResponse toApiResponse(
            EndpointPermissionSpecListResult result) {
        List<EndpointPermissionSpecApiResponse> endpoints =
                result.endpoints().stream().map(this::toApiResponse).toList();

        return new EndpointPermissionSpecListApiResponse(
                result.version(), result.updatedAt(), endpoints);
    }

    /**
     * 개별 스펙 결과를 API 응답으로 변환
     *
     * @param result 스펙 결과
     * @return API 응답 DTO
     */
    private EndpointPermissionSpecApiResponse toApiResponse(EndpointPermissionSpecResult result) {
        return new EndpointPermissionSpecApiResponse(
                result.serviceName(),
                result.pathPattern(),
                result.httpMethod(),
                result.requiredPermissions(),
                result.requiredRoles(),
                result.isPublic(),
                result.description());
    }
}
