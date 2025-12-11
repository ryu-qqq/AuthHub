package com.ryuqq.authhub.adapter.in.rest.role.mapper;

import com.ryuqq.authhub.adapter.in.rest.role.dto.command.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.query.SearchRolesApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RolePermissionApiResponse;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * RoleApiMapper - 역할 API DTO 변환기
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
public class RoleApiMapper {

    /**
     * CreateRoleApiRequest → CreateRoleCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateRoleCommand toCommand(CreateRoleApiRequest request) {
        UUID tenantId = request.tenantId() != null ? UUID.fromString(request.tenantId()) : null;
        boolean isSystem = request.isSystem() != null && request.isSystem();
        return new CreateRoleCommand(
                tenantId, request.name(), request.description(), request.scope(), isSystem);
    }

    /**
     * UpdateRoleApiRequest → UpdateRoleCommand 변환
     *
     * @param roleId 역할 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateRoleCommand toCommand(String roleId, UpdateRoleApiRequest request) {
        return new UpdateRoleCommand(
                UUID.fromString(roleId), request.name(), request.description());
    }

    /**
     * roleId → DeleteRoleCommand 변환
     *
     * @param roleId 역할 ID (PathVariable)
     * @return Application Command DTO
     */
    public DeleteRoleCommand toDeleteCommand(String roleId) {
        return new DeleteRoleCommand(UUID.fromString(roleId));
    }

    /**
     * roleId → GetRoleQuery 변환
     *
     * @param roleId 역할 ID (PathVariable)
     * @return Application Query DTO
     */
    public GetRoleQuery toGetQuery(String roleId) {
        return new GetRoleQuery(UUID.fromString(roleId));
    }

    /**
     * SearchRolesApiRequest → SearchRolesQuery 변환
     *
     * @param request API 요청 DTO
     * @return Application Query DTO
     */
    public SearchRolesQuery toQuery(SearchRolesApiRequest request) {
        UUID tenantId = request.tenantId() != null ? UUID.fromString(request.tenantId()) : null;
        RoleScope scope = parseScope(request.scope());
        RoleType type = parseType(request.type());
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null ? request.size() : 20;
        return new SearchRolesQuery(tenantId, request.name(), scope, type, page, size);
    }

    /**
     * RoleResponse → CreateRoleApiResponse 변환
     *
     * @param response 생성된 역할 Response
     * @return API 응답 DTO
     */
    public CreateRoleApiResponse toCreateResponse(RoleResponse response) {
        return new CreateRoleApiResponse(response.roleId().toString());
    }

    /**
     * RoleResponse → RoleApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public RoleApiResponse toApiResponse(RoleResponse response) {
        return new RoleApiResponse(
                response.roleId().toString(),
                response.tenantId() != null ? response.tenantId().toString() : null,
                response.name(),
                response.description(),
                response.scope(),
                response.type(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * RoleResponse 목록 → RoleApiResponse 목록 변환
     *
     * @param responses Application Response DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<RoleApiResponse> toApiResponseList(List<RoleResponse> responses) {
        return responses.stream().map(this::toApiResponse).toList();
    }

    /**
     * 문자열을 RoleScope로 변환
     *
     * @param scope 범위 문자열
     * @return RoleScope (유효하지 않거나 null이면 null)
     */
    private RoleScope parseScope(String scope) {
        if (scope == null || scope.isBlank()) {
            return null;
        }
        try {
            return RoleScope.valueOf(scope.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 문자열을 RoleType으로 변환
     *
     * @param type 타입 문자열
     * @return RoleType (유효하지 않거나 null이면 null)
     */
    private RoleType parseType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return RoleType.valueOf(type.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ===== RolePermission 관련 변환 메서드 =====

    /**
     * GrantRolePermissionApiRequest → GrantRolePermissionCommand 변환
     *
     * @param roleId 역할 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public GrantRolePermissionCommand toGrantPermissionCommand(
            String roleId, GrantRolePermissionApiRequest request) {
        return new GrantRolePermissionCommand(UUID.fromString(roleId), request.permissionId());
    }

    /**
     * roleId + permissionId → RevokeRolePermissionCommand 변환
     *
     * @param roleId 역할 ID (PathVariable)
     * @param permissionId 권한 ID (PathVariable)
     * @return Application Command DTO
     */
    public RevokeRolePermissionCommand toRevokePermissionCommand(
            String roleId, String permissionId) {
        return new RevokeRolePermissionCommand(
                UUID.fromString(roleId), UUID.fromString(permissionId));
    }

    /**
     * RolePermissionResponse → RolePermissionApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public RolePermissionApiResponse toRolePermissionApiResponse(RolePermissionResponse response) {
        return new RolePermissionApiResponse(
                response.roleId(), response.permissionId(), response.grantedAt());
    }

    /**
     * RolePermissionResponse 목록 → RolePermissionApiResponse 목록 변환
     *
     * @param responses Application Response DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<RolePermissionApiResponse> toRolePermissionApiResponseList(
            List<RolePermissionResponse> responses) {
        return responses.stream().map(this::toRolePermissionApiResponse).toList();
    }
}
