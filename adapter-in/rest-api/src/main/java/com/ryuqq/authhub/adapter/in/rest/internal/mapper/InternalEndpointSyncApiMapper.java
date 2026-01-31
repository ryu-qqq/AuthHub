package com.ryuqq.authhub.adapter.in.rest.internal.mapper;

import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.EndpointSyncApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.command.EndpointSyncApiRequest.EndpointInfoApiRequest;
import com.ryuqq.authhub.adapter.in.rest.internal.dto.response.EndpointSyncResultApiResponse;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand.EndpointSyncItem;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * InternalEndpointSyncApiMapper - Internal 엔드포인트 동기화 API 매퍼
 *
 * <p>REST API 계층과 Application 계층 간의 DTO 변환을 담당합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class InternalEndpointSyncApiMapper {

    /**
     * API 요청 → Application Command 변환
     *
     * @param request API 요청 DTO
     * @return Application Command
     */
    public SyncEndpointsCommand toCommand(EndpointSyncApiRequest request) {
        List<EndpointSyncItem> items =
                request.endpoints().stream().map(this::toEndpointSyncItem).toList();
        return new SyncEndpointsCommand(request.serviceName(), items);
    }

    private EndpointSyncItem toEndpointSyncItem(EndpointInfoApiRequest apiRequest) {
        return new EndpointSyncItem(
                apiRequest.httpMethod(),
                apiRequest.pathPattern(),
                apiRequest.permissionKey(),
                apiRequest.description());
    }

    /**
     * Application Result → API 응답 변환
     *
     * @param result Application 결과 DTO
     * @return API 응답 DTO
     */
    public EndpointSyncResultApiResponse toApiResponse(SyncEndpointsResult result) {
        return new EndpointSyncResultApiResponse(
                result.serviceName(),
                result.totalEndpoints(),
                result.createdPermissions(),
                result.createdEndpoints(),
                result.skippedEndpoints());
    }
}
