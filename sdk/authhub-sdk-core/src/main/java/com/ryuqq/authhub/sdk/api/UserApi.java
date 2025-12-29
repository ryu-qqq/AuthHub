package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.common.PageResponse;
import com.ryuqq.authhub.sdk.model.user.AssignUserRoleRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserResponse;
import com.ryuqq.authhub.sdk.model.user.UpdateUserPasswordRequest;
import com.ryuqq.authhub.sdk.model.user.UpdateUserRequest;
import com.ryuqq.authhub.sdk.model.user.UpdateUserStatusRequest;
import com.ryuqq.authhub.sdk.model.user.UserResponse;
import com.ryuqq.authhub.sdk.model.user.UserSummaryResponse;
import java.util.Map;

/** User 관련 API 인터페이스. 사용자 생성, 조회, 수정, 상태 변경, 역할 할당 등의 기능을 제공합니다. */
public interface UserApi {

    /**
     * 새로운 사용자를 생성합니다.
     *
     * @param request 사용자 생성 요청
     * @return 생성된 사용자 ID를 포함한 응답
     */
    ApiResponse<CreateUserResponse> create(CreateUserRequest request);

    /**
     * 사용자 ID로 사용자를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    ApiResponse<UserResponse> getById(Long userId);

    /**
     * 사용자 목록을 검색합니다.
     *
     * @param queryParams 검색 조건 (tenantId, organizationId, identifier, status, page, size)
     * @return 사용자 목록
     */
    ApiResponse<PageResponse<UserResponse>> search(Map<String, Object> queryParams);

    /**
     * Admin용 사용자 목록을 검색합니다.
     *
     * @param queryParams 검색 조건
     * @return Admin용 사용자 목록 (Summary 포함)
     */
    ApiResponse<PageResponse<UserSummaryResponse>> searchAdmin(Map<String, Object> queryParams);

    /**
     * 사용자 정보를 수정합니다.
     *
     * @param userId 사용자 ID
     * @param request 수정 요청
     * @return 수정된 사용자 정보
     */
    ApiResponse<UserResponse> update(Long userId, UpdateUserRequest request);

    /**
     * 사용자 상태를 변경합니다.
     *
     * @param userId 사용자 ID
     * @param request 상태 변경 요청
     * @return 변경된 사용자 정보
     */
    ApiResponse<UserResponse> updateStatus(Long userId, UpdateUserStatusRequest request);

    /**
     * 사용자 비밀번호를 변경합니다.
     *
     * @param userId 사용자 ID
     * @param request 비밀번호 변경 요청
     */
    void updatePassword(Long userId, UpdateUserPasswordRequest request);

    /**
     * 사용자에게 역할을 할당합니다.
     *
     * @param userId 사용자 ID
     * @param request 역할 할당 요청
     */
    void assignRole(Long userId, AssignUserRoleRequest request);

    /**
     * 사용자에서 역할을 해제합니다.
     *
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     */
    void unassignRole(Long userId, Long roleId);

    /**
     * 사용자를 삭제(비활성화)합니다.
     *
     * @param userId 사용자 ID
     */
    void delete(Long userId);
}
