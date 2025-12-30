package com.ryuqq.authhub.sdk.model.user;

import java.util.Objects;

/** 사용자 역할 할당 요청. */
public record AssignUserRoleRequest(String roleId) {

    public AssignUserRoleRequest {
        Objects.requireNonNull(roleId, "roleId must not be null");
    }
}
