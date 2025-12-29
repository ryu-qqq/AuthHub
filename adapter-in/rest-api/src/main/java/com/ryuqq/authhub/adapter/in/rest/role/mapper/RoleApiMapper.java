package com.ryuqq.authhub.adapter.in.rest.role.mapper;

import com.ryuqq.authhub.adapter.in.rest.role.dto.command.CreateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.GrantRolePermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.command.UpdateRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.query.SearchRolesAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.query.SearchRolesApiRequest;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.CreateRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RolePermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RolePermissionSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.role.dto.response.RoleUserApiResponse;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.role.dto.command.CreateRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.DeleteRoleCommand;
import com.ryuqq.authhub.application.role.dto.command.GrantRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.RevokeRolePermissionCommand;
import com.ryuqq.authhub.application.role.dto.command.UpdateRoleCommand;
import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.query.SearchRoleUsersQuery;
import com.ryuqq.authhub.application.role.dto.query.SearchRolesQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;
import com.ryuqq.authhub.application.role.dto.response.RolePermissionResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleSummaryResponse;
import com.ryuqq.authhub.application.role.dto.response.RoleUserResponse;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import java.util.List;
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
@SuppressWarnings("PMD.ExcessiveImports")
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
        return SearchRolesQuery.of(
                tenantId,
                request.name(),
                request.scopes(),
                request.types(),
                request.createdFrom(),
                request.createdTo(),
                request.pageOrDefault(),
                request.sizeOrDefault());
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

    // ===== RoleUser 관련 변환 메서드 =====

    /**
     * roleId + 페이징 정보 → SearchRoleUsersQuery 변환
     *
     * @param roleId 역할 ID (PathVariable)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return Application Query DTO
     */
    public SearchRoleUsersQuery toRoleUsersQuery(String roleId, Integer page, Integer size) {
        RoleId id = RoleId.of(UUID.fromString(roleId));
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        return SearchRoleUsersQuery.of(id, pageNum, pageSize);
    }

    /**
     * RoleUserResponse → RoleUserApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public RoleUserApiResponse toRoleUserApiResponse(RoleUserResponse response) {
        return RoleUserApiResponse.of(
                response.userId(),
                response.email(),
                response.tenantId(),
                response.organizationId(),
                response.assignedAt());
    }

    /**
     * PageResponse<RoleUserResponse> → PageResponse<RoleUserApiResponse> 변환
     *
     * @param pageResponse Application Page Response
     * @return API Page Response
     */
    public PageResponse<RoleUserApiResponse> toRoleUserApiPageResponse(
            PageResponse<RoleUserResponse> pageResponse) {
        List<RoleUserApiResponse> content =
                pageResponse.content().stream().map(this::toRoleUserApiResponse).toList();
        return PageResponse.of(
                content,
                pageResponse.page(),
                pageResponse.size(),
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                pageResponse.first(),
                pageResponse.last());
    }

    // ===== Admin 전용 변환 메서드 =====

    /**
     * SearchRolesAdminApiRequest → SearchRolesQuery 변환 (Admin용)
     *
     * @param request Admin용 API 요청 DTO
     * @return Application Query DTO (확장 필터 포함)
     */
    public SearchRolesQuery toAdminQuery(SearchRolesAdminApiRequest request) {
        UUID tenantId = request.tenantId() != null ? UUID.fromString(request.tenantId()) : null;
        return SearchRolesQuery.ofAdmin(
                tenantId,
                request.name(),
                request.searchType(),
                request.scopes(),
                request.types(),
                request.createdFrom(),
                request.createdTo(),
                request.sortBy(),
                request.sortDirection(),
                request.pageOrDefault(),
                request.sizeOrDefault());
    }

    /**
     * RoleSummaryResponse → RoleSummaryApiResponse 변환 (Admin 목록용)
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public RoleSummaryApiResponse toSummaryApiResponse(RoleSummaryResponse response) {
        return new RoleSummaryApiResponse(
                response.roleId().toString(),
                response.tenantId() != null ? response.tenantId().toString() : null,
                response.tenantName(),
                response.name(),
                response.description(),
                response.scope(),
                response.type(),
                response.permissionCount(),
                response.userCount(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * RoleDetailResponse → RoleDetailApiResponse 변환 (Admin 상세용)
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public RoleDetailApiResponse toDetailApiResponse(RoleDetailResponse response) {
        List<RolePermissionSummaryApiResponse> permissions =
                response.permissions().stream().map(this::toPermissionSummaryApiResponse).toList();

        return new RoleDetailApiResponse(
                response.roleId().toString(),
                response.tenantId() != null ? response.tenantId().toString() : null,
                response.tenantName(),
                response.name(),
                response.description(),
                response.scope(),
                response.type(),
                permissions,
                response.userCount(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * RoleDetailResponse.RolePermissionSummary → RolePermissionSummaryApiResponse 변환
     *
     * @param permissionSummary Application 권한 요약 DTO
     * @return API 권한 요약 DTO
     */
    private RolePermissionSummaryApiResponse toPermissionSummaryApiResponse(
            RoleDetailResponse.RolePermissionSummary permissionSummary) {
        return new RolePermissionSummaryApiResponse(
                permissionSummary.permissionId().toString(),
                permissionSummary.name(),
                permissionSummary.description(),
                permissionSummary.resource(),
                permissionSummary.action());
    }
}
