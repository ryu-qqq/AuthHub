package com.ryuqq.authhub.adapter.in.rest.endpointpermission.mapper;

import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.CreateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.UpdateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.query.SearchEndpointPermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.CreateEndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.EndpointPermissionApiResponse;
import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.command.DeleteEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.command.UpdateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * EndpointPermissionApiMapper - 엔드포인트 권한 API DTO 변환기
 *
 * <p>REST API DTO와 Application DTO 간의 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지
 *   <li>단순 변환만 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class EndpointPermissionApiMapper {

    /**
     * CreateEndpointPermissionApiRequest -> CreateEndpointPermissionCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateEndpointPermissionCommand toCommand(CreateEndpointPermissionApiRequest request) {
        return new CreateEndpointPermissionCommand(
                request.serviceName(),
                request.path(),
                request.method(),
                request.description(),
                request.isPublic(),
                request.requiredPermissions(),
                request.requiredRoles());
    }

    /**
     * UpdateEndpointPermissionApiRequest -> UpdateEndpointPermissionCommand 변환
     *
     * @param endpointPermissionId 엔드포인트 권한 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateEndpointPermissionCommand toCommand(
            String endpointPermissionId, UpdateEndpointPermissionApiRequest request) {
        return new UpdateEndpointPermissionCommand(
                endpointPermissionId,
                request.description(),
                request.isPublic(),
                request.requiredPermissions(),
                request.requiredRoles());
    }

    /**
     * endpointPermissionId -> DeleteEndpointPermissionCommand 변환
     *
     * @param endpointPermissionId 엔드포인트 권한 ID (PathVariable)
     * @return Application Command DTO
     */
    public DeleteEndpointPermissionCommand toDeleteCommand(String endpointPermissionId) {
        return new DeleteEndpointPermissionCommand(endpointPermissionId);
    }

    /**
     * endpointPermissionId -> GetEndpointPermissionQuery 변환
     *
     * @param endpointPermissionId 엔드포인트 권한 ID (PathVariable)
     * @return Application Query DTO
     */
    public GetEndpointPermissionQuery toGetQuery(String endpointPermissionId) {
        return new GetEndpointPermissionQuery(endpointPermissionId);
    }

    /**
     * SearchEndpointPermissionsApiRequest -> SearchEndpointPermissionsQuery 변환
     *
     * @param request API 요청 DTO
     * @return Application Query DTO
     */
    public SearchEndpointPermissionsQuery toQuery(SearchEndpointPermissionsApiRequest request) {
        return new SearchEndpointPermissionsQuery(
                request.serviceName(),
                request.pathPattern(),
                request.method(),
                request.isPublic(),
                request.page(),
                request.size());
    }

    /**
     * EndpointPermissionResponse -> CreateEndpointPermissionApiResponse 변환
     *
     * @param response 생성된 엔드포인트 권한 Response
     * @return API 응답 DTO
     */
    public CreateEndpointPermissionApiResponse toCreateResponse(
            EndpointPermissionResponse response) {
        return new CreateEndpointPermissionApiResponse(response.id());
    }

    /**
     * EndpointPermissionResponse -> EndpointPermissionApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public EndpointPermissionApiResponse toApiResponse(EndpointPermissionResponse response) {
        return new EndpointPermissionApiResponse(
                response.id(),
                response.serviceName(),
                response.path(),
                response.method(),
                response.description(),
                response.isPublic(),
                response.requiredPermissions(),
                response.requiredRoles(),
                response.version(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * EndpointPermissionResponse 목록 -> EndpointPermissionApiResponse 목록 변환
     *
     * @param responses Application Response DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<EndpointPermissionApiResponse> toApiResponseList(
            List<EndpointPermissionResponse> responses) {
        return responses.stream().map(this::toApiResponse).toList();
    }
}
