package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;

import java.util.Map;
import java.util.UUID;

/**
 * RoleNotFoundException - 역할을 찾을 수 없을 때 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class RoleNotFoundException extends DomainException {

    public RoleNotFoundException(UUID roleId) {
        super(RoleErrorCode.ROLE_NOT_FOUND, Map.of("roleId", roleId));
    }

    public RoleNotFoundException(RoleId roleId) {
        this(roleId.value());
    }

    public RoleNotFoundException(String roleName) {
        super(RoleErrorCode.ROLE_NOT_FOUND, Map.of("roleName", roleName));
    }
}
