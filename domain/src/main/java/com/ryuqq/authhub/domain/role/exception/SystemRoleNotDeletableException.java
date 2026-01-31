package com.ryuqq.authhub.domain.role.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import java.util.Map;

/**
 * SystemRoleNotDeletableException - 시스템 역할 삭제 시도 시 발생하는 예외
 *
 * @author development-team
 * @since 1.0.0
 */
public class SystemRoleNotDeletableException extends DomainException {

    public SystemRoleNotDeletableException(String roleName) {
        super(RoleErrorCode.SYSTEM_ROLE_NOT_DELETABLE, Map.of("roleName", roleName));
    }

    public SystemRoleNotDeletableException(RoleName roleName) {
        this(roleName.value());
    }
}
