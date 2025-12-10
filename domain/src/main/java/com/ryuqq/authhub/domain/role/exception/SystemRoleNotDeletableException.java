package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import java.util.Map;
import java.util.UUID;

/**
 * SystemRoleNotDeletableException - 시스템 역할 삭제 시도 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class SystemRoleNotDeletableException extends DomainException {

    public SystemRoleNotDeletableException(UUID roleId) {
        super(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE, Map.of("roleId", roleId));
    }

    public SystemRoleNotDeletableException(RoleId roleId) {
        this(roleId.value());
    }

    public SystemRoleNotDeletableException(String roleName) {
        super(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE, Map.of("roleName", roleName));
    }
}
