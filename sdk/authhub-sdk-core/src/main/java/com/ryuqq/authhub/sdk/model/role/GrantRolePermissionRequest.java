package com.ryuqq.authhub.sdk.model.role;

import java.util.List;
import java.util.Objects;

/** 역할 권한 부여 요청. */
public record GrantRolePermissionRequest(List<Long> permissionIds) {

    public GrantRolePermissionRequest {
        Objects.requireNonNull(permissionIds, "permissionIds must not be null");
        if (permissionIds.isEmpty()) {
            throw new IllegalArgumentException("permissionIds must not be empty");
        }
        permissionIds = List.copyOf(permissionIds);
    }
}
