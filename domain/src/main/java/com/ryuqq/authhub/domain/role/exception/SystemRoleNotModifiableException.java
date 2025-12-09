package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;

import java.util.Map;
import java.util.UUID;

/**
 * SystemRoleNotModifiableException - 시스템 역할 수정 시도 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class SystemRoleNotModifiableException extends DomainException {

    public SystemRoleNotModifiableException(UUID roleId) {
        super(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE, Map.of("roleId", roleId));
    }

    public SystemRoleNotModifiableException(RoleId roleId) {
        this(roleId.value());
    }

    public SystemRoleNotModifiableException(String roleName) {
        super(RoleErrorCode.SYSTEM_ROLE_NOT_MODIFIABLE, Map.of("roleName", roleName));
    }
}
