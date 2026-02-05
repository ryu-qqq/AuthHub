package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesRequest;
import com.ryuqq.authhub.sdk.model.user.CreateUserWithRolesResponse;

/**
 * 사용자 관련 Internal API 인터페이스.
 *
 * <p>사용자 생성 + 역할 할당 기능을 제공합니다.
 */
public interface UserApi {

    /**
     * 사용자를 생성하고 선택적으로 역할을 할당합니다.
     *
     * <p>serviceCode와 roleNames가 제공되면 SERVICE scope Role을 자동으로 할당합니다.
     *
     * @param request 사용자 생성 + 역할 할당 요청
     * @return 생성된 userId, assignedRoleCount
     */
    ApiResponse<CreateUserWithRolesResponse> createUserWithRoles(
            CreateUserWithRolesRequest request);
}
