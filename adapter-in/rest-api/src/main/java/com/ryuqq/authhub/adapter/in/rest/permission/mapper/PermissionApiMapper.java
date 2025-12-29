package com.ryuqq.authhub.adapter.in.rest.permission.mapper;

import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.CreatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.command.UpdatePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.query.SearchPermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.CreatePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.PermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.permission.dto.response.UserPermissionsApiResponse;
import com.ryuqq.authhub.application.permission.dto.command.CreatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.DeletePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.command.UpdatePermissionCommand;
import com.ryuqq.authhub.application.permission.dto.query.GetPermissionQuery;
import com.ryuqq.authhub.application.permission.dto.query.SearchPermissionsQuery;
import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.application.role.dto.response.UserRolesResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * PermissionApiMapper - 권한 API DTO 변환기
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
public class PermissionApiMapper {

    /**
     * CreatePermissionApiRequest → CreatePermissionCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreatePermissionCommand toCommand(CreatePermissionApiRequest request) {
        boolean isSystem = request.isSystem() != null && request.isSystem();
        return new CreatePermissionCommand(
                request.resource(), request.action(), request.description(), isSystem);
    }

    /**
     * UpdatePermissionApiRequest → UpdatePermissionCommand 변환
     *
     * @param permissionId 권한 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdatePermissionCommand toCommand(
            String permissionId, UpdatePermissionApiRequest request) {
        return new UpdatePermissionCommand(UUID.fromString(permissionId), request.description());
    }

    /**
     * permissionId → DeletePermissionCommand 변환
     *
     * @param permissionId 권한 ID (PathVariable)
     * @return Application Command DTO
     */
    public DeletePermissionCommand toDeleteCommand(String permissionId) {
        return new DeletePermissionCommand(UUID.fromString(permissionId));
    }

    /**
     * permissionId → GetPermissionQuery 변환
     *
     * @param permissionId 권한 ID (PathVariable)
     * @return Application Query DTO
     */
    public GetPermissionQuery toGetQuery(String permissionId) {
        return new GetPermissionQuery(UUID.fromString(permissionId));
    }

    /**
     * SearchPermissionsApiRequest → SearchPermissionsQuery 변환
     *
     * @param request API 요청 DTO
     * @return Application Query DTO
     */
    public SearchPermissionsQuery toQuery(SearchPermissionsApiRequest request) {
        return SearchPermissionsQuery.of(
                request.resource(),
                request.action(),
                request.types(),
                request.createdFrom(),
                request.createdTo(),
                request.pageOrDefault(),
                request.sizeOrDefault());
    }

    /**
     * PermissionResponse → CreatePermissionApiResponse 변환
     *
     * @param response 생성된 권한 Response
     * @return API 응답 DTO
     */
    public CreatePermissionApiResponse toCreateResponse(PermissionResponse response) {
        return new CreatePermissionApiResponse(response.permissionId().toString());
    }

    /**
     * PermissionResponse → PermissionApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public PermissionApiResponse toApiResponse(PermissionResponse response) {
        return new PermissionApiResponse(
                response.permissionId().toString(),
                response.key(),
                response.resource(),
                response.action(),
                response.description(),
                response.type(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * PermissionResponse 목록 → PermissionApiResponse 목록 변환
     *
     * @param responses Application Response DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<PermissionApiResponse> toApiResponseList(List<PermissionResponse> responses) {
        return responses.stream().map(this::toApiResponse).toList();
    }

    /**
     * UserRolesResponse → UserPermissionsApiResponse 변환
     *
     * <p>Gateway API에서 사용자 권한 조회 시 사용됩니다.
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public UserPermissionsApiResponse toUserPermissionsApiResponse(UserRolesResponse response) {
        return new UserPermissionsApiResponse(
                response.userId().toString(), response.roles(), response.permissions());
    }
}
