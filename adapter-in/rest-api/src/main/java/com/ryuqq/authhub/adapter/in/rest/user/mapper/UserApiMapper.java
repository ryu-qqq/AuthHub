package com.ryuqq.authhub.adapter.in.rest.user.mapper;

import com.ryuqq.authhub.adapter.in.rest.user.dto.command.AssignUserRoleApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.CreateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserPasswordApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.command.UpdateUserStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.query.SearchUsersAdminApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.query.SearchUsersApiRequest;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.CreateUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserDetailApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserRoleApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserRoleSummaryApiResponse;
import com.ryuqq.authhub.adapter.in.rest.user.dto.response.UserSummaryApiResponse;
import com.ryuqq.authhub.application.user.dto.command.AssignUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.command.CreateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.DeleteUserCommand;
import com.ryuqq.authhub.application.user.dto.command.RevokeUserRoleCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserPasswordCommand;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserStatusCommand;
import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;
import com.ryuqq.authhub.application.user.dto.response.UserSummaryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * UserApiMapper - 사용자 API DTO 변환기
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
public class UserApiMapper {

    /**
     * CreateUserApiRequest → CreateUserCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateUserCommand toCommand(CreateUserApiRequest request) {
        return new CreateUserCommand(
                UUID.fromString(request.tenantId()),
                UUID.fromString(request.organizationId()),
                request.identifier(),
                request.password());
    }

    /**
     * UpdateUserApiRequest → UpdateUserCommand 변환
     *
     * @param userId 사용자 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateUserCommand toCommand(String userId, UpdateUserApiRequest request) {
        return new UpdateUserCommand(UUID.fromString(userId), request.identifier());
    }

    /**
     * UpdateUserStatusApiRequest → UpdateUserStatusCommand 변환
     *
     * @param userId 사용자 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateUserStatusCommand toStatusCommand(
            String userId, UpdateUserStatusApiRequest request) {
        return new UpdateUserStatusCommand(UUID.fromString(userId), request.status());
    }

    /**
     * UpdateUserPasswordApiRequest → UpdateUserPasswordCommand 변환
     *
     * @param userId 사용자 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateUserPasswordCommand toPasswordCommand(
            String userId, UpdateUserPasswordApiRequest request) {
        return new UpdateUserPasswordCommand(
                UUID.fromString(userId), request.currentPassword(), request.newPassword());
    }

    /**
     * userId → DeleteUserCommand 변환
     *
     * @param userId 사용자 ID (PathVariable)
     * @return Application Command DTO
     */
    public DeleteUserCommand toDeleteCommand(String userId) {
        return new DeleteUserCommand(UUID.fromString(userId));
    }

    /**
     * userId → GetUserQuery 변환
     *
     * @param userId 사용자 ID (PathVariable)
     * @return Application Query DTO
     */
    public GetUserQuery toGetQuery(String userId) {
        return new GetUserQuery(UUID.fromString(userId));
    }

    /**
     * SearchUsersApiRequest → SearchUsersQuery 변환
     *
     * @param request API 요청 DTO
     * @return Application Query DTO
     */
    public SearchUsersQuery toQuery(SearchUsersApiRequest request) {
        UUID tenantId = request.tenantId() != null ? UUID.fromString(request.tenantId()) : null;
        UUID organizationId =
                request.organizationId() != null ? UUID.fromString(request.organizationId()) : null;
        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null ? request.size() : 20;
        return SearchUsersQuery.of(
                tenantId, organizationId, request.identifier(), request.status(), page, size);
    }

    /**
     * UserResponse → CreateUserApiResponse 변환
     *
     * @param response 생성된 사용자 Response
     * @return API 응답 DTO
     */
    public CreateUserApiResponse toCreateResponse(UserResponse response) {
        return new CreateUserApiResponse(response.userId().toString());
    }

    /**
     * UserResponse → UserApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public UserApiResponse toApiResponse(UserResponse response) {
        return new UserApiResponse(
                response.userId().toString(),
                response.tenantId().toString(),
                response.organizationId().toString(),
                response.identifier(),
                response.status(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * UserResponse 목록 → UserApiResponse 목록 변환
     *
     * @param responses Application Response DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<UserApiResponse> toApiResponseList(List<UserResponse> responses) {
        return responses.stream().map(this::toApiResponse).toList();
    }

    // ===== UserRole 관련 변환 메서드 =====

    /**
     * AssignUserRoleApiRequest → AssignUserRoleCommand 변환
     *
     * @param userId 사용자 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public AssignUserRoleCommand toAssignRoleCommand(
            String userId, AssignUserRoleApiRequest request) {
        return new AssignUserRoleCommand(UUID.fromString(userId), request.roleId());
    }

    /**
     * userId + roleId → RevokeUserRoleCommand 변환
     *
     * @param userId 사용자 ID (PathVariable)
     * @param roleId 역할 ID (PathVariable)
     * @return Application Command DTO
     */
    public RevokeUserRoleCommand toRevokeRoleCommand(String userId, String roleId) {
        return new RevokeUserRoleCommand(UUID.fromString(userId), UUID.fromString(roleId));
    }

    /**
     * UserRoleResponse → UserRoleApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public UserRoleApiResponse toUserRoleApiResponse(UserRoleResponse response) {
        return new UserRoleApiResponse(response.userId(), response.roleId(), response.assignedAt());
    }

    /**
     * UserRoleResponse 목록 → UserRoleApiResponse 목록 변환
     *
     * @param responses Application Response DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<UserRoleApiResponse> toUserRoleApiResponseList(List<UserRoleResponse> responses) {
        return responses.stream().map(this::toUserRoleApiResponse).toList();
    }

    // ===== Admin 전용 변환 메서드 =====

    /**
     * SearchUsersAdminApiRequest → SearchUsersQuery 변환 (Admin용)
     *
     * @param request Admin용 API 요청 DTO
     * @return Application Query DTO (확장 필터 포함)
     */
    public SearchUsersQuery toAdminQuery(SearchUsersAdminApiRequest request) {
        UUID tenantId = request.tenantId() != null ? UUID.fromString(request.tenantId()) : null;
        UUID organizationId =
                request.organizationId() != null ? UUID.fromString(request.organizationId()) : null;
        UUID roleId = request.roleId() != null ? UUID.fromString(request.roleId()) : null;

        int page = request.page() != null ? request.page() : 0;
        int size = request.size() != null ? request.size() : SearchUsersQuery.DEFAULT_PAGE_SIZE;
        String sortBy =
                request.sortBy() != null ? request.sortBy() : SearchUsersQuery.DEFAULT_SORT_BY;
        String sortDirection =
                request.sortDirection() != null
                        ? request.sortDirection()
                        : SearchUsersQuery.DEFAULT_SORT_DIRECTION;

        return new SearchUsersQuery(
                tenantId,
                organizationId,
                request.identifier(),
                request.status(),
                roleId,
                request.createdFrom(),
                request.createdTo(),
                sortBy,
                sortDirection,
                page,
                size);
    }

    /**
     * UserSummaryResponse → UserSummaryApiResponse 변환 (Admin 목록용)
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public UserSummaryApiResponse toSummaryApiResponse(UserSummaryResponse response) {
        return new UserSummaryApiResponse(
                response.userId().toString(),
                response.tenantId().toString(),
                response.tenantName(),
                response.organizationId() != null ? response.organizationId().toString() : null,
                response.organizationName(),
                response.identifier(),
                response.status(),
                response.roleCount(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * UserDetailResponse → UserDetailApiResponse 변환 (Admin 상세용)
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public UserDetailApiResponse toDetailApiResponse(UserDetailResponse response) {
        List<UserRoleSummaryApiResponse> roles =
                response.roles().stream().map(this::toRoleSummaryApiResponse).toList();

        return new UserDetailApiResponse(
                response.userId().toString(),
                response.tenantId().toString(),
                response.tenantName(),
                response.organizationId() != null ? response.organizationId().toString() : null,
                response.organizationName(),
                response.identifier(),
                response.status(),
                roles,
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * UserDetailResponse.UserRoleSummary → UserRoleSummaryApiResponse 변환
     *
     * @param roleSummary Application 역할 요약 DTO
     * @return API 역할 요약 DTO
     */
    private UserRoleSummaryApiResponse toRoleSummaryApiResponse(
            UserDetailResponse.UserRoleSummary roleSummary) {
        return new UserRoleSummaryApiResponse(
                roleSummary.roleId().toString(),
                roleSummary.name(),
                roleSummary.description(),
                roleSummary.scope(),
                roleSummary.type());
    }
}
